package net.creeperhost.testmod.blocks.mirror;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Screen for {@link MirrorContainer}. Shows all 41 mirrored player slots,
 * demonstrating that PlayerInventoryMirrorContainer correctly bridges
 * reads/writes to the live player inventory.
 */
public class ScreenMirrorTest extends ContainerGuiProvider<MirrorContainer>
{
    public static ModularGuiContainer<MirrorContainer> create(MirrorContainer menu, Inventory playerInv, Component title)
    {
        return new ModularGuiContainer<>(menu, playerInv, new ScreenMirrorTest());
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui)
    {
        GuiManipulable root = new GuiManipulable(gui).addMoveHandle(10);
        root.enableCursors(true);
        Constraints.bind(GuiRectangle.toolTipBackground(root.getContentElement()), root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<MirrorContainer> access)
    {
        MirrorContainer menu = access.getMenu();
        gui.initStandardGui(176, 132);
        gui.setGuiTitle(Component.literal("Player Inventory Mirror"));

        GuiElement<?> root = gui.getRoot();

        GuiRectangle bg = new GuiRectangle(root).fill(0xFF2D2D2D).border(0xFF888888);
        Constraints.bind(bg, root);

        GuiText title = new GuiText(bg, gui.getGuiTitle())
                .setTextColour(0xFFFFFFFF)
                .setShadow(false)
                .setAlignment(Align.LEFT)
                .constrain(TOP,    relative(bg.get(TOP),    4))
                .constrain(HEIGHT, literal(8))
                .constrain(LEFT,   relative(bg.get(LEFT),   5))
                .constrain(RIGHT,  relative(bg.get(RIGHT), -5));

        var all = GuiSlots.playerAllSlots(bg, access, menu.main, menu.hotBar, menu.armor, menu.offhand);
        all.container
                .constrain(BOTTOM, relative(bg.get(BOTTOM), -6))
                .constrain(LEFT,   match(bg.get(LEFT)))
                .constrain(RIGHT,  match(bg.get(RIGHT)));
    }
}
