package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.DynamicTextures;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Function;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.RIGHT;

public class ScreenInventoryTestBlock extends ContainerGuiProvider<ContainerInventoryTestBlock> implements DynamicTextures
{
    private String BACKGROUND_TEXTURE;

    @Override
    public void makeTextures(Function<DynamicTexture, String> textures)
    {
        BACKGROUND_TEXTURE = dynamicTexture(textures, new ResourceLocation(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla"),
                new ResourceLocation(TestMod.MOD_ID, "textures/gui/mgui_test_block"), 226, 220, 4);
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui)
    {
        GuiManipulable root = new GuiManipulable(gui)
                .addResizeHandles(4, false)
                .addMoveHandle(10);
        root.enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), TestModTextures.get(BACKGROUND_TEXTURE)).dynamicTexture();
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ContainerInventoryTestBlock> screenAccess)
    {
        ContainerInventoryTestBlock menu = screenAccess.getMenu();
        gui.initStandardGui(226, 220);
        gui.setGuiTitle(Component.literal("Test Machine"));

        GuiElement<?> root = gui.getRoot();
        GuiTexture background = new GuiTexture(root, TestModTextures.get(BACKGROUND_TEXTURE));
        Constraints.bind(background, root);

        GuiText title = new GuiText(background, gui.getGuiTitle())
                .setTextColour(0x404040)
                .setShadow(false)
                .constrain(TOP, relative(background.get(TOP), 5))
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(LEFT, relative(background.get(LEFT), 5))
                .constrain(RIGHT, relative(background.get(RIGHT), -5));

        var inventory = GuiSlots.playerAllSlots(background, screenAccess, menu.main, menu.hotBar, menu.armor, menu.offhand);
        inventory.container
                .constrain(WIDTH, null)
                .constrain(LEFT, match(background.get(LEFT)))
                .constrain(RIGHT, match(background.get(RIGHT)))
                .constrain(BOTTOM, relative(background.get(BOTTOM), -6));

        GuiText invLabel = new GuiText(background, Component.translatable("container.inventory"))
                .setTextColour(0x404040)
                .setShadow(false)
                .setAlignment(Align.LEFT)
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(BOTTOM, relative(inventory.container.get(TOP), -3))
                .constrain(LEFT, relative(inventory.getPart(1).get(LEFT), 0))
                .constrain(RIGHT, relative(inventory.primary.get(RIGHT), 0));

        int inputSpacing = 8;
        GuiSlots inputSlots = new GuiSlots(background, screenAccess, menu.machineInputs, 1)
                .setXSlotSpacing(inputSpacing)
                .setEmptyIcon(slot -> PolyTextures.get("slots/dust"))
                .constrain(LEFT, match(inventory.primary.get(LEFT)))
                .constrain(BOTTOM, midPoint(title.get(TOP), invLabel.get(TOP)));

        GuiSlots outSlots = new GuiSlots(background, screenAccess, menu.machineOutputs, 1)
                .setXSlotSpacing(inputSpacing)
                .setEmptyIcon(slot -> PolyTextures.get("slots/dust"))
                .constrain(RIGHT, match(inventory.primary.get(RIGHT)))
                .constrain(BOTTOM, midPoint(title.get(TOP), invLabel.get(TOP)));

        GuiProgressIcon progress = new GuiProgressIcon(background)
                .setBackground(PolyTextures.get("widgets/progress_arrow_empty"))
                .setAnimated(PolyTextures.get("widgets/progress_arrow_full"))
                .setProgress(() -> menu.progressSync.get() / 100D)
                .setTooltipSingle(() -> Component.literal(menu.progressSync.get() + "%"))
                .setTooltipDelay(0)
                .constrain(TOP, midPoint(inputSlots.get(TOP), inputSlots.get(BOTTOM), -8))
                .constrain(LEFT, midPoint(background.get(LEFT), background.get(RIGHT), -11))
                .constrain(WIDTH, literal(22))
                .constrain(HEIGHT, literal(16));
    }

    public static ModularGuiContainer<ContainerInventoryTestBlock> create(ContainerInventoryTestBlock menu, Inventory inventory, Component component)
    {
        return new ModularGuiContainer<>(menu, inventory, new ScreenInventoryTestBlock());
    }
}
