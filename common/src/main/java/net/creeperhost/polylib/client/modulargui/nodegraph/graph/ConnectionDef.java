package net.creeperhost.polylib.client.modulargui.nodegraph.graph;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * Immutable description of one directed wire between two node ports.
 *
 * <p>A connection is valid iff:
 * <ul>
 *   <li>{@code fromNode} exists in the graph and has an OUT port at {@code fromPort}.</li>
 *   <li>{@code toNode} exists and has an IN port at {@code toPort}.</li>
 *   <li>The two port data types are compatible per {@link PortDescriptor.PortDataType#compatible}.</li>
 *   <li>Adding this connection does not create a cycle in the graph.</li>
 * </ul>
 */
public record ConnectionDef(UUID id, UUID fromNode, int fromPort, UUID toNode, int toPort) {

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id",        id.toString());
        tag.putString("from_node", fromNode.toString());
        tag.putInt("from_port",    fromPort);
        tag.putString("to_node",   toNode.toString());
        tag.putInt("to_port",      toPort);
        return tag;
    }

    public static ConnectionDef fromNbt(CompoundTag tag) {
        try {
            UUID id       = UUID.fromString(tag.getString("id").orElse(""));
            UUID fromNode = UUID.fromString(tag.getString("from_node").orElse(""));
            int  fromPort = tag.getInt("from_port").orElse(0);
            UUID toNode   = UUID.fromString(tag.getString("to_node").orElse(""));
            int  toPort   = tag.getInt("to_port").orElse(0);
            return new ConnectionDef(id, fromNode, fromPort, toNode, toPort);
        } catch (Exception e) {
            return null;
        }
    }
}
