package net.creeperhost.polylib.client.modulargui.nodegraph.graph;

/**
 * Describes a single input or output port on a node.
 *
 * <p>Ports are identified by their {@link #index()} within the list returned by
 * {@link INodeType#getPorts(NodeDef)}.  Indices are stable — a node type must
 * never change what port lives at a given index, or all saved connections become
 * invalid.</p>
 */
public record PortDescriptor(int index, String label, PortDirection direction, PortDataType dataType) {

    // ── Direction ─────────────────────────────────────────────────────────────

    public enum PortDirection {
        /** This port receives data / items / signals from an upstream node. */
        IN,
        /** This port sends data / items / signals to a downstream node. */
        OUT
    }

    // ── Data types ────────────────────────────────────────────────────────────

    public enum PortDataType {
        /** Transfers {@link net.minecraft.world.item.ItemStack} streams. */
        ITEMS,
        /** Transfers fluid amounts (mB units). */
        FLUID,
        /** Boolean signal — high (true) or low (false). */
        SIGNAL,
        /** Accepts any data type — used by pass-through nodes like Relay. */
        ANY;

        /**
         * Returns true if a connection from {@code outType} → {@code inType} is type-compatible.
         * Any side being {@link #ANY} makes the connection valid.
         */
        public static boolean compatible(PortDataType outType, PortDataType inType) {
            if (outType == ANY || inType == ANY) return true;
            return outType == inType;
        }
    }

    // ── Factory helpers ───────────────────────────────────────────────────────

    public static PortDescriptor itemIn(int index, String label) {
        return new PortDescriptor(index, label, PortDirection.IN, PortDataType.ITEMS);
    }

    public static PortDescriptor itemOut(int index, String label) {
        return new PortDescriptor(index, label, PortDirection.OUT, PortDataType.ITEMS);
    }

    public static PortDescriptor signalIn(int index, String label) {
        return new PortDescriptor(index, label, PortDirection.IN, PortDataType.SIGNAL);
    }

    public static PortDescriptor signalOut(int index, String label) {
        return new PortDescriptor(index, label, PortDirection.OUT, PortDataType.SIGNAL);
    }

    public static PortDescriptor anyIn(int index, String label) {
        return new PortDescriptor(index, label, PortDirection.IN, PortDataType.ANY);
    }

    public static PortDescriptor anyOut(int index, String label) {
        return new PortDescriptor(index, label, PortDirection.OUT, PortDataType.ANY);
    }
}
