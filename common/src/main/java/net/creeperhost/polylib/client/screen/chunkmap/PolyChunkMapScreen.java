package net.creeperhost.polylib.client.screen.chunkmap;

import net.creeperhost.polylib.chunkmap.client.PolyChunkMapClient;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapCodecs;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkTicket;
import net.creeperhost.polylib.chunkmap.common.network.PolyChunkMapStopPayload;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Full-screen chunk-map viewer.
 *
 * <p>Reads live data from {@link PolyChunkMapClient} and draws one coloured rectangle
 * per tracked server-side chunk. Supports zoom (scroll wheel) and pan (click-drag).
 *
 * <h3>Colour legend</h3>
 * <ul>
 *   <li>Green  — ENTITY_TICKING</li>
 *   <li>Yellow — BLOCK_TICKING</li>
 *   <li>Orange — FULL</li>
 *   <li>Red    — INACCESSIBLE</li>
 *   <li>Blue overlay — unloading</li>
 * </ul>
 *
 * <p>Open via {@link PolyChunkMapKeys#open(Minecraft)}.
 */
public class PolyChunkMapScreen extends Screen
{
    // ── Colours ───────────────────────────────────────────────────────────────
    private static final int COL_ENTITY_TICKING    = 0xFF22CC44;
    private static final int COL_BLOCK_TICKING     = 0xFFCCCC22;
    private static final int COL_FULL              = 0xFFCC8822;
    private static final int COL_INACCESSIBLE      = 0xFF882222;
    private static final int COL_UNLOADING_OVERLAY = 0x66224488;
    private static final int COL_PLAYER            = 0xFFFFFFFF;
    private static final int COL_BG                = 0xFF111111;
    private static final int COL_HEADER_BG         = 0xCC000000;
    private static final int COL_HEADER_TEXT       = 0xFFFFFFFF;

    private static final int HEADER_HEIGHT = 20;
    private static final int FOOTER_HEIGHT = 12;

    // ── View state ────────────────────────────────────────────────────────────
    /** Pixels per chunk edge. */
    private double zoom = 4.0;
    private static final double ZOOM_MIN = 1.0;
    private static final double ZOOM_MAX = 16.0;

    /** World-space chunk coordinates at screen centre. */
    private double cameraChunkX;
    private double cameraChunkZ;

    // Pan via mouse drag — tracked via mouseMoved when button 0 is held
    private boolean dragging = false;
    private double dragStartMouseX, dragStartMouseZ;
    private double cameraDragOriginX, cameraDragOriginZ;

    // ── Hover state ───────────────────────────────────────────────────────────
    private @Nullable PolyChunkMapData hoveredChunk = null;
    private int tooltipX, tooltipY;

    public PolyChunkMapScreen()
    {
        super(Component.translatable("screen.polylib.chunk_map"));
    }

    @Override
    protected void init()
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null)
        {
            cameraChunkX = mc.player.getX() / 16.0;
            cameraChunkZ = mc.player.getZ() / 16.0;
        }
    }

    // ── Close ─────────────────────────────────────────────────────────────────

    @Override
    public void onClose()
    {
        Services.NETWORK.sendToServer(PolyChunkMapStopPayload.stopAll());
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) { onClose(); return; }

        ResourceKey<Level> dim = mc.level.dimension();
        boolean permitted = PolyChunkMapClient.isPermitted();
        Map<Long, PolyChunkMapData> chunks = permitted
                ? PolyChunkMapClient.getChunks(dim)
                : Map.of();

        int mapTop    = HEADER_HEIGHT;
        int mapBottom = height - FOOTER_HEIGHT;

        // ── Background
        gfx.fill(0, mapTop, width, mapBottom, COL_BG);

        // ── Chunk rectangles
        hoveredChunk = null;
        for (PolyChunkMapData c : chunks.values())
        {
            int px = chunkToScreenX(c.position().x());
            int pz = chunkToScreenY(c.position().z());
            int size = Math.max(1, (int) zoom);

            // clip to map area
            if (px + size < 0 || px > width || pz + size < mapTop || pz > mapBottom)
                continue;

            int colour = colourForStatus(c.status());
            gfx.fill(px, pz, px + size, pz + size, colour);

            if (c.unloading())
                gfx.fill(px, pz, px + size, pz + size, COL_UNLOADING_OVERLAY);

            // Hover detection
            if (mouseX >= px && mouseX < px + size && mouseY >= pz && mouseY < pz + size
                    && mouseY >= mapTop && mouseY < mapBottom)
            {
                hoveredChunk = c;
                tooltipX = mouseX;
                tooltipY = mouseY;
                // bright 1-px border
                gfx.fill(px - 1, pz - 1,      px + size + 1, pz,          0xFFFFFFFF);
                gfx.fill(px - 1, pz + size,    px + size + 1, pz + size + 1, 0xFFFFFFFF);
                gfx.fill(px - 1, pz,            px,            pz + size,   0xFFFFFFFF);
                gfx.fill(px + size, pz,         px + size + 1, pz + size,   0xFFFFFFFF);
            }
        }

        // ── Player crosshair
        if (mc.player != null)
        {
            int px = chunkToScreenX((int) Math.floor(mc.player.getX() / 16.0));
            int pz = chunkToScreenY((int) Math.floor(mc.player.getZ() / 16.0));
            if (pz >= mapTop && pz < mapBottom)
            {
                int sz = Math.max(1, (int) zoom);
                gfx.fill(px - 2,      pz + sz / 2,     px + sz + 2,     pz + sz / 2 + 1, COL_PLAYER);
                gfx.fill(px + sz / 2, pz - 2,          px + sz / 2 + 1, pz + sz + 2,     COL_PLAYER);
            }
        }

        // ── Header bar
        gfx.fill(0, 0, width, HEADER_HEIGHT, COL_HEADER_BG);
        String dimId = dim.toString();
        String serverStatus = permitted ? "§aGRANTED§r" : "§cDENIED§r";
        String header = "§lPolyLib Chunk Map§r  " + dimId
                + "  Server: " + serverStatus
                + "  Chunks: " + chunks.size()
                + "  Zoom: " + String.format("%.0f", zoom) + "px"
                + "  [Scroll] Zoom  [Drag] Pan  [Esc] Close";
        gfx.text(font, header, 4, (HEADER_HEIGHT - 8) / 2, COL_HEADER_TEXT, false);

        // ── Footer bar
        gfx.fill(0, mapBottom, width, height, COL_HEADER_BG);

        // ── Tooltip (must be last so it renders on top)
        if (hoveredChunk != null)
            renderChunkTooltip(gfx, hoveredChunk, tooltipX, tooltipY);
    }

    private void renderChunkTooltip(GuiGraphicsExtractor gfx, PolyChunkMapData c, int mx, int my)
    {
        List<String> lines = new ArrayList<>();
        lines.add("Chunk " + c.position().x() + ", " + c.position().z());
        lines.add("Status: " + c.status().name());
        if (c.stage() != null)
            lines.add("Stage:  " + c.stage().getName());
        if (c.unloading())
            lines.add("§cUnloading§r");
        lines.add("Tickets: " + c.tickets().size());
        for (PolyChunkTicket t : c.tickets())
            lines.add("  " + PolyChunkMapCodecs.ticketTypeName(t.type()) + " lv=" + t.ticketLevel());

        int tw = lines.stream().mapToInt(font::width).max().orElse(0) + 8;
        int th = lines.size() * (font.lineHeight + 2) + 6;

        int tx = Math.min(mx + 6, width  - tw - 2);
        int ty = Math.min(my + 6, height - th - 2);

        gfx.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, 0xFF000000);
        gfx.fill(tx, ty, tx + tw, ty + th, 0xC0111111);

        int lineY = ty + 3;
        for (String line : lines)
        {
            gfx.text(font, line, tx + 4, lineY, 0xFFFFFF, false);
            lineY += font.lineHeight + 2;
        }
    }

    // ── Coordinate helpers ─────────────────────────────────────────────────────

    private int chunkToScreenX(int cx)
    {
        return (int) ((cx - cameraChunkX) * zoom + width / 2.0);
    }

    private int chunkToScreenY(int cz)
    {
        int mapCentreY = HEADER_HEIGHT + (height - HEADER_HEIGHT - FOOTER_HEIGHT) / 2;
        return (int) ((cz - cameraChunkZ) * zoom + mapCentreY);
    }

    private double screenXToChunk(double sx)
    {
        return (sx - width / 2.0) / zoom + cameraChunkX;
    }

    private double screenYToChunk(double sy)
    {
        int mapCentreY = HEADER_HEIGHT + (height - HEADER_HEIGHT - FOOTER_HEIGHT) / 2;
        return (sy - mapCentreY) / zoom + cameraChunkZ;
    }

    // ── Input ─────────────────────────────────────────────────────────────────

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY)
    {
        // Zoom towards cursor
        double chunkUnderCursorX = screenXToChunk(mx);
        double chunkUnderCursorZ = screenYToChunk(my);

        double factor = scrollY > 0 ? 1.25 : 0.8;
        zoom = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoom * factor));

        // Reposition camera so the chunk under the cursor stays fixed
        cameraChunkX = chunkUnderCursorX - (mx - width / 2.0) / zoom;
        int mapCentreY = HEADER_HEIGHT + (height - HEADER_HEIGHT - FOOTER_HEIGHT) / 2;
        cameraChunkZ = chunkUnderCursorZ - (my - mapCentreY) / zoom;
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl)
    {
        if (super.mouseClicked(event, bl)) return true;
        double mx = event.x(), my = event.y();
        int button = event.button();
        if (button == 0 && my > HEADER_HEIGHT && my < height - FOOTER_HEIGHT)
        {
            dragging = true;
            dragStartMouseX = mx;
            dragStartMouseZ = my;
            cameraDragOriginX = cameraChunkX;
            cameraDragOriginZ = cameraChunkZ;
            return true;
        }
        return false;
    }

    /** Pans the map while the left mouse button is held. */
    @Override
    public void mouseMoved(double mx, double my)
    {
        if (dragging)
        {
            cameraChunkX = cameraDragOriginX - (mx - dragStartMouseX) / zoom;
            cameraChunkZ = cameraDragOriginZ - (my - dragStartMouseZ) / zoom;
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        if (event.button() == 0) dragging = false;
        return super.mouseReleased(event);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static int colourForStatus(FullChunkStatus status)
    {
        return switch (status)
        {
            case ENTITY_TICKING -> COL_ENTITY_TICKING;
            case BLOCK_TICKING  -> COL_BLOCK_TICKING;
            case FULL           -> COL_FULL;
            case INACCESSIBLE   -> COL_INACCESSIBLE;
        };
    }
}
