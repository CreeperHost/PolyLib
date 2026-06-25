package net.creeperhost.polylib.network.packets;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server-to-client container payload.
 *
 * @param data encoded container packet bytes
 */
public record ContainerClientPayload(byte[] data) implements CustomPacketPayload {
    /**
     * Payload type id for server-to-client container packets.
     */
    public static final Type<ContainerClientPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "container_client"));

    /**
     * Codec that stores the encoded payload length followed by the payload bytes.
     */
    public static final StreamCodec<ByteBuf, ContainerClientPayload> CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeInt(p.data.length); buf.writeBytes(p.data); },
            buf -> { byte[] d = new byte[buf.readInt()]; buf.readBytes(d); return new ContainerClientPayload(d); });

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
