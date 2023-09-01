package net.creeperhost.polylib.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.function.Supplier;

public class LoadingSpinnerWidget extends AbstractWidget
{
    private final Supplier<Boolean> active;
    private final ItemStack stack;

    public LoadingSpinnerWidget(int x, int y, int width, int height, Component component, ItemStack stack, Supplier<Boolean> active)
    {
        super(x, y, width, height, component);
        this.active = active;
        this.stack = stack;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if(active.get())
        {
            PoseStack poseStack = guiGraphics.pose();

            int ticks = (int) Minecraft.getInstance().level.getGameTime() % 80;
            int rotateTickMax = 60;
            int throbTickMax = 20;
            int rotateTicks = ticks % rotateTickMax;
            int throbTicks = ticks % throbTickMax;
            float rotationDegrees = (rotateTicks + partialTicks) * (360F / rotateTickMax);

            float scale = 1F + ((throbTicks >= (throbTickMax / 2) ? (throbTickMax - (throbTicks + partialTicks)) : (throbTicks + partialTicks)) * (2F / throbTickMax));
            poseStack.pushPose();
            poseStack.translate(getX(), getY(), 0);
            poseStack.scale(scale, scale, 1F);
            poseStack.mulPose(new Quaternionf().rotateLocalZ(rotationDegrees));
            LoadingSpinner.drawItem(poseStack, stack, 0, true, null);
            poseStack.popPose();
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
}
