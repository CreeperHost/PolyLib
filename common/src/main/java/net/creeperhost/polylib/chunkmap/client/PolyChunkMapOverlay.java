package net.creeperhost.polylib.chunkmap.client;

import net.creeperhost.polylib.PolyLibClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class PolyChunkMapOverlay {

    public static void init() {
        net.creeperhost.polylib.event.events.client.PolyGuiEvents.RENDER_GUI_POST.register(PolyChunkMapOverlay::onRenderGui);
    }

    private static void onRenderGui(GuiGraphicsExtractor guiGraphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (mc.getDebugOverlay().showDebugScreen()) {
            // we'll handle this soon
        }
        
        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config == null) return;

        boolean shouldRender = false;
        if (config.minimapDisplayMode == PolyChunkMapConfig.MinimapDisplayMode.ALWAYS) {
            shouldRender = true;
        } else if (config.minimapDisplayMode == PolyChunkMapConfig.MinimapDisplayMode.F3_ONLY && mc.getDebugOverlay().showDebugScreen()) {
            shouldRender = true;
        }

        // Do not render if the F3 debug profiler is covering the screen
        if (mc.getDebugOverlay().showDebugScreen() && mc.getDebugOverlay().showProfilerChart()) {
            shouldRender = false;
        }

        if (!shouldRender) return;

        // Render the minimap!
        renderMinimap(guiGraphics, mc, config.minimapAnchorX, config.minimapAnchorY);
    }

    public static void renderMinimap(GuiGraphicsExtractor gfx, Minecraft mc, int x, int y) {
        if (mc.level == null || mc.player == null) return;
        
        java.util.Map<Long, net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData> chunks = net.creeperhost.polylib.chunkmap.client.PolyChunkMapClient.getChunks(mc.level.dimension());
        if (chunks == null || chunks.isEmpty()) return;

        double cameraX = mc.player.getX() / 16.0;
        double cameraZ = mc.player.getZ() / 16.0;
        double zoom = PolyLibClient.chunkMapConfig != null ? PolyLibClient.chunkMapConfig.minimapZoom : 2.0;

        int size = PolyLibClient.chunkMapConfig != null ? PolyLibClient.chunkMapConfig.minimapSize : 100;
        int x1 = x;
        int y1 = y;
        int x2 = x + size;
        int y2 = y + size;

        // Draw background
        gfx.fill(x1, y1, x2, y2, 0xAA000000);

        PolyChunkGridRenderer.renderGrid(gfx, chunks, zoom, cameraX, cameraZ, x1, y1, x2, y2, -1, -1, false);
        
        // Draw border
        gfx.fill(x1 - 1, y1 - 1, x2 + 1, y1, 0xFFFFFFFF);
        gfx.fill(x1 - 1, y2, x2 + 1, y2 + 1, 0xFFFFFFFF);
        gfx.fill(x1 - 1, y1, x1, y2, 0xFFFFFFFF);
        gfx.fill(x2, y1, x2 + 1, y2, 0xFFFFFFFF);
    }
}
