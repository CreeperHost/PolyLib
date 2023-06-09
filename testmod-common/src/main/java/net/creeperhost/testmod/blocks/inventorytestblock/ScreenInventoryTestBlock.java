package net.creeperhost.testmod.blocks.inventorytestblock;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.client.screen.widget.buttons.ButtonInfoTab;
import net.creeperhost.polylib.client.screen.widget.buttons.ButtonItemStack;
import net.creeperhost.polylib.client.screen.widget.buttons.ButtonRedstoneControl;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.creeperhost.polylib.data.EnumRedstoneState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class ScreenInventoryTestBlock extends AbstractContainerScreen<ContainerInventoryTestBlock>
{
    ScreenBuilder screenBuilder = new ScreenBuilder();

    public ScreenInventoryTestBlock(ContainerInventoryTestBlock abstractContainerMenu, Inventory inventory, Component component)
    {
        super(abstractContainerMenu, inventory, component);
        this.imageWidth = 190;
        this.imageHeight = 220;
        this.inventoryLabelY = 118;
    }

    @Override
    protected void init()
    {
        super.init();
        addRenderableWidget(new ButtonRedstoneControl(this, leftPos + imageWidth - 29, topPos + 10, 20, 20, EnumRedstoneState.IGNORED, button ->
        {

        }));

        addRenderableWidget(new ButtonInfoTab(this, leftPos - 20, topPos, 20, 20, Component.literal("i"), button ->
        {

        }));
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float f, int mouseX, int mouseY)
    {
        int progress = getMenu().getContainerData().get(0);
        renderBackground(guiGraphics);
        screenBuilder.drawDefaultBackground(guiGraphics, leftPos, topPos, imageWidth, imageHeight, 256, 256);
        screenBuilder.drawPlayerSlots(guiGraphics, leftPos + imageWidth / 2, topPos + 131, true, 256, 256);

        screenBuilder.drawSlot(guiGraphics, leftPos + 40, topPos + 60, 256, 256);
        screenBuilder.drawSlot(guiGraphics, leftPos + 120, topPos + 60, 256, 256);

        screenBuilder.drawProgressBar(guiGraphics, progress, 100, leftPos + 80, topPos + 60, mouseX, mouseY);
        FluidStack fluidStack = FluidStack.create(Fluids.WATER, progress * 10);
        screenBuilder.drawTankWithOverlay(guiGraphics, fluidStack, 1000, leftPos + imageWidth - 30, topPos + 40, 49, mouseX, mouseY);
        screenBuilder.drawBar(guiGraphics, leftPos + 10, topPos + 20, 80, progress, 100, mouseX, mouseY, Component.literal(progress + " FE"));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int i, int j, float f)
    {
        super.render(guiGraphics, i, j, f);

        renderTooltip(guiGraphics, i, j);
    }
}
