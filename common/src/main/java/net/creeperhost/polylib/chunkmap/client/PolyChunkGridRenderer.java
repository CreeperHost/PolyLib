package net.creeperhost.polylib.chunkmap.client;

import net.creeperhost.polylib.PolyLibClient;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.client.screen.chunkmap.PolyChunkMapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PolyChunkGridRenderer {

    // Store recently unloaded chunks here for fade-out effects
    public static final Map<Long, FadingChunk> fadingChunks = new HashMap<>();

    public static class FadingChunk {
        public PolyChunkMapData data;
        public long unloadTick;

        public FadingChunk(PolyChunkMapData data, long unloadTick) {
            this.data = data;
            this.unloadTick = unloadTick;
        }
    }

    public static @Nullable PolyChunkMapData renderGrid(GuiGraphicsExtractor gfx, Map<Long, PolyChunkMapData> liveChunks,
                                                        double zoom, double cameraChunkX, double cameraChunkZ,
                                                        int x1, int y1, int x2, int y2,
                                                        int mouseX, int mouseY, boolean isInteractive) {

        Minecraft mc = Minecraft.getInstance();
        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        PolyChunkMapData hoveredChunk = null;

        long currentTick = mc.level != null ? mc.level.getGameTime() : 0;
        int retentionTicks = config != null ? config.retentionTicks : 0;

        // Process Unloads into Fading Chunks
        // We need a way to detect what was unloaded. Since liveChunks is a snapshot,
        // any fading chunk that reappears in liveChunks should be removed from fading.
        Iterator<Map.Entry<Long, FadingChunk>> it = fadingChunks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, FadingChunk> entry = it.next();
            if (liveChunks.containsKey(entry.getKey())) {
                it.remove(); // came back to life
            } else if (currentTick - entry.getValue().unloadTick > retentionTicks) {
                it.remove(); // expired
            }
        }

        gfx.enableScissor(x1, y1, x2, y2);
        
        gfx.pose().pushMatrix();
        double width = x2 - x1;
        double height = y2 - y1;
        gfx.pose().translate((float) (x1 + width / 2.0), (float) (y1 + height / 2.0));
        gfx.pose().scale((float) zoom, (float) zoom);
        gfx.pose().translate((float) -cameraChunkX, (float) -cameraChunkZ);

        // Hover test coordinates
        int hx = (int) Math.floor((mouseX - (x1 + width / 2.0)) / zoom + cameraChunkX);
        int hz = (int) Math.floor((mouseY - (y1 + height / 2.0)) / zoom + cameraChunkZ);
        boolean isMouseInBounds = mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;

        // Draw Fading Chunks
        for (FadingChunk fading : fadingChunks.values()) {
            PolyChunkMapData c = fading.data;
            float age = (currentTick - fading.unloadTick);
            float alpha = 1.0f - (age / (float) Math.max(1, retentionTicks));
            if (alpha <= 0.05f) continue;
            
            int color = getColorForChunk(c);
            int aBits = (int)(alpha * 255) << 24;
            color = (color & 0x00FFFFFF) | aBits;
            
            drawChunkQuad(gfx, c, color);
        }

        // Draw Live Chunks
        for (PolyChunkMapData c : liveChunks.values()) {
            int color = getColorForChunk(c);
            drawChunkQuad(gfx, c, color);

            if (c.unloading()) {
                drawChunkQuad(gfx, c, 0x66224488); // COL_UNLOADING_OVERLAY
            }

            if (isInteractive && isMouseInBounds && c.position().x() == hx && c.position().z() == hz) {
                hoveredChunk = c;
                int cx = c.position().x();
                int cz = c.position().z();
                // Draw selection highlight outline
                gfx.fill(cx, cz, cx + 1, cz + 1, 0x88FFFFFF); // highlight fill instead of single-pixel border, because scaled borders get weird
            }
        }

        // Draw Player
        Player player = mc.player;
        if (player != null) {
            int cx = (int) Math.floor(player.getX() / 16.0);
            int cz = (int) Math.floor(player.getZ() / 16.0);
            
            // Render player dot slightly larger
            gfx.pose().pushMatrix();
            gfx.pose().translate((float) (player.getX() / 16.0), (float) (player.getZ() / 16.0));
            float pSize = (float) (2.0 / zoom); // 4 pixels on screen
            gfx.fill((int)-pSize, (int)-pSize, (int)pSize, (int)pSize, 0xFFFFFFFF);
            gfx.pose().popMatrix();
        }

        gfx.pose().popMatrix();
        gfx.disableScissor();

        return hoveredChunk;
    }

    private static void drawChunkQuad(GuiGraphicsExtractor gfx, PolyChunkMapData c, int color) {
        int cx = c.position().x();
        int cz = c.position().z();
        gfx.fill(cx, cz, cx + 1, cz + 1, color);
    }

    public static int getColorForChunk(PolyChunkMapData c) {
        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config != null && config.renderMode == PolyChunkMapConfig.RenderMode.TICKETS) {
            if (!c.tickets().isEmpty()) {
                return PolyChunkMapScreen.colourForTickets(c.tickets());
            }
            // For chunks loaded by ticket propagation but without their own direct ticket,
            // render them dimmer based on their status level so they aren't invisible.
            int statusCol = getStatusColor(c);
            return (statusCol & 0x00FFFFFF) | 0x88000000;
        }
        
        return getStatusColor(c);
    }

    private static int getStatusColor(PolyChunkMapData c) {
        return switch (c.status()) {
            case ENTITY_TICKING -> 0xFF22CC44;
            case BLOCK_TICKING  -> 0xFFCCCC22;
            case FULL           -> 0xFFCC8822;
            case INACCESSIBLE   -> 0xFF882222;
        };
    }
}
