package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.ForegroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.nodegraph.NodeTypeRegistry;
import net.creeperhost.polylib.client.modulargui.nodegraph.graph.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * A panning/zooming canvas that renders a {@link NodeGraph}.
 *
 * <h3>Coordinate system</h3>
 * <ul>
 *   <li>Canvas space — matches {@link NodeDef#x()} / {@link NodeDef#y()}</li>
 *   <li>Screen space — pixel positions on screen</li>
 *   <li>Conversion: {@code screenX = xMin() + panX + canvasX * zoom}</li>
 * </ul>
 *
 * <h3>Interaction</h3>
 * <ul>
 *   <li>Middle-mouse drag → pan</li>
 *   <li>Scroll wheel → zoom around cursor</li>
 *   <li>Left-mouse drag on node header → move node</li>
 *   <li>Left-click on port dot → start/complete connection wire</li>
 * </ul>
 *
 * <h3>Bezier wire rendering</h3>
 * GPU distance-field bezier wires are submitted via
 * {@link net.minecraft.client.gui.GuiGraphicsExtractor#submitPictureInPictureRenderState}.
 * A {@link Function bezierStateFactory} must be injected at construction time (or via
 * {@link #setBezierStateFactory}) to avoid a direct dependency on NeoForge render classes
 * from this common-module class.  The NeoForge module provides a factory that constructs
 * {@code BezierWireRenderState}.  Pass {@code null} to fall back to the CPU rasteriser.
 *
 * <h3>Mutation callbacks</h3>
 * Wire a {@link BiConsumer} via {@link #setMutationSender} to forward node edits to
 * your server-side block entity (or local model).  Byte constants {@code MT_*} mirror
 * the mutation types expected by the receiving block entity.
 */
public class GuiNodeCanvas extends GuiElement<GuiNodeCanvas>
        implements BackgroundRender, ForegroundRender {

    // ── Standard mutation-type constants ─────────────────────────────────────
    public static final byte MT_ADD_NODE        = 0;
    public static final byte MT_REMOVE_NODE     = 1;
    public static final byte MT_MOVE_NODE       = 2;
    public static final byte MT_ADD_CONNECTION  = 3;
    public static final byte MT_REMOVE_CONN     = 4;
    public static final byte MT_UPDATE_SETTINGS = 5;

    // ── Node visual constants (canvas units) ──────────────────────────────────
    public static final int NODE_WIDTH  = 160;
    public static final int HEADER_H    = 18;
    public static final int PORT_H      = 14;
    public static final int PORT_RAD    = 5;
    public static final int MIN_NODE_H  = HEADER_H + PORT_H;

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final int COL_BG           = 0xFF1A1A1A;
    private static final int COL_GRID_DOT     = 0xFF2D2D2D;
    private static final int COL_NODE_BG      = 0xFF2B2B2B;
    private static final int COL_NODE_HEADER  = 0xFF3A4A6A;
    private static final int COL_NODE_BORDER  = 0xFF555555;
    private static final int COL_NODE_SEL     = 0xFF88AAFF;
    private static final int COL_PORT_IN      = 0xFF60AAFF;
    private static final int COL_PORT_OUT     = 0xFFFFAA44;
    private static final int COL_PORT_HOVER   = 0xFFFFFFFF;
    private static final int COL_WIRE         = 0xFF88AAFF;
    private static final int COL_WIRE_PENDING = 0xFF88FF88;
    private static final int COL_TEXT         = 0xFFDDDDDD;
    private static final int COL_TEXT_DIM     = 0xFF888888;

    // ── State ─────────────────────────────────────────────────────────────────
    private NodeGraph graph = new NodeGraph();
    private double panX = 0, panY = 0;
    private double zoom = 1.0;

    // Panning
    private boolean panning = false;
    private double panMouseStartX, panMouseStartY;
    private double panStartX, panStartY;

    // Node drag
    private @Nullable UUID draggedNodeId = null;
    private double dragNodeOrigCanvasX, dragNodeOrigCanvasY;
    private double dragMouseStartX, dragMouseStartY;

    // Wire connection pending
    private @Nullable PortHandle pendingWireFrom = null;
    private double pendingWireMouseX, pendingWireMouseY;

    // Hover state
    private @Nullable UUID hoveredNodeId = null;
    private @Nullable PortHandle hoveredPort = null;

    // Selected nodes
    private final Set<UUID> selectedNodes = new LinkedHashSet<>();

    // Callbacks
    private BiConsumer<Byte, CompoundTag> mutationSender = (t, d) -> {};

    /**
     * Optional GPU bezier renderer.  Called with the current {@link GuiRender}, pose, four
     * normalised screen-space control points, and wire colour.  Set to {@code null} to fall
     * back to the CPU rasteriser.
     *
     * <p>Wire this from your NeoForge client initialiser (where PiP APIs are available):
     * <pre>
     *     canvas.setBezierRenderer((render, pose, pts, col) ->
     *         render.graphics().submitPictureInPictureRenderState(
     *             BezierWireRenderState.fromScreen(pose, pts, col)));
     * </pre>
     */
    private @Nullable BezierRenderer bezierRenderer = null;

    /**
     * Platform-injectable GPU bezier draw callback.
     * Common module code never calls PiP APIs directly — the NeoForge module provides the impl.
     */
    @FunctionalInterface
    public interface BezierRenderer {
        void draw(GuiRender render, Matrix3x2f pose, org.joml.Vector2fc[] screenPoints, int colour);
    }

    // ── Constructor ───────────────────────────────────────────────────────────

    public GuiNodeCanvas(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void setGraph(NodeGraph graph) {
        this.graph = graph;
        if (!graph.nodes().isEmpty()) {
            double cx = graph.nodes().values().stream().mapToInt(NodeDef::x).average().orElse(0);
            double cy = graph.nodes().values().stream().mapToInt(NodeDef::y).average().orElse(0);
            panX = xSize() / 2.0 - cx * zoom;
            panY = ySize() / 2.0 - cy * zoom;
        }
    }

    public NodeGraph getGraph() { return graph; }

    public void setMutationSender(BiConsumer<Byte, CompoundTag> sender) {
        this.mutationSender = sender;
    }

    public void setBezierRenderer(@Nullable BezierRenderer renderer) {
        this.bezierRenderer = renderer;
    }

    public double viewCenterCanvasX() { return (xSize() / 2.0 - panX) / zoom; }
    public double viewCenterCanvasY() { return (ySize() / 2.0 - panY) / zoom; }

    // ── Coordinate helpers ────────────────────────────────────────────────────

    private double canvasToScreenX(double cx) { return xMin() + panX + cx * zoom; }
    private double canvasToScreenY(double cy) { return yMin() + panY + cy * zoom; }
    private double screenToCanvasX(double sx) { return (sx - xMin() - panX) / zoom; }
    private double screenToCanvasY(double sy) { return (sy - yMin() - panY) / zoom; }
    private double z(double v) { return v * zoom; }

    // ── Hit testing ───────────────────────────────────────────────────────────

    private @Nullable UUID nodeAt(double sx, double sy) {
        for (NodeDef n : graph.nodes().values()) {
            int nh = nodeHeight(n);
            double nx = canvasToScreenX(n.x());
            double ny = canvasToScreenY(n.y());
            double nw = z(NODE_WIDTH);
            double nHs = z(nh);
            if (sx >= nx && sx < nx + nw && sy >= ny && sy < ny + nHs) return n.id();
        }
        return null;
    }

    private @Nullable PortHandle portAt(double sx, double sy) {
        double hitR = Math.max(6, z(PORT_RAD + 2));
        for (NodeDef n : graph.nodes().values()) {
            if (n.isCollapsed()) continue;
            List<PortDescriptor> ports = portsFor(n);
            for (int i = 0; i < ports.size(); i++) {
                PortDescriptor pd = ports.get(i);
                double[] sc = portScreenCenter(n, pd, i);
                double dx = sx - sc[0], dy = sy - sc[1];
                if (dx * dx + dy * dy <= hitR * hitR) {
                    return new PortHandle(n.id(), i, pd.direction() == PortDescriptor.PortDirection.OUT);
                }
            }
        }
        return null;
    }

    private boolean inHeader(NodeDef n, double sx, double sy) {
        double nx = canvasToScreenX(n.x());
        double ny = canvasToScreenY(n.y());
        double nw = z(NODE_WIDTH);
        double hh = z(HEADER_H);
        return sx >= nx && sx < nx + nw && sy >= ny && sy < ny + hh;
    }

    // ── Layout helpers ────────────────────────────────────────────────────────

    private int nodeHeight(NodeDef n) {
        if (n.isCollapsed()) return HEADER_H;
        int portRows = portsFor(n).size();
        return HEADER_H + Math.max(1, portRows) * PORT_H + 4;
    }

    private List<PortDescriptor> portsFor(NodeDef n) {
        return NodeTypeRegistry.get(n.typeId())
                .map(t -> t.getPorts(n))
                .orElse(List.of(PortDescriptor.anyIn(0, "?"), PortDescriptor.anyOut(1, "?")));
    }

    private double[] portCanvasCenter(NodeDef n, PortDescriptor pd, int idx) {
        boolean isOut = pd.direction() == PortDescriptor.PortDirection.OUT;
        double px = n.x() + (isOut ? NODE_WIDTH : 0);
        if (n.isCollapsed()) {
            double py = n.y() + HEADER_H / 2.0;
            return new double[]{px, py};
        }
        double py = n.y() + HEADER_H + (idx + 0.5) * PORT_H;
        return new double[]{px, py};
    }

    private double[] portScreenCenter(NodeDef n, PortDescriptor pd, int idx) {
        double[] c = portCanvasCenter(n, pd, idx);
        return new double[]{canvasToScreenX(c[0]), canvasToScreenY(c[1])};
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTick) {
        render.fill(xMin(), yMin(), xMax(), yMax(), COL_BG);
        drawGrid(render);
        for (NodeDef n : graph.nodes().values()) {
            drawNode(render, n);
        }
    }

    @Override
    public void renderInFront(GuiRender render, double mouseX, double mouseY, float partialTick) {
        Map<String, Integer> lineCounts = new HashMap<>();
        Map<ConnectionDef, Integer> lineOffsets = new HashMap<>();
        for (ConnectionDef conn : graph.connections()) {
            NodeDef fromNode = graph.nodes().get(conn.fromNode());
            NodeDef toNode   = graph.nodes().get(conn.toNode());
            if (fromNode == null || toNode == null) continue;
            List<PortDescriptor> fromPorts = portsFor(fromNode);
            List<PortDescriptor> toPorts   = portsFor(toNode);
            if (conn.fromPort() >= fromPorts.size() || conn.toPort() >= toPorts.size()) continue;
            double[] from = portScreenCenter(fromNode, fromPorts.get(conn.fromPort()), conn.fromPort());
            double[] to   = portScreenCenter(toNode,   toPorts.get(conn.toPort()),     conn.toPort());
            String key = String.format(Locale.ROOT, "%.1f,%.1f->%.1f,%.1f", from[0], from[1], to[0], to[1]);
            int count = lineCounts.getOrDefault(key, 0);
            int offsetIndex = (count % 2 == 0) ? (count / 2) : -(count / 2 + 1); // Alternating offset 0, -1, 1, -2, 2
            lineOffsets.put(conn, offsetIndex);
            lineCounts.put(key, count + 1);
        }

        for (ConnectionDef conn : graph.connections()) {
            int offsetIndex = lineOffsets.getOrDefault(conn, 0);
            drawWire(render, conn, COL_WIRE, offsetIndex);
        }
        if (pendingWireFrom != null) {
            NodeDef fromNode = graph.nodes().get(pendingWireFrom.nodeId());
            if (fromNode != null) {
                List<PortDescriptor> ports = portsFor(fromNode);
                if (pendingWireFrom.portIndex() < ports.size()) {
                    double[] sc = portScreenCenter(fromNode, ports.get(pendingWireFrom.portIndex()), pendingWireFrom.portIndex());
                    drawBezier(render, sc[0], sc[1], pendingWireMouseX, pendingWireMouseY, COL_WIRE_PENDING, 0);
                }
            }
        }
        updateHover(mouseX, mouseY);
    }

    private void drawGrid(GuiRender render) {
        int gridUnit = 32;
        double gsz = z(gridUnit);
        if (gsz < 4) return;
        double startX = xMin() + ((panX % gsz) + gsz) % gsz;
        double startY = yMin() + ((panY % gsz) + gsz) % gsz;
        double dotSize = Math.max(1, z(1.5));
        double x = startX;
        while (x < xMax()) {
            double y = startY;
            while (y < yMax()) {
                render.fill(x, y, x + dotSize, y + dotSize, COL_GRID_DOT);
                y += gsz;
            }
            x += gsz;
        }
    }

    private void drawNode(GuiRender render, NodeDef n) {
        int nh = nodeHeight(n);
        double nx = canvasToScreenX(n.x());
        double ny = canvasToScreenY(n.y());
        double nw = z(NODE_WIDTH);
        double nhs = z(nh);
        double hh = z(HEADER_H);
        if (nx + nw < xMin() || nx > xMax() || ny + nhs < yMin() || ny > yMax()) return;
        boolean selected = selectedNodes.contains(n.id());
        render.fill(nx, ny, nx + nw, ny + nhs, COL_NODE_BG);
        render.fill(nx, ny, nx + nw, ny + hh, COL_NODE_HEADER);
        int borderCol = selected ? COL_NODE_SEL : COL_NODE_BORDER;
        render.fill(nx,          ny,           nx + nw,     ny + 1,        borderCol);
        render.fill(nx,          ny + nhs - 1, nx + nw,     ny + nhs,      borderCol);
        render.fill(nx,          ny,           nx + 1,       ny + nhs,      borderCol);
        render.fill(nx + nw - 1, ny,           nx + nw,      ny + nhs,      borderCol);
        String label = NodeTypeRegistry.get(n.typeId())
                .map(t -> t.getDisplayName().getString())
                .orElse(n.typeId().getPath());
        double textScale = Math.min(1.0, zoom);
        if (textScale > 0.4) {
            render.drawString(Component.literal(label), nx + 3, ny + (hh - 9 * textScale) / 2, COL_TEXT);
            double btnX = nx + nw - 12 * zoom;
            double btnY = ny + (hh - 9 * textScale) / 2;
            render.drawString(Component.literal(n.isCollapsed() ? "+" : "-"), btnX, btnY, COL_TEXT);
        }
        if (!n.isCollapsed()) {
            List<PortDescriptor> ports = portsFor(n);
            for (int i = 0; i < ports.size(); i++) {
                drawPort(render, n, ports.get(i), i);
            }
        }
    }

    private void drawPort(GuiRender render, NodeDef n, PortDescriptor pd, int idx) {
        double[] sc = portScreenCenter(n, pd, idx);
        double r = z(PORT_RAD);
        boolean isOut = pd.direction() == PortDescriptor.PortDirection.OUT;
        PortHandle ph = new PortHandle(n.id(), idx, isOut);
        int col = ph.equals(hoveredPort) ? COL_PORT_HOVER : (isOut ? COL_PORT_OUT : COL_PORT_IN);
        render.fill(sc[0] - r, sc[1] - r, sc[0] + r, sc[1] + r, col);
        double textScale = Math.min(1.0, zoom);
        if (textScale > 0.5 && !pd.label().isEmpty()) {
            double lx = isOut ? (sc[0] - r - 2 - render.font().width(pd.label()) * textScale)
                              : (sc[0] + r + 2);
            render.drawString(Component.literal(pd.label()), lx, sc[1] - 4 * textScale, COL_TEXT_DIM);
        }
    }

    private void drawWire(GuiRender render, ConnectionDef conn, int colour, int offsetIndex) {
        NodeDef fromNode = graph.nodes().get(conn.fromNode());
        NodeDef toNode   = graph.nodes().get(conn.toNode());
        if (fromNode == null || toNode == null) return;
        List<PortDescriptor> fromPorts = portsFor(fromNode);
        List<PortDescriptor> toPorts   = portsFor(toNode);
        if (conn.fromPort() >= fromPorts.size() || conn.toPort() >= toPorts.size()) return;
        double[] from = portScreenCenter(fromNode, fromPorts.get(conn.fromPort()), conn.fromPort());
        double[] to   = portScreenCenter(toNode,   toPorts.get(conn.toPort()),     conn.toPort());
        drawBezier(render, from[0], from[1], to[0], to[1], colour, offsetIndex);
    }

    private void drawBezier(GuiRender render, double x0, double y0, double x1, double y1, int colour, int offsetIndex) {
        double dx = Math.abs(x1 - x0);
        double handle = Math.max(40 * zoom, dx * 0.5);
        double offsetAmount = offsetIndex * 15 * zoom;
        double cpx0 = x0 + handle, cpy0 = y0 + offsetAmount;
        double cpx1 = x1 - handle, cpy1 = y1 + offsetAmount;

        if (bezierRenderer != null) {
            org.joml.Vector2fc[] screenPts = new org.joml.Vector2fc[]{
                new Vector2f((float) x0,           (float) y0),
                new Vector2f((float) cpx0,         (float) cpy0),
                new Vector2f((float) cpx1,         (float) cpy1),
                new Vector2f((float) x1,           (float) y1)
            };
            // render.pose() returns Matrix3x2fStack which extends Matrix3x2f; copy it.
            Matrix3x2f pose = new Matrix3x2f(render.pose());
            bezierRenderer.draw(render, pose, screenPts, colour);
        } else {
            // CPU fallback — 24-segment approximation
            int steps = 24;
            double prevX = x0, prevY = y0;
            double w = Math.max(1.0, zoom * 1.5);
            for (int i = 1; i <= steps; i++) {
                double t = (double) i / steps;
                double mt = 1 - t;
                double nx = mt*mt*mt*x0 + 3*mt*mt*t*cpx0 + 3*mt*t*t*cpx1 + t*t*t*x1;
                double ny = mt*mt*mt*y0 + 3*mt*mt*t*cpy0 + 3*mt*t*t*cpy1 + t*t*t*y1;
                double minX = Math.min(prevX, nx), maxX = Math.max(prevX, nx);
                double minY = Math.min(prevY, ny), maxY = Math.max(prevY, ny);
                render.fill(minX - w/2, minY - w/2, maxX + w/2, maxY + w/2, colour);
                prevX = nx; prevY = ny;
            }
        }
    }

    private void updateHover(double mouseX, double mouseY) {
        if (!isInBounds(mouseX, mouseY)) {
            hoveredNodeId = null;
            hoveredPort = null;
            return;
        }
        hoveredPort   = portAt(mouseX, mouseY);
        hoveredNodeId = (hoveredPort == null) ? nodeAt(mouseX, mouseY) : null;
    }

    private boolean isInBounds(double mx, double my) {
        return mx >= xMin() && mx < xMax() && my >= yMin() && my < yMax();
    }

    // ── Mouse events ──────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isInBounds(mouseX, mouseY)) return false;
        if (button == 2) {
            panning = true;
            panMouseStartX = mouseX; panMouseStartY = mouseY;
            panStartX = panX; panStartY = panY;
            return true;
        }
        if (button == 0) {
            PortHandle ph = portAt(mouseX, mouseY);
            if (ph != null) {
                if (pendingWireFrom == null) {
                    pendingWireFrom = ph;
                    pendingWireMouseX = mouseX;
                    pendingWireMouseY = mouseY;
                } else {
                    tryConnectPorts(pendingWireFrom, ph);
                    pendingWireFrom = null;
                }
                return true;
            }
            if (pendingWireFrom != null) {
                pendingWireFrom = null;
                return true;
            }
            UUID nid = nodeAt(mouseX, mouseY);
            if (nid != null) {
                NodeDef n = graph.nodes().get(nid);
                if (n != null && inHeader(n, mouseX, mouseY)) {
                    double btnX = canvasToScreenX(n.x()) + z(NODE_WIDTH) - 16 * zoom;
                    if (mouseX >= btnX) {
                        n.setCollapsed(!n.isCollapsed());
                        CompoundTag data = n.toNbt();
                        mutationSender.accept(MT_UPDATE_SETTINGS, data);
                        return true;
                    }
                    draggedNodeId = nid;
                    dragNodeOrigCanvasX = n.x();
                    dragNodeOrigCanvasY = n.y();
                    dragMouseStartX = mouseX;
                    dragMouseStartY = mouseY;
                    selectedNodes.clear();
                    selectedNodes.add(nid);
                    return true;
                }
                selectedNodes.clear();
                selectedNodes.add(nid);
            } else {
                selectedNodes.clear();
            }
            return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (panning) {
            panX = panStartX + (mouseX - panMouseStartX);
            panY = panStartY + (mouseY - panMouseStartY);
        }
        if (draggedNodeId != null) {
            NodeDef n = graph.nodes().get(draggedNodeId);
            if (n != null) {
                int newX = (int) Math.round(dragNodeOrigCanvasX + (mouseX - dragMouseStartX) / zoom);
                int newY = (int) Math.round(dragNodeOrigCanvasY + (mouseY - dragMouseStartY) / zoom);
                graph.moveNode(n.id(), newX, newY);
            }
        }
        if (pendingWireFrom != null) {
            pendingWireMouseX = mouseX;
            pendingWireMouseY = mouseY;
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 2 && panning) {
            panning = false;
            return true;
        }
        if (button == 0 && draggedNodeId != null) {
            NodeDef n = graph.nodes().get(draggedNodeId);
            if (n != null) {
                CompoundTag data = new CompoundTag();
                data.putString("id", n.id().toString());
                data.putInt("x", n.x());
                data.putInt("y", n.y());
                mutationSender.accept(MT_MOVE_NODE, data);
            }
            draggedNodeId = null;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isInBounds(mouseX, mouseY)) return false;
        double factor = scrollY > 0 ? 1.15 : (1.0 / 1.15);
        double newZoom = Math.max(0.25, Math.min(3.0, zoom * factor));
        double cx = mouseX - xMin();
        double cy = mouseY - yMin();
        panX = cx - (cx - panX) * (newZoom / zoom);
        panY = cy - (cy - panY) * (newZoom / zoom);
        zoom = newZoom;
        return true;
    }

    // ── Wire connection logic ─────────────────────────────────────────────────

    private void tryConnectPorts(PortHandle from, PortHandle to) {
        if (from.isOutput() == to.isOutput()) return;
        PortHandle outPort = from.isOutput() ? from : to;
        PortHandle inPort  = from.isOutput() ? to   : from;
        NodeDef outNode = graph.nodes().get(outPort.nodeId());
        NodeDef inNode  = graph.nodes().get(inPort.nodeId());
        if (outNode == null || inNode == null || outNode.id().equals(inNode.id())) return;
        List<PortDescriptor> outPorts = portsFor(outNode);
        List<PortDescriptor> inPorts  = portsFor(inNode);
        if (outPort.portIndex() >= outPorts.size() || inPort.portIndex() >= inPorts.size()) return;
        PortDescriptor opd = outPorts.get(outPort.portIndex());
        PortDescriptor ipd = inPorts.get(inPort.portIndex());
        if (!PortDescriptor.PortDataType.compatible(opd.dataType(), ipd.dataType())) return;
        ConnectionDef conn = new ConnectionDef(UUID.randomUUID(), outNode.id(), outPort.portIndex(),
                inNode.id(), inPort.portIndex());
        CompoundTag data = conn.toNbt();
        mutationSender.accept(MT_ADD_CONNECTION, data);
        graph.addConnection(conn);
    }

    // ── Add / Remove helpers ──────────────────────────────────────────────────

    public void addNode(NodeDef node) {
        CompoundTag data = node.toNbt();
        mutationSender.accept(MT_ADD_NODE, data);
        graph.addNode(node);
    }

    public void removeSelectedNodes() {
        for (UUID id : new ArrayList<>(selectedNodes)) {
            CompoundTag data = new CompoundTag();
            data.putString("id", id.toString());
            mutationSender.accept(MT_REMOVE_NODE, data);
            graph.removeNode(id);
        }
        selectedNodes.clear();
    }

    // ── Inner types ───────────────────────────────────────────────────────────

    public record PortHandle(UUID nodeId, int portIndex, boolean isOutput) {}
}
