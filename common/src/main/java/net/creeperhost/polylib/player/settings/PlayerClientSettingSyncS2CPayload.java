package net.creeperhost.polylib.player.settings;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * S2C: server broadcasts a PlayerClientSetting value update to clients per the type's {@link BroadcastScope}.
 */
public record PlayerClientSettingSyncS2CPayload(UUID playerUUID, Identifier typeId, byte[] data)
        implements CustomPacketPayload
{
    public static final Type<PlayerClientSettingSyncS2CPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "player_setting_sync"));

    public static final StreamCodec<ByteBuf, PlayerClientSettingSyncS2CPayload> CODEC = StreamCodec.of(
            (buf, payload) ->
            {
                buf.writeLong(payload.playerUUID().getMostSignificantBits());
                buf.writeLong(payload.playerUUID().getLeastSignificantBits());
                byte[] idBytes = payload.typeId().toString().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(idBytes.length);
                buf.writeBytes(idBytes);
                buf.writeInt(payload.data().length);
                buf.writeBytes(payload.data());
            },
            buf ->
            {
                long msb = buf.readLong();
                long lsb = buf.readLong();
                UUID playerUUID = new UUID(msb, lsb);
                int idLen = buf.readInt();
                byte[] idBytes = new byte[idLen];
                buf.readBytes(idBytes);
                Identifier typeId = Identifier.parse(new String(idBytes, StandardCharsets.UTF_8));
                int dataLen = buf.readInt();
                byte[] data = new byte[dataLen];
                buf.readBytes(data);
                return new PlayerClientSettingSyncS2CPayload(playerUUID, typeId, data);
            });

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
