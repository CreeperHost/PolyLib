package net.creeperhost.testmod.nodegraph;

import net.creeperhost.polylib.client.modulargui.nodegraph.graph.INodeType;
import net.creeperhost.polylib.client.modulargui.nodegraph.graph.NodeDef;
import net.creeperhost.polylib.client.modulargui.nodegraph.graph.PortDescriptor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Test node: receives items on one input port and emits them on one output port.
 * Used to verify that {@link net.creeperhost.polylib.client.modulargui.nodegraph.NodeTypeRegistry}
 * and {@link net.creeperhost.polylib.client.modulargui.elements.GuiNodeCanvas} render a connected pair of nodes correctly.
 */
public class TestItemPassthroughNodeType implements INodeType
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath("testmod", "item_passthrough");
    public static final TestItemPassthroughNodeType INSTANCE = new TestItemPassthroughNodeType();

    private static final List<PortDescriptor> PORTS = List.of(
            PortDescriptor.itemIn(0,  "In"),
            PortDescriptor.itemOut(1, "Out")
    );

    private TestItemPassthroughNodeType() {}

    @Override
    public Identifier getTypeId() { return ID; }

    @Override
    public Component getDisplayName() { return Component.literal("Item Passthrough"); }

    @Override
    public List<PortDescriptor> getPorts(NodeDef node) { return PORTS; }
}
