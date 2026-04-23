package net.creeperhost.polylib.accessibility;

import io.netty.buffer.ByteBuf;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * C2S packet: client sends its current accessibility preference values to the server.
 * Only preferences with effective policy {@link AccessibilityPolicy#PLAYER_OVERRIDES_SERVER}
 * are included.
 */
public record AccessibilityPrefsC2SPayload(Map<String, Boolean> values) implements CustomPacketPayload
{
    public static final Type<AccessibilityPrefsC2SPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "accessibility_prefs"));

    public static final StreamCodec<ByteBuf, AccessibilityPrefsC2SPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.values().size());
                payload.values().forEach((key, value) -> {
                    byte[] keyBytes = key.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    buf.writeInt(keyBytes.length);
                    buf.writeBytes(keyBytes);
                    buf.writeBoolean(value);
                });
            },
            buf -> {
                int size = buf.readInt();
                Map<String, Boolean> map = new HashMap<>(size);
                for (int i = 0; i < size; i++)
                {
                    int keyLen = buf.readInt();
                    byte[] keyBytes = new byte[keyLen];
                    buf.readBytes(keyBytes);
                    String key = new String(keyBytes, java.nio.charset.StandardCharsets.UTF_8);
                    boolean value = buf.readBoolean();
                    map.put(key, value);
                }
                return new AccessibilityPrefsC2SPayload(map);
            });

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
