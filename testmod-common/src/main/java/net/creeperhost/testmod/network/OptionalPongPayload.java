package net.creeperhost.testmod.network;

import net.creeperhost.testmod.TestModCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Optional test payload returned from the server to the client.
 *
 * @param value value received in the matching ping
 */
public record OptionalPongPayload(int value) implements CustomPacketPayload
{
    /** Payload type for the optional pong. */
    public static final Type<OptionalPongPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "optional_pong"));

    /** Network codec for the optional pong value. */
    public static final StreamCodec<RegistryFriendlyByteBuf, OptionalPongPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            OptionalPongPayload::value,
            OptionalPongPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
