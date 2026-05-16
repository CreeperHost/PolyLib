package net.creeperhost.testmod.screens;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Demonstrates {@link GuiFuzzySearch} populated with the full list of registered block names.
 *
 * <p>Open in-game: KP_2 (numpad 2) — registered in {@link net.creeperhost.testmod.init.TestClientEvents}.
 *
 * <p>Expected: typing in the search field filters the block list in real time;
 * clicking a result prints the selected block to chat.
 */
public class ScreenFuzzySearchDemo extends ModularGuiScreen
{
    public ScreenFuzzySearchDemo()
    {
        super(new Provider());
    }

    private static class Provider implements GuiProvider
    {
        @Override
        public void buildGui(ModularGui gui)
        {
            gui.initStandardGui(200, 220);
            gui.setGuiTitle(Component.literal("Fuzzy Search Demo"));

            GuiElement<?> root = gui.getRoot();

            GuiRectangle bg = new GuiRectangle(root).fill(0xFF1E1E1E).border(0xFF555555);
            Constraints.bind(bg, root);

            new GuiText(bg, Component.literal("Fuzzy Search — type to filter blocks"))
                    .setShadow(false).setTextColour(0xFFCCCCCC)
                    .setAlignment(Align.LEFT)
                    .constrain(TOP,    relative(bg.get(TOP), 5))
                    .constrain(HEIGHT, literal(8))
                    .constrain(LEFT,   relative(bg.get(LEFT), 5))
                    .constrain(RIGHT,  relative(bg.get(RIGHT), -5));

            // Collect all block registry names as strings
            List<String> blockNames = BuiltInRegistries.BLOCK.keySet()
                    .stream()
                    .map(Object::toString)
                    .sorted()
                    .collect(Collectors.toList());

            GuiFuzzySearch<String> search = new GuiFuzzySearch<>(
                    bg,
                    blockNames,
                    s -> s,            // string extractor
                    selected -> {      // on-select callback
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.player != null) {
                            mc.player.displayClientMessage(
                                    Component.literal("[FuzzySearchDemo] selected: " + selected), false);
                        }
                    }
            );
            search
                    .constrain(TOP,    relative(bg.get(TOP),    20))
                    .constrain(BOTTOM, relative(bg.get(BOTTOM), -5))
                    .constrain(LEFT,   relative(bg.get(LEFT),    5))
                    .constrain(RIGHT,  relative(bg.get(RIGHT),  -5));
        }
    }
}
