package net.creeperhost.testmod.screens;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.nodegraph.graph.*;
import net.minecraft.network.chat.Component;

import java.util.UUID;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Demonstrates {@link GuiNodeCanvas} with two pre-built test nodes.
 *
 * <p>Open in-game: KP_4 (numpad 4) — registered in {@link net.creeperhost.testmod.init.TestClientEvents}.
 *
 * <p>Expected:
 * <ul>
 *   <li>Two nodes visible on the canvas (Signal Source + Item Passthrough)</li>
 *   <li>Middle-drag to pan, scroll to zoom</li>
 *   <li>Click a port dot and drag to another compatible port to wire them</li>
 *   <li>On NeoForge: bezier wire renders via GPU shader</li>
 *   <li>On Fabric: flat-line fallback (no crash)</li>
 * </ul>
 */
public class ScreenNodeGraphDemo extends ModularGuiScreen
{
    public ScreenNodeGraphDemo()
    {
        super(new Provider());
    }

    private static class Provider implements GuiProvider
    {
        @Override
        public void buildGui(ModularGui gui)
        {
            gui.initFullscreenGui();
            gui.setGuiTitle(Component.literal("Node Graph Demo"));

            GuiElement<?> root = gui.getRoot();

            new GuiText(root, Component.literal(
                    "Node Graph Demo — pan: middle-drag | zoom: scroll | wire: click port dot"))
                    .setShadow(true).setTextColour(0xFFFFFFFF)
                    .constrain(TOP,    literal(4))
                    .constrain(HEIGHT, literal(8))
                    .constrain(LEFT,   literal(4))
                    .constrain(RIGHT,  relative(root.get(RIGHT), -4));

            // ── Build a small test graph ───────────────────────────────────────
            NodeGraph graph = new NodeGraph();

            UUID sourceId      = UUID.randomUUID();
            UUID passthroughId = UUID.randomUUID();

            graph.addNode(new NodeDef(sourceId,
                    net.creeperhost.testmod.nodegraph.TestSignalSourceNodeType.ID,
                    80, 80, null));
            graph.addNode(new NodeDef(passthroughId,
                    net.creeperhost.testmod.nodegraph.TestItemPassthroughNodeType.ID,
                    260, 80, null));

            // ── Canvas ────────────────────────────────────────────────────────
            GuiNodeCanvas canvas = new GuiNodeCanvas(root);
            canvas
                    .setGraph(graph)
                    .constrain(TOP,    literal(16))
                    .constrain(BOTTOM, match(root.get(BOTTOM)))
                    .constrain(LEFT,   match(root.get(LEFT)))
                    .constrain(RIGHT,  match(root.get(RIGHT)));

            GuiRectangle canvasBg = new GuiRectangle(canvas).fill(0xFF0D0D0D);
            Constraints.bind(canvasBg, canvas);
        }
    }
}
