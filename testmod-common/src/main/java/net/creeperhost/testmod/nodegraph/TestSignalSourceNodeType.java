package net.creeperhost.testmod.nodegraph;

import net.creeperhost.polylib.client.modulargui.nodegraph.graph.INodeType;
import net.creeperhost.polylib.client.modulargui.nodegraph.graph.NodeDef;
import net.creeperhost.polylib.client.modulargui.nodegraph.graph.PortDescriptor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Test node: a signal source that emits a constant HIGH signal on its output port.
 * Accepts no inputs. Used to verify signal-type port rendering in the node canvas.
 */
public class TestSignalSourceNodeType implements INodeType
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath("testmod", "signal_source");
    public static final TestSignalSourceNodeType INSTANCE = new TestSignalSourceNodeType();

    private static final List<PortDescriptor> PORTS = List.of(
            PortDescriptor.signalOut(0, "Signal")
    );

    private TestSignalSourceNodeType() {}

    @Override
    public Identifier getTypeId() { return ID; }

    @Override
    public Component getDisplayName() { return Component.literal("Signal Source"); }

    @Override
    public List<PortDescriptor> getPorts(NodeDef node) { return PORTS; }
}
