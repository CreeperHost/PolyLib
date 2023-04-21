package net.creeperhost.testmod.blocks.inventorytestblock;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.client.screen.widget.buttons.ButtonInfoTab;
import net.creeperhost.polylib.client.screen.widget.buttons.ButtonItemStack;
import net.creeperhost.polylib.client.screen.widget.buttons.ButtonRedstoneControl;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.creeperhost.polylib.data.EnumRedstoneState;
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
        addRenderableWidget(new Button.Builder(Component.empty(), button ->
        {

        }).pos(leftPos + imageWidth - 29, topPos + 10).size(20, 20).build());

        addRenderableWidget(new Button.Builder(Component.literal("i"), button ->
        {

        }).pos(leftPos - 20, topPos).size(20, 20).build());
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float f, int mouseX, int mouseY)
    {
        int progress = getMenu().getContainerData().get(0);
        renderBackground(poseStack);
        screenBuilder.drawDefaultBackground(this, poseStack, leftPos, topPos, imageWidth, imageHeight, 256, 256);
        screenBuilder.drawPlayerSlots(this, poseStack, leftPos + imageWidth / 2, topPos + 131, true, 256, 256);

        screenBuilder.drawSlot(this, poseStack, leftPos + 40, topPos + 60, 256, 256);
        screenBuilder.drawSlot(this, poseStack, leftPos + 120, topPos + 60, 256, 256);

        screenBuilder.drawProgressBar(this, poseStack, progress, 100, leftPos + 80, topPos + 60, mouseX, mouseY);
        FluidStack fluidStack = FluidStack.create(Fluids.WATER, progress * 10);
        screenBuilder.drawTankWithOverlay(this, poseStack, fluidStack, 1000, leftPos + imageWidth - 30, topPos + 40, 49, mouseX, mouseY);
        screenBuilder.drawBar(this, poseStack, leftPos + 10, topPos + 20, 80, progress, 100, mouseX, mouseY, Component.literal(progress + " FE"));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        super.render(poseStack, i, j, f);

        renderTooltip(poseStack, i, j);
    }
}
