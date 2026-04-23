package net.creeperhost.polylib.player.serverdata;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;

/**
 * S2C packet: server pushes an updated server-authoritative data value to the owning player.
 * Sent only to the player whose data changed — never to other players.
 */
public record SyncPlayerServerDataS2CPayload(String typeId, byte[] data)
        implements CustomPacketPayload
{
    public static final Type<SyncPlayerServerDataS2CPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "sync_server_data"));

    public static final StreamCodec<ByteBuf, SyncPlayerServerDataS2CPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                byte[] idBytes = payload.typeId().getBytes(StandardCharsets.UTF_8);
                buf.writeInt(idBytes.length);
                buf.writeBytes(idBytes);
                buf.writeInt(payload.data().length);
                buf.writeBytes(payload.data());
            },
            buf -> {
                int idLen = buf.readInt();
                byte[] idBytes = new byte[idLen];
                buf.readBytes(idBytes);
                String typeId = new String(idBytes, StandardCharsets.UTF_8);
                int dataLen = buf.readInt();
                byte[] data = new byte[dataLen];
                buf.readBytes(data);
                return new SyncPlayerServerDataS2CPayload(typeId, data);
            });

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
