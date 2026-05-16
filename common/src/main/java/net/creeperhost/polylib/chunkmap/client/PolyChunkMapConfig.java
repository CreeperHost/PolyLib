package net.creeperhost.polylib.chunkmap.client;

import blue.endless.jankson.Comment;
import net.creeperhost.polylib.config.ConfigData;

public class PolyChunkMapConfig extends ConfigData {

    @Comment("The rendering mode for the chunk map (STATUS or TICKETS)")
    public RenderMode renderMode = RenderMode.STATUS;

    @Comment("When to display the minimap HUD overlay (ALWAYS, F3_ONLY, or OFF)")
    public MinimapDisplayMode minimapDisplayMode = MinimapDisplayMode.F3_ONLY;

    @Comment("The X coordinate anchor for the minimap HUD")
    public int minimapAnchorX = 10;

    @Comment("The Y coordinate anchor for the minimap HUD")
    public int minimapAnchorY = 10;

    @Comment("The zoom level for the minimap HUD")
    public double minimapZoom = 0.5;

    @Comment("The size of the minimap HUD (width and height)")
    public int minimapSize = 200;

    @Comment("How many ticks an unloaded chunk remains visible before fading out completely")
    public int retentionTicks = 100;

    public enum RenderMode {
        STATUS, TICKETS
    }

    public enum MinimapDisplayMode {
        ALWAYS, F3_ONLY, OFF
    }
}
