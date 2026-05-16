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
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.creeperhost.polylib.PolyLibClient;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapConfig;

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

    // Ticket Colours
    private static final int COL_TICKET_PLAYER     = 0xFF22CC44;
    private static final int COL_TICKET_FORCED     = 0xFFCC2222;
    private static final int COL_TICKET_PEARL      = 0xFFAA22CC;
    private static final int COL_TICKET_PORTAL     = 0xFFCC8822;
    private static final int COL_TICKET_UNKNOWN    = 0xFF666666;

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

        // Send a request to the server to start sending chunk map data.
        Services.NETWORK.sendToServer(new net.creeperhost.polylib.chunkmap.common.network.PolyChunkMapStartPayload(java.util.List.of(mc.level.dimension())));
        
        int btnWidth = 100;
        int btnHeight = 20;
        int startX = 5;
        int startY = height - FOOTER_HEIGHT + 4;

        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                net.minecraft.network.chat.Component.literal("Mode: " + PolyLibClient.chunkMapConfig.renderMode.name()),
                btn -> {
                    PolyLibClient.chunkMapConfig.renderMode = PolyLibClient.chunkMapConfig.renderMode == PolyChunkMapConfig.RenderMode.STATUS
                            ? PolyChunkMapConfig.RenderMode.TICKETS : PolyChunkMapConfig.RenderMode.STATUS;
                    btn.setMessage(net.minecraft.network.chat.Component.literal("Mode: " + PolyLibClient.chunkMapConfig.renderMode.name()));
                }).bounds(startX, startY, btnWidth, btnHeight).build());

        startX += btnWidth + 5;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                net.minecraft.network.chat.Component.literal("Overlay: " + PolyLibClient.chunkMapConfig.minimapDisplayMode.name()),
                btn -> {
                    PolyChunkMapConfig.MinimapDisplayMode[] modes = PolyChunkMapConfig.MinimapDisplayMode.values();
                    int next = (PolyLibClient.chunkMapConfig.minimapDisplayMode.ordinal() + 1) % modes.length;
                    PolyLibClient.chunkMapConfig.minimapDisplayMode = modes[next];
                    btn.setMessage(net.minecraft.network.chat.Component.literal("Overlay: " + PolyLibClient.chunkMapConfig.minimapDisplayMode.name()));
                }).bounds(startX, startY, btnWidth + 20, btnHeight).build());

        int yOffset = 5;
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.literal("Minimap Zoom: " + String.format("%.1fx", zoom)),
                btn -> {
                    if (PolyLibClient.chunkMapConfig != null) {
                        double nextZoom = PolyLibClient.chunkMapConfig.minimapZoom + 0.5;
                        if (nextZoom > 3.0) nextZoom = 0.5;
                        PolyLibClient.chunkMapConfig.minimapZoom = nextZoom;
                        btn.setMessage(Component.literal("Minimap Zoom: " + String.format("%.1fx", nextZoom)));
                    }
                }
        ).bounds(width - btnWidth - 10, yOffset, btnWidth, btnHeight).build());
        yOffset += btnHeight + 4;
        
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.literal("Edit Overlay Pos"),
                btn -> Minecraft.getInstance().setScreen(new PolyChunkMapEditOverlayScreen(this))
        ).bounds(width - btnWidth - 10, yOffset, btnWidth, btnHeight).build());
        yOffset += btnHeight + 4;
    }

    // ── Close ─────────────────────────────────────────────────────────────────

    @Override
    public void onClose()
    {
        Services.NETWORK.sendToServer(PolyChunkMapStopPayload.stopAll());
        if (net.creeperhost.polylib.PolyLibClient.chunkMapConfigBuilder != null) {
            net.creeperhost.polylib.PolyLibClient.chunkMapConfigBuilder.save();
        }
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

        // ── Chunk Grid and Player
        hoveredChunk = net.creeperhost.polylib.chunkmap.client.PolyChunkGridRenderer.renderGrid(gfx, chunks, zoom, cameraChunkX, cameraChunkZ, 0, mapTop, width, mapBottom, mouseX, mouseY, true);

        // ── Header bar
        gfx.fill(0, 0, width, HEADER_HEIGHT, COL_HEADER_BG);
        String dimId = dim.toString();
        String serverStatus = permitted ? "§aGRANTED§r" : "§cDENIED§r";
        String header = "§lPolyLib Chunk Map§r  " + dimId
                + "  Server: " + serverStatus
                + "  Chunks: " + chunks.size()
                + "  Zoom: " + String.format("%.0f", zoom) + "px"
                + "  [Scroll] Zoom  [Drag] Pan  [T] Ticket Mode  [S] Status Mode  [Esc] Close";
        gfx.text(font, header, 4, (HEADER_HEIGHT - 8) / 2, COL_HEADER_TEXT, false);

        // ── Footer bar
        gfx.fill(0, mapBottom, width, height, COL_HEADER_BG);

        // ── Render widgets (Options button)
        super.extractRenderState(gfx, mouseX, mouseY, partialTick);

        // ── Tooltip (must be last so it renders on top)
        if (hoveredChunk != null)
            renderChunkTooltip(gfx, hoveredChunk, mouseX, mouseY, font);
    }

    private void renderChunkTooltip(GuiGraphicsExtractor gfx, PolyChunkMapData c, int mx, int my, net.minecraft.client.gui.Font font)
    {
        List<String> lines = new java.util.ArrayList<>();
        lines.add(String.format("Chunk [%d, %d]", c.position().x(), c.position().z()));
        lines.add("Stage: " + (c.stage() == null ? "None" : c.stage().getName()));

        int statusLevel = c.statusLevel();
        if (c.unloading()) {
            lines.add(String.format("Unloading (Level %d)", statusLevel));
        } else {
            lines.add(String.format("Status: Level %d", statusLevel));
        }

        if (c.tickets().isEmpty()) {
            lines.add("  No tickets");
        } else {
            for (PolyChunkTicket t : c.tickets()) {
                String tName = net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapCodecs.ticketTypeName(t.type());
                lines.add(String.format("  %s (Lvl %d)", tName, t.ticketLevel()));
            }
        }

        int tw = lines.stream().mapToInt(font::width).max().orElse(0) + 8;
        int th = lines.size() * (font.lineHeight + 2) + 6;

        int tx = Math.min(mx + 6, width  - tw - 2);
        int ty = Math.min(my + 6, height - th - 2);

        gfx.pose().pushMatrix();
        
        gfx.fill(tx - 1, ty - 1, tx + tw + 1, ty + th + 1, 0xFF000000);
        gfx.fill(tx, ty, tx + tw, ty + th, 0xC0111111);

        int lineY = ty + 3;
        for (String line : lines)
        {
            gfx.text(font, line, tx + 4, lineY, 0xFFFFFFFF, false);
            lineY += font.lineHeight + 2;
        }
        
        gfx.pose().popMatrix();
    }


    // ── Coordinate helpers ─────────────────────────────────────────────────────

    private int chunkToScreenX(int cx)
    {
        return (int) Math.floor((cx - cameraChunkX) * zoom + width / 2.0);
    }

    private int chunkToScreenY(int cz)
    {
        int mapCentreY = HEADER_HEIGHT + (height - HEADER_HEIGHT - FOOTER_HEIGHT) / 2;
        return (int) Math.floor((cz - cameraChunkZ) * zoom + mapCentreY);
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
    public boolean keyPressed(KeyEvent event)
    {
        if (PolyLibClient.chunkMapConfig != null) {
            if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_T) {
                PolyLibClient.chunkMapConfig.renderMode = PolyChunkMapConfig.RenderMode.TICKETS;
                return true;
            } else if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_S) {
                PolyLibClient.chunkMapConfig.renderMode = PolyChunkMapConfig.RenderMode.STATUS;
                return true;
            }
        }
        return super.keyPressed(event);
    }

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

    public static int colourForTickets(List<PolyChunkTicket> tickets)
    {
        if (tickets == null || tickets.isEmpty()) return COL_BG;
        // Find the strongest ticket (lowest level)
        PolyChunkTicket dominant = tickets.get(0);
        for (PolyChunkTicket t : tickets) {
            if (t.ticketLevel() < dominant.ticketLevel()) {
                dominant = t;
            }
        }

        String typeName = net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapCodecs.ticketTypeName(dominant.type());
        Identifier id = net.minecraft.resources.Identifier.tryParse(typeName);
        if (id == null) return COL_TICKET_UNKNOWN;

        return switch (id.getPath()) {
            case "player" -> COL_TICKET_PLAYER;
            case "player_spawn", "spawn_search" -> 0xFFBFFF00; // ChunkDebug colours
            case "forced" -> COL_TICKET_FORCED;
            case "ender_pearl" -> COL_TICKET_PEARL;
            case "portal" -> COL_TICKET_PORTAL;
            case "dragon" -> 0xFFCC00CC; // ChunkDebug colours
            default -> COL_TICKET_UNKNOWN;
        };
    }
}

