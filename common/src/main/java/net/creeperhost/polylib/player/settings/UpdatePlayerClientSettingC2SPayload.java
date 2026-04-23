package net.creeperhost.polylib.player.settings;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * C2S: client sends an updated PlayerClientSetting value to the server.
 */
public record UpdatePlayerClientSettingC2SPayload(Identifier typeId, byte[] data)
        implements CustomPacketPayload
{
    public static final Type<UpdatePlayerClientSettingC2SPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "update_player_setting"));

    public static final StreamCodec<ByteBuf, UpdatePlayerClientSettingC2SPayload> CODEC = StreamCodec.of(
            (buf, payload) ->
            {
                byte[] idBytes = payload.typeId().toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                buf.writeInt(idBytes.length);
                buf.writeBytes(idBytes);
                buf.writeInt(payload.data().length);
                buf.writeBytes(payload.data());
            },
            buf ->
            {
                int idLen = buf.readInt();
                byte[] idBytes = new byte[idLen];
                buf.readBytes(idBytes);
                Identifier typeId = Identifier.parse(new String(idBytes, java.nio.charset.StandardCharsets.UTF_8));
                int dataLen = buf.readInt();
                byte[] data = new byte[dataLen];
                buf.readBytes(data);
                return new UpdatePlayerClientSettingC2SPayload(typeId, data);
            });

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
