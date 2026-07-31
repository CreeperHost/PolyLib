package net.creeperhost.testmod.network;

import net.creeperhost.testmod.TestModCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Optional test payload sent from the client to the server.
 *
 * @param value value echoed by the server
 */
public record OptionalPingPayload(int value) implements CustomPacketPayload
{
    /** Payload type for the optional ping. */
    public static final Type<OptionalPingPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "optional_ping"));

    /** Network codec for the optional ping value. */
    public static final StreamCodec<RegistryFriendlyByteBuf, OptionalPingPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            OptionalPingPayload::value,
            OptionalPingPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
