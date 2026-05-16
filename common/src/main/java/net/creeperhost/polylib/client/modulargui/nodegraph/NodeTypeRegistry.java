package net.creeperhost.polylib.client.modulargui.nodegraph;

import net.creeperhost.polylib.client.modulargui.nodegraph.graph.INodeType;
import net.minecraft.resources.Identifier;

import java.util.*;

/**
 * Central registry for all {@link INodeType} implementations.
 *
 * <p>Call {@link #register(INodeType)} during mod initialisation (before any world loads).
 * This is a simple static map — no DeferredRegister is needed because node types do not
 * need world/registry context during registration.</p>
 */
public final class NodeTypeRegistry {

    private NodeTypeRegistry() {}

    private static final Map<Identifier, INodeType> REGISTRY = new LinkedHashMap<>();

    public static void register(INodeType type) {
        Identifier id = type.getTypeId();
        if (REGISTRY.containsKey(id)) {
            throw new IllegalStateException("NodeTypeRegistry: duplicate registration for " + id);
        }
        REGISTRY.put(id, type);
    }

    public static Optional<INodeType> get(Identifier id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    public static Collection<INodeType> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static boolean contains(Identifier id) {
        return REGISTRY.containsKey(id);
    }
}
