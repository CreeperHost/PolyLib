package net.creeperhost.polylib.client.modulargui.nodegraph.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Contract that every automation node type must fulfil.
 *
 * <p>Implementations are registered via {@link net.creeperhost.polylib.client.modulargui.nodegraph.NodeTypeRegistry}
 * and must be singletons — one instance per type, shared across all {@link NodeDef} instances
 * that reference the same {@link #getTypeId()}.</p>
 *
 * <p>The GUI side (settings widget construction) is intentionally left out of this
 * interface so the common module stays free of client dependencies.  Mods may provide
 * an additional {@code INodeTypeGui} interface on the client side that extends this one
 * and adds {@code buildSettingsWidget}.</p>
 */
public interface INodeType {

    /** Unique identifier for this node type.  Must never change after release. */
    Identifier getTypeId();

    /** Human-readable name shown in the node header and palette. */
    Component getDisplayName();

    /**
     * Returns the port layout for a given node instance.
     *
     * <p>The list is stable: a specific index always refers to the same logical port.
     * If a node's settings change the number of ports, old connections referencing
     * now-absent port indices are considered broken and should be pruned.</p>
     *
     * @param node the node instance whose settings may influence the port layout
     * @return immutable ordered list of port descriptors
     */
    List<PortDescriptor> getPorts(NodeDef node);

    /**
     * Returns default settings for a newly created node of this type.
     * The returned tag is copied into the new {@link NodeDef} — callers may mutate it.
     */
    default CompoundTag defaultSettings() {
        return new CompoundTag();
    }

    /**
     * Called once per server tick for each node of this type in each active graph.
     * Implementations read from input ports, perform their logic, and write results.
     *
     * @param node the node definition (position, settings, UUID)
     */
    default void evaluate(NodeDef node) {
        // No-op by default.
    }
}
