package net.creeperhost.polylib.network.packets;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ContainerServerPayload(byte[] data) implements CustomPacketPayload {
    public static final Type<ContainerServerPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "container_packet_server"));
    public static final StreamCodec<ByteBuf, ContainerServerPayload> CODEC = StreamCodec.of(
            (buf, p) -> { buf.writeInt(p.data.length); buf.writeBytes(p.data); },
            buf -> { byte[] d = new byte[buf.readInt()]; buf.readBytes(d); return new ContainerServerPayload(d); });

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
