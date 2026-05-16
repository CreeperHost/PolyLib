package net.creeperhost.polylib.client.screen.chunkmap;

import com.mojang.blaze3d.platform.InputConstants;
import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapClient;
import net.creeperhost.polylib.chunkmap.common.network.PolyChunkMapStartPayload;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Holds the PolyLib chunk-map {@link KeyMapping} and the per-tick open logic.
 *
 * <p>The keybind is <b>unbound by default</b> — it shows up under the "PolyLib"
 * category in Controls so the player can assign their own key.
 *
 * <p>Loaders must register {@link #OPEN_CHUNK_MAP} with their respective key-binding
 * systems at init time:
 * <ul>
 *   <li>NeoForge — {@code RegisterKeyMappingsEvent}</li>
 *   <li>Fabric — {@code KeyBindingHelper.registerKeyBinding(...)}</li>
 * </ul>
 *
 * <p>{@link #tick()} is called each client tick from
 * {@link net.creeperhost.polylib.init.InternalEventListenerClient}.
 */
public final class PolyChunkMapKeys
{
    /** Controls category shown in the vanilla key-bindings screen. */
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(Constants.MOD_ID, "main"));

    /**
     * Open Chunk Map — unbound by default.
     * Translation key: {@code key.polylib.open_chunk_map}
     */
    public static final KeyMapping OPEN_CHUNK_MAP = new KeyMapping(
            "key.polylib.open_chunk_map",
            InputConstants.UNKNOWN.getValue(),
            CATEGORY);

    private PolyChunkMapKeys() {}

    /**
     * Called every client tick. Opens {@link PolyChunkMapScreen} when the
     * keybind is consumed and no other screen is open.
     */
    public static void tick()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.screen != null) return;
        while (OPEN_CHUNK_MAP.consumeClick())
        {
            open(mc);
        }
    }

    public static void open(Minecraft mc)
    {
        if (PolyChunkMapClient.isPermitted())
        {
            // Request chunk data for the current dimension
            Services.NETWORK.sendToServer(
                    new PolyChunkMapStartPayload(List.of(mc.level.dimension())));
        }
        mc.setScreen(new PolyChunkMapScreen());
    }
}
