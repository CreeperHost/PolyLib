package net.creeperhost.polylib.network.packets;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server-to-client block-entity data-manager payload.
 *
 * @param data encoded tile data packet bytes
 */
public record TileDataClientPayload(byte[] data) implements CustomPacketPayload {
    /**
     * Payload type id for server-to-client tile data packets.
     */
    public static final Type<TileDataClientPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "tile_client"));

    /**
     * Codec that stores the encoded payload length followed by the payload bytes.
     */
    public static final StreamCodec<ByteBuf, TileDataClientPayload> CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeInt(p.data.length); buf.writeBytes(p.data); },
            buf -> { byte[] d = new byte[buf.readInt()]; buf.readBytes(d); return new TileDataClientPayload(d); });

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
