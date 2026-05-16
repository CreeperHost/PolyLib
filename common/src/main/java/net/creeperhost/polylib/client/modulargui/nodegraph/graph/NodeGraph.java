package net.creeperhost.polylib.client.modulargui.nodegraph.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

/**
 * Mutable directed acyclic graph of automation nodes and their connections.
 *
 * <p>This is the server-authoritative data structure.  Client-side code receives
 * a copy over the network and re-applies mutations as the user interacts with the
 * editor.  The server validates each mutation before applying it to the authoritative
 * graph stored in your block entity.</p>
 *
 * <p>Not thread-safe — all mutations must happen on the server tick thread.</p>
 */
public final class NodeGraph {

    private final Map<UUID, NodeDef>  nodes       = new LinkedHashMap<>();
    private final List<ConnectionDef> connections = new ArrayList<>();

    // ── Node operations ───────────────────────────────────────────────────────

    public Map<UUID, NodeDef> nodes() {
        return Collections.unmodifiableMap(nodes);
    }

    public List<ConnectionDef> connections() {
        return Collections.unmodifiableList(connections);
    }

    public void addNode(NodeDef node) {
        nodes.put(node.id(), node);
    }

    public boolean removeNode(UUID id) {
        if (!nodes.containsKey(id)) return false;
        nodes.remove(id);
        connections.removeIf(c -> c.fromNode().equals(id) || c.toNode().equals(id));
        return true;
    }

    public boolean moveNode(UUID id, int x, int y) {
        NodeDef node = nodes.get(id);
        if (node == null) return false;
        node.setPosition(x, y);
        return true;
    }

    public boolean updateNodeSettings(UUID id, CompoundTag settings) {
        NodeDef node = nodes.get(id);
        if (node == null) return false;
        node.setSettings(settings);
        return true;
    }

    // ── Connection operations ─────────────────────────────────────────────────

    public boolean addConnection(ConnectionDef conn) {
        if (!nodes.containsKey(conn.fromNode())) return false;
        if (!nodes.containsKey(conn.toNode()))   return false;
        for (ConnectionDef existing : connections) {
            if (existing.toNode().equals(conn.toNode()) && existing.toPort() == conn.toPort()) return false;
        }
        if (conn.fromNode().equals(conn.toNode())) return false;
        connections.add(conn);
        if (hasCycle()) {
            connections.remove(connections.size() - 1);
            return false;
        }
        return true;
    }

    public boolean removeConnection(UUID id) {
        return connections.removeIf(c -> c.id().equals(id));
    }

    // ── Cycle detection ───────────────────────────────────────────────────────

    public boolean hasCycle() {
        Set<UUID> visited = new HashSet<>();
        Set<UUID> inStack = new HashSet<>();
        for (UUID id : nodes.keySet()) {
            if (dfsCycle(id, visited, inStack)) return true;
        }
        return false;
    }

    private boolean dfsCycle(UUID node, Set<UUID> visited, Set<UUID> inStack) {
        if (inStack.contains(node))  return true;
        if (visited.contains(node))  return false;
        visited.add(node);
        inStack.add(node);
        for (ConnectionDef conn : connections) {
            if (conn.fromNode().equals(node)) {
                if (dfsCycle(conn.toNode(), visited, inStack)) return true;
            }
        }
        inStack.remove(node);
        return false;
    }

    // ── Topological order ─────────────────────────────────────────────────────

    public List<UUID> topologicalOrder() {
        Map<UUID, Integer> inDegree = new HashMap<>();
        nodes.keySet().forEach(id -> inDegree.put(id, 0));
        for (ConnectionDef conn : connections) {
            inDegree.merge(conn.toNode(), 1, Integer::sum);
        }
        Queue<UUID> queue = new ArrayDeque<>();
        inDegree.forEach((id, deg) -> { if (deg == 0) queue.add(id); });
        List<UUID> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            UUID cur = queue.poll();
            order.add(cur);
            for (ConnectionDef conn : connections) {
                if (conn.fromNode().equals(cur)) {
                    int newDeg = inDegree.merge(conn.toNode(), -1, Integer::sum);
                    if (newDeg == 0) queue.add(conn.toNode());
                }
            }
        }
        return order;
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    public CompoundTag toNbt() {
        CompoundTag root = new CompoundTag();
        ListTag nodeList = new ListTag();
        for (NodeDef node : nodes.values()) {
            nodeList.add(node.toNbt());
        }
        root.put("nodes", nodeList);
        ListTag connList = new ListTag();
        for (ConnectionDef conn : connections) {
            connList.add(conn.toNbt());
        }
        root.put("connections", connList);
        return root;
    }

    public static NodeGraph fromNbt(CompoundTag root) {
        NodeGraph graph = new NodeGraph();
        if (root == null) return graph;
        Tag nodesTag = root.get("nodes");
        if (nodesTag instanceof ListTag nodeList) {
            for (Tag t : nodeList) {
                if (t instanceof CompoundTag ct) {
                    NodeDef node = NodeDef.fromNbt(ct);
                    if (node != null) graph.nodes.put(node.id(), node);
                }
            }
        }
        Tag connsTag = root.get("connections");
        if (connsTag instanceof ListTag connList) {
            for (Tag t : connList) {
                if (t instanceof CompoundTag ct) {
                    ConnectionDef conn = ConnectionDef.fromNbt(ct);
                    if (conn != null) graph.connections.add(conn);
                }
            }
        }
        return graph;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return "NodeGraph{nodes=" + nodes.size() + ", connections=" + connections.size() + "}";
    }
}
