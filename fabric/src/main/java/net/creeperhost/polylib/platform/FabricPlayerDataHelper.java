package net.creeperhost.polylib.platform;

import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.platform.services.IPlayerDataHelper;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataRegistry;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataStore;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsStore;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Fabric implementation of {@link IPlayerDataHelper}.
 * Each {@link PlayerClientSettingsType} gets its own {@link AttachmentType AttachmentType&lt;byte[]&gt;}
 * registered with {@link AttachmentRegistry}, persisted via {@code Codec.BYTE_BUFFER.xmap(...)}.
 * The {@code copyOnDeath()} flag mirrors {@link PlayerClientSettingsType#copyOnDeath()}.
 */
public class FabricPlayerDataHelper implements IPlayerDataHelper
{
    /** Codec<byte[]> using DFU BYTE_BUFFER — compatible with DFU 9. */
    private static final Codec<byte[]> BYTE_ARRAY_CODEC = Codec.BYTE_BUFFER.xmap(
            buf -> {
                if (buf.hasArray()) return buf.array();
                byte[] bytes = new byte[buf.capacity()];
                buf.get(bytes);
                return bytes;
            },
            ByteBuffer::wrap);

    private final Map<String, AttachmentType<byte[]>> attachmentsByTypeId = new LinkedHashMap<>();

    @Override
    public void registerType(PlayerClientSettingsType<?> type)
    {
        // Convert the namespaced type id (e.g. "discrafthonored:appearance") to a valid
        // Identifier path by replacing ':' → '/' and any non-[a-z0-9_.-/] chars → '_'
        String sanitized = type.id().replace(':', '/').replaceAll("[^a-z0-9_.\\-/]", "_");
        Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "settings/" + sanitized);

        AttachmentType<byte[]> attachment;
        if (type.copyOnDeath())
        {
            attachment = AttachmentRegistry.create(id,
                    builder -> builder.persistent(BYTE_ARRAY_CODEC).copyOnDeath());
        }
        else
        {
            attachment = AttachmentRegistry.create(id,
                    builder -> builder.persistent(BYTE_ARRAY_CODEC));
        }
        attachmentsByTypeId.put(type.id(), attachment);
    }

    @Override
    public void loadAll(UUID playerUUID, ServerPlayer player, PlayerClientSettingsStore store)
    {
        for (Map.Entry<String, AttachmentType<byte[]>> entry : attachmentsByTypeId.entrySet())
        {
            byte[] bytes = player.getAttached(entry.getValue());
            if (bytes != null && bytes.length > 0)
            {
                PlayerClientSettingsType<?> type = net.creeperhost.polylib.player.settings.PlayerClientSettingsRegistry
                        .byId(entry.getKey()).orElse(null);
                if (type != null)
                    loadTyped(type, bytes, player, store);
            }
        }
    }

    @Override
    public void saveAll(UUID playerUUID, ServerPlayer player, PlayerClientSettingsStore store)
    {
        for (PlayerClientSettingsType<?> dirty : store.getDirty())
        {
            AttachmentType<byte[]> attachment = attachmentsByTypeId.get(dirty.id());
            if (attachment != null)
            {
                byte[] bytes = encodeTyped(dirty, player, store);
                player.setAttached(attachment, bytes);
            }
        }
        store.clearDirty();
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadTyped(PlayerClientSettingsType<T> type,
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
    private static <T> byte[] encodeTyped(PlayerClientSettingsType<T> type,
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

    private final Map<String, AttachmentType<?>> serverDataAttachments = new LinkedHashMap<>();

    @Override
    public void registerServerDataType(PlayerServerDataType<?> type)
    {
        String sanitized = type.id().replace(':', '/').replaceAll("[^a-z0-9_.\\-/]", "_");
        Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sdata/" + sanitized);
        AttachmentType<?> attachment = createServerDataAttachment(type, id);
        serverDataAttachments.put(type.id(), attachment);
    }

    @SuppressWarnings("unchecked")
    private static <T> AttachmentType<T> createServerDataAttachment(PlayerServerDataType<T> type, Identifier id)
    {
        return type.copyOnDeath()
                ? AttachmentRegistry.create(id, builder -> builder.persistent(type.nbtCodec()).copyOnDeath())
                : AttachmentRegistry.create(id, builder -> builder.persistent(type.nbtCodec()));
    }

    @Override
    public void loadServerData(UUID playerUUID, ServerPlayer player, PlayerServerDataStore store)
    {
        for (Map.Entry<String, AttachmentType<?>> entry : serverDataAttachments.entrySet())
        {
            loadServerDataTyped(entry.getKey(), entry.getValue(), player, store);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void loadServerDataTyped(String id,
                                                 AttachmentType<T> attachment,
                                                 ServerPlayer player,
                                                 PlayerServerDataStore store)
    {
        T value = player.getAttached(attachment);
        if (value != null)
        {
            PlayerServerDataType<T> type = (PlayerServerDataType<T>) PlayerServerDataRegistry.byId(id).orElse(null);
            if (type != null) store.load(type, value);
        }
    }

    @Override
    public void saveServerData(UUID playerUUID, ServerPlayer player, PlayerServerDataStore store)
    {
        for (PlayerServerDataType<?> dirty : store.getDirty())
        {
            saveServerDataTyped(dirty, player, store);
        }
        store.clearDirty();
    }

    @SuppressWarnings("unchecked")
    private <T> void saveServerDataTyped(PlayerServerDataType<T> type,
                                          ServerPlayer player,
                                          PlayerServerDataStore store)
    {
        AttachmentType<T> attachment = (AttachmentType<T>) serverDataAttachments.get(type.id());
        if (attachment != null)
            player.setAttached(attachment, store.get(type));
    }
}

