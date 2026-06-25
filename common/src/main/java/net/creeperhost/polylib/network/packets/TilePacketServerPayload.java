package net.creeperhost.polylib.network.packets;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Client-to-server custom block-entity payload.
 *
 * @param data encoded tile packet bytes
 */
public record TilePacketServerPayload(byte[] data) implements CustomPacketPayload {
    /**
     * Payload type id for client-to-server tile packets.
     */
    public static final Type<TilePacketServerPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tile_packet_server"));

    /**
     * Codec that stores the encoded payload length followed by the payload bytes.
     */
    public static final StreamCodec<ByteBuf, TilePacketServerPayload> CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeInt(p.data.length); buf.writeBytes(p.data); },
            buf -> { byte[] d = new byte[buf.readInt()]; buf.readBytes(d); return new TilePacketServerPayload(d); });

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
