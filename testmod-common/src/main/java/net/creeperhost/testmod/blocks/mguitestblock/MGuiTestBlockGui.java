package net.creeperhost.testmod.blocks.mguitestblock;

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
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Function;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

public class MGuiTestBlockGui extends ContainerGuiProvider<MGuiTestBlockContainerMenu> implements DynamicTextures {

    private String backgroundTexture;

    @Override
    public void makeTextures(Function<DynamicTextures.DynamicTexture, String> textures) {
        backgroundTexture = dynamicTexture(textures, new ResourceLocation(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla"), new ResourceLocation(TestMod.MOD_ID, "textures/gui/mgui_test_block"), 226, 220, 4);
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        return new GuiDVD(gui);
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<MGuiTestBlockContainerMenu> screenAccess) {
        MGuiTestBlockContainerMenu menu = screenAccess.getMenu();
        gui.initStandardGui(226, 220);
        gui.setGuiTitle(menu.blockEntity.getDisplayName());

        //This is just something I did for fun, Take a look at ModularGuiTest to see an example of how the root element should be setup.
        GuiElement<?> root = gui.getRoot();
        GuiTexture background = new GuiTexture(root, TestModTextures.get(backgroundTexture));
        Constraints.bind(background, root);
        GuiDVD dvd = (GuiDVD) gui.getDirectRoot();
        dvd.onBounce(bounce -> background.setColour(0xFF000000 | ChatFormatting.getById(1 + (bounce % 15)).getColor()));

        //Ok. no more memes beyond this point.

        //Setup Gui Title element
        GuiText title = new GuiText(background, gui.getGuiTitle())
                .setTextColour(0x404040)
                .setShadow(false)
                .constrain(TOP, relative(background.get(TOP), 5))
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(LEFT, relative(background.get(LEFT), 5))
                .constrain(RIGHT, relative(background.get(RIGHT), -5));

        //Setup Player Inventory element, This controls the rendering of inventory slots AND the stacks in those slots. It also handles positioning the slots.
        var inventory = GuiSlots.playerAllSlots(background, screenAccess, menu.main, menu.hotBar, menu.armor, menu.offhand);
        inventory.container
                .constrain(WIDTH, null)
                .constrain(LEFT, match(background.get(LEFT)))
                .constrain(RIGHT, match(background.get(RIGHT)))
                .constrain(BOTTOM, relative(background.get(BOTTOM), -6));

        //Player inventory label (TODO, Add an option to include this in GuiSlots creation methods)
        GuiText invLabel = new GuiText(background, Component.translatable("container.inventory"))
                .setTextColour(0x404040)
                .setShadow(false)
                .setAlignment(Align.LEFT)
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(BOTTOM, relative(inventory.container.get(TOP), -3))
                .constrain(LEFT, relative(inventory.getPart(1).get(LEFT), 0))
                .constrain(RIGHT, relative(inventory.primary.get(RIGHT), 0));

        //Add Machine input slots
        int inputSpacing = 8;
        Material[] inputIcons = new Material[]{PolyTextures.get("slots/dust"), PolyTextures.get("slots/bucket"), PolyTextures.get("slots/bucket")};
        GuiSlots inputSlots = new GuiSlots(background, screenAccess, menu.machineInputs, 3)
                .setXSlotSpacing(inputSpacing)
                .setEmptyIcon(slot -> inputIcons[slot])
                .constrain(LEFT, match(invLabel.get(LEFT)))
                .constrain(BOTTOM, relative(invLabel.get(TOP), -4));

        //Add Energy Bar
        var energyBar = GuiEnergyBar.simpleBar(background);
        energyBar.container
                .constrain(LEFT, match(inputSlots.get(LEFT)))
                .constrain(BOTTOM, relative(inputSlots.get(TOP), -2))
                .constrain(WIDTH, literal(18))
                .constrain(TOP, relative(title.get(BOTTOM), 8));
        energyBar.primary
                .setCapacity(() -> (long) menu.maxEnergy.get())
                .setEnergy(() -> (long) menu.energy.get());

        //Add Tanks
        var lavaTank = GuiFluidTank.simpleTank(background);
        lavaTank.container
                .constrain(BOTTOM, match(energyBar.container.get(BOTTOM)))
                .constrain(LEFT, relative(energyBar.container.get(RIGHT), inputSpacing))
                .constrain(WIDTH, literal(18))
                .constrain(TOP, match(energyBar.container.get(TOP)));
        lavaTank.primary
                .setCapacity(() -> (long) menu.tankCap.get())
                .setFluidStack(menu.lavaTank::get);

        var waterTank = GuiFluidTank.simpleTank(background);
        waterTank.container
                .constrain(BOTTOM, match(lavaTank.container.get(BOTTOM)))
                .constrain(LEFT, relative(lavaTank.container.get(RIGHT), inputSpacing))
                .constrain(WIDTH, literal(18))
                .constrain(TOP, match(lavaTank.container.get(TOP)));
        waterTank.primary
                .setCapacity(() -> (long) menu.tankCap.get())
                .setFluidStack(menu.waterTank::get);

        //Add Progress Icon
        GuiProgressIcon progress = new GuiProgressIcon(background)
                .setBackground(PolyTextures.get("widgets/progress_arrow_empty"))
                .setAnimated(PolyTextures.get("widgets/progress_arrow_full"))
                .setProgress(() -> menu.progressSync.get() / 100D)
                .setTooltipSingle(() -> Component.literal(menu.progressSync.get() + "%"))
                .setTooltipDelay(0)
                .constrain(TOP, midPoint(waterTank.container.get(TOP), inputSlots.get(BOTTOM), -8))
                .constrain(LEFT, midPoint(background.get(LEFT), background.get(RIGHT), -11))
                .constrain(WIDTH, literal(22))
                .constrain(HEIGHT, literal(16));

        //Add Output Scrolling Window
        var scrollWindow = GuiScrolling.simpleScrollWindow(background, true, false);
        scrollWindow.container
                .constrain(TOP, match(waterTank.container.get(TOP)))
                .constrain(RIGHT, match(inventory.getPart(2).get(RIGHT)))
                .constrain(WIDTH, literal((3 * 18) + 12)) //Width is 3 slots plus 12 for the scroll bar and background border
                .constrain(BOTTOM, match(inputSlots.get(BOTTOM)));

        //Add output slots to scroll element's content element.
        GuiElement<?> content = scrollWindow.primary.getContentElement();
        GuiSlots outputSlots = new GuiSlots(content, screenAccess, menu.machineOutputs, 3)
                .constrain(TOP, match(content.get(TOP)))
                .constrain(LEFT, match(content.get(LEFT)));

        //Ok
        GuiButton dvdButton = GuiButton.vanillaAnimated(background, Component.literal("DVD"))
                .onPress(dvd::start)
                .constrain(LEFT, relative(inputSlots.get(RIGHT), 8))
                .constrain(RIGHT, relative(scrollWindow.container.get(LEFT), -8))
                .constrain(BOTTOM, match(inputSlots.get(BOTTOM)))
                .constrain(HEIGHT, literal(18));

    }

    public static ModularGuiContainer<MGuiTestBlockContainerMenu> create(MGuiTestBlockContainerMenu menu, Inventory inventory, Component component) {
        return new ModularGuiContainer<>(menu, inventory, new MGuiTestBlockGui());
    }
}
