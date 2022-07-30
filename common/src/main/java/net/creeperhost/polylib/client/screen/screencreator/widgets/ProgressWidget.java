package net.creeperhost.polylib.client.screen.screencreator.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ProgressWidget extends Button
{
    ScreenBuilder screenBuilder = new ScreenBuilder();
    private int progress = 0;
    private int maxProgress = 100;

    public ProgressWidget(int i, int j, int k, int l, Component component, OnPress onPress)
    {
        super(i, j, k, l, component, onPress);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        screenBuilder.drawProgressBar(Minecraft.getInstance().screen, poseStack, progress, maxProgress, x, y, i, j);
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }

    public void setMaxProgress(int maxProgress)
    {
        this.maxProgress = maxProgress;
    }
}
