package net.creeperhost.testmod.blocks.inventorytestblock;

import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.elements.TextInputDialog;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.DynamicTextures;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Align;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import java.util.UUID;
import java.util.function.Function;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

public class ScreenInventoryTest extends ContainerGuiProvider<ContainerInventoryTest> implements DynamicTextures
{
    private String BACKGROUND_TEXTURE;

    @Override
    public void makeTextures(Function<DynamicTexture, String> textures)
    {
        BACKGROUND_TEXTURE = dynamicTexture(textures, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/dynamic/gui_vanilla"),
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/dynamic/gui_vanilla"), 226, 220, 4);
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui)
    {
        GuiManipulable root = new GuiManipulable(gui)
                .addResizeHandles(4, false)
                .addMoveHandle(10);
        root.enableCursors(true);
        GuiTexture bg = new GuiTexture(root.getContentElement(), PolyTextures.get(BACKGROUND_TEXTURE)).dynamicTexture();
        Constraints.bind(bg, root.getContentElement());
        return root;
    }

    @Override
    public void buildGui(ModularGui gui, ContainerScreenAccess<ContainerInventoryTest> screenAccess)
    {
        ContainerInventoryTest menu = screenAccess.getMenu();
        gui.initStandardGui(226, 220);
        gui.setGuiTitle(Component.literal("Test Machine"));

        GuiElement<?> root = gui.getRoot();
        GuiTexture background = new GuiTexture(root, PolyTextures.get(BACKGROUND_TEXTURE));
        Constraints.bind(background, root);

        GuiText title = new GuiText(background, gui.getGuiTitle())
                .setTextColour(-12566464)
                .setShadow(false)
                .constrain(TOP, relative(background.get(TOP), 10))
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
                .setTextColour(-12566464)
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
                .setTooltip(Component.literal("I'm a slot"))
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

        var energyBar = GuiEnergyBar.simpleBar(background);
        energyBar.container
                .constrain(LEFT, midPoint(background.get(LEFT), inputSlots.get(LEFT), -6))
                .constrain(BOTTOM, relative(invLabel.get(TOP), -6))
                .constrain(WIDTH, literal(18))
                .constrain(TOP, relative(title.get(BOTTOM), 8));
        energyBar.primary
                .setCapacity(() -> (long) menu.maxEnergy.get())
                .setEnergy(() -> (long) menu.energy.get());


        BlockEntityInventoryTest blockEntity = menu.blockEntity;
        GuiButton clientToServerPacketTest = GuiButton.vanilla(root, Component.literal("Send Test Packet"))
                .onPress(() -> blockEntity.sendPacketToServer(0, buf -> {}))
                .constrain(TOP, literal(10))
                .constrain(LEFT, literal(10))
                .constrain(WIDTH, literal(100))
                .constrain(HEIGHT, literal(15));

        // StringData test: editable block label, CLIENT_CONTROL sends value to server
        GuiText labelDisplay = new GuiText(root, () -> Component.literal("Label: " + blockEntity.testLabel.get()))
                .setScroll(false)
                .setAlignment(Align.MIN)
                .constrain(TOP, relative(clientToServerPacketTest.get(BOTTOM), 4))
                .constrain(LEFT, literal(10))
                .constrain(WIDTH, literal(200))
                .constrain(HEIGHT, literal(8));

        GuiButton editLabelButton = GuiButton.vanilla(root, Component.literal("Edit Label"))
                .onPress(() -> TextInputDialog
                        .simpleDialog(root, Component.literal("Set Block Label"), blockEntity.testLabel.get())
                        .setResultCallback(s -> blockEntity.sendDataValueToServer(blockEntity.testLabel, s)))
                .constrain(TOP, relative(labelDisplay.get(BOTTOM), 2))
                .constrain(LEFT, literal(10))
                .constrain(WIDTH, literal(80))
                .constrain(HEIGHT, literal(15));

        // UUIDData test: read-only display of last player UUID who opened this container
        new GuiText(root, () -> {
                    UUID uuid = blockEntity.lastVisitorUUID.get();
                    return Component.literal("Visitor: " + (uuid != null ? uuid.toString().substring(0, 8) + "..." : "none"));
                })
                .setScroll(false)
                .setAlignment(Align.MIN)
                .constrain(TOP, relative(editLabelButton.get(BOTTOM), 4))
                .constrain(LEFT, literal(10))
                .constrain(WIDTH, literal(200))
                .constrain(HEIGHT, literal(8));
    }

    public static ModularGuiContainer<ContainerInventoryTest> create(ContainerInventoryTest menu, Inventory inventory, Component component)
    {
        return new ModularGuiContainer<>(menu, inventory, new ScreenInventoryTest());
    }
}
