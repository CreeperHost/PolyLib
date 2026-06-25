package net.creeperhost.testmod.blocks.fluidtank;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.GuiFluidTank;
import net.creeperhost.polylib.client.modulargui.elements.GuiRectangle;
import net.creeperhost.polylib.client.modulargui.elements.GuiText;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.literal;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.match;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.midPoint;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.relative;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.BOTTOM;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.HEIGHT;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.LEFT;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.RIGHT;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.TOP;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.WIDTH;

/**
 * Modular GUI screen for the test fluid tank.
 */
public class FluidTankScreen extends ContainerGuiProvider<FluidTankContainer> {
    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<FluidTankContainer> screenAccess) {
        FluidTankContainer menu = screenAccess.getMenu();
        gui.initStandardGui(120, 118);
        gui.setGuiTitle(Component.literal("Fluid Tank"));

        GuiRectangle background = new GuiRectangle(gui.getRoot())
                .shadedRect(0xFFFFFFFF, 0xFF555555, 0xFFC6C6C6)
                .constrain(WIDTH, literal(120))
                .constrain(HEIGHT, literal(118))
                .constrain(LEFT, midPoint(gui.getRoot().get(LEFT), gui.getRoot().get(RIGHT), -60))
                .constrain(TOP, midPoint(gui.getRoot().get(TOP), gui.getRoot().get(BOTTOM), -59));

        new GuiText(background, gui.getGuiTitle())
                .setTextColour(-12566464)
                .setShadow(false)
                .setAlignment(Align.CENTER)
                .constrain(TOP, relative(background.get(TOP), 8))
                .constrain(LEFT, relative(background.get(LEFT), 5))
                .constrain(RIGHT, relative(background.get(RIGHT), -5))
                .constrain(HEIGHT, literal(8));

        var tank = GuiFluidTank.simpleTank(background);
        tank.container
                .constrain(WIDTH, literal(28))
                .constrain(HEIGHT, literal(76))
                .constrain(LEFT, midPoint(background.get(LEFT), background.get(RIGHT), -14))
                .constrain(TOP, relative(background.get(TOP), 26));
        tank.primary
                .setCapacity(FluidTankBlockEntity.CAPACITY)
                .setFluidStack(menu.fluid::get);

        new GuiText(background, () -> Component.literal((menu.fluid.get().getAmount() / FluidManager.MILLIBUCKET) + " mB"))
                .setTextColour(-12566464)
                .setShadow(false)
                .setAlignment(Align.CENTER)
                .constrain(TOP, relative(tank.container.get(BOTTOM), 4))
                .constrain(LEFT, match(background.get(LEFT)))
                .constrain(RIGHT, match(background.get(RIGHT)))
                .constrain(HEIGHT, literal(8));
    }

    /**
     * Creates the Minecraft screen wrapper for this modular GUI.
     *
     * @param menu tank container menu
     * @param inventory player inventory
     * @param component vanilla screen title
     * @return modular GUI container screen
     */
    public static ModularGuiContainer<FluidTankContainer> create(FluidTankContainer menu, Inventory inventory, Component component) {
        return new ModularGuiContainer<>(menu, inventory, new FluidTankScreen());
    }
}
