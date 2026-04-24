package net.creeperhost.polylib.platform;

import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.platform.services.IPlayerDataHelper;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataRegistry;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataStore;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsStore;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * NeoForge implementation of {@link IPlayerDataHelper}.
 *
 * <p><b>Client settings</b> (StreamCodec-based): stored as {@code byte[]} in
 * {@code player.getPersistentData()} under key {@code polylib_settings.<typeId>}.
 *
 * <p><b>Server data</b> (DFU Codec-based): stored as a wrapped NBT tag in
 * {@code player.getPersistentData()} under key {@code polylib_sdata.<typeId>}.
 * The tag is wrapped in a CompoundTag ({@code {v: <tag>}}) to handle all NBT element types.
 *
 * <p>Both are saved with the player NBT automatically and loaded on join.
 */
public class NeoForgePlayerDataHelper implements IPlayerDataHelper
{
    private final Map<Identifier, PlayerClientSettingsType<?>> registeredTypes = new LinkedHashMap<>();
    private static final String SETTINGS_PREFIX = "polylib_settings.";

    @Override
    public void registerType(PlayerClientSettingsType<?> type)
    {
        registeredTypes.put(type.id(), type);
    }

    @Override
    public void loadAll(UUID playerUUID, ServerPlayer player, PlayerClientSettingsStore store)
    {
        CompoundTag persistent = player.getPersistentData();
        for (PlayerClientSettingsType<?> type : registeredTypes.values())
        {
            String key = SETTINGS_PREFIX + type.id().toString();
            if (persistent.contains(key))
            {
                byte[] bytes = persistent.getByteArray(key).orElse(null);
                if (bytes == null || bytes.length == 0) continue;
                loadClientTyped(type, bytes, player, store);
            }
        }
    }

    @Override
    public void saveAll(UUID playerUUID, ServerPlayer player, PlayerClientSettingsStore store)
    {
        CompoundTag persistent = player.getPersistentData();
        for (PlayerClientSettingsType<?> dirty : store.getDirty())
        {
            String key = SETTINGS_PREFIX + dirty.id().toString();
            byte[] bytes = encodeClientTyped(dirty, player, store);
            persistent.put(key, new ByteArrayTag(bytes));
        }
        store.clearDirty();
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadClientTyped(PlayerClientSettingsType<T> type,
                                             byte[] bytes,
                                             ServerPlayer player,
                                             PlayerClientSettingsStore store)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.wrappedBuffer(bytes),
                player.level().registryAccess());
        T value = type.codec().decode(buf);
        store.load(type, value);
    }

    @SuppressWarnings("unchecked")
    private static <T> byte[] encodeClientTyped(PlayerClientSettingsType<T> type,
                                                 ServerPlayer player,
                                                 PlayerClientSettingsStore store)
    {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                Unpooled.buffer(),
                player.level().registryAccess());
        type.codec().encode(buf, store.get(type));
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();
        return data;
    }

    private final Map<Identifier, PlayerServerDataType<?>> registeredServerTypes = new LinkedHashMap<>();
    private static final String SDATA_PREFIX = "polylib_sdata.";

    @Override
    public void registerServerDataType(PlayerServerDataType<?> type)
    {
        registeredServerTypes.put(type.id(), type);
    }

    @Override
    public void loadServerData(UUID playerUUID, ServerPlayer player, PlayerServerDataStore store)
    {
        CompoundTag persistent = player.getPersistentData();
        for (PlayerServerDataType<?> type : registeredServerTypes.values())
        {
            String key = SDATA_PREFIX + type.id().toString();
            Tag raw = persistent.get(key);
            if (raw instanceof CompoundTag wrapper)
            {
                Tag inner = wrapper.get("v");
                if (inner != null)
                    loadServerTyped(type, inner, store);
            }
        }
    }

    @Override
    public void saveServerData(UUID playerUUID, ServerPlayer player, PlayerServerDataStore store)
    {
        CompoundTag persistent = player.getPersistentData();
        for (PlayerServerDataType<?> dirty : store.getDirty())
        {
            String key = SDATA_PREFIX + dirty.id().toString();
            saveServerTyped(dirty, persistent, key, store);
        }
        store.clearDirty();
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadServerTyped(PlayerServerDataType<T> type,
                                             Tag inner,
                                             PlayerServerDataStore store)
    {
        type.nbtCodec().parse(NbtOps.INSTANCE, inner)
                .result()
                .ifPresent(value -> store.load(type, value));
    }

    @SuppressWarnings("unchecked")
    private static <T> void saveServerTyped(PlayerServerDataType<T> type,
                                             CompoundTag persistent,
                                             String key,
                                             PlayerServerDataStore store)
    {
        type.nbtCodec().encodeStart(NbtOps.INSTANCE, store.get(type))
                .result()
                .ifPresent(tag -> {
                    CompoundTag wrapper = new CompoundTag();
                    wrapper.put("v", tag);
                    persistent.put(key, wrapper);
                });
    }

    @Override
    public <T> void loadServerDataForType(UUID playerUUID, ServerPlayer player,
                                           PlayerServerDataStore store, PlayerServerDataType<T> type)
    {
        String key = SDATA_PREFIX + type.id().toString();
        Tag raw = player.getPersistentData().get(key);
        if (raw instanceof CompoundTag wrapper)
        {
            Tag inner = wrapper.get("v");
            if (inner != null)
                loadServerTyped(type, inner, store);
        }
    }
}

