package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ButtonInfoTab extends PolyButton
{
    boolean isOpen = false;
    boolean isOpening = false;
    boolean isClosing = false;
    ScreenBuilder screenBuilder = new ScreenBuilder();
    Screen screen;

    public ButtonInfoTab(Screen screen, int i, int j, int k, int l, Component component, OnPress onPress)
    {
        super(i, j, k, l, component, onPress, DEFAULT_NARRATION);
        this.screen = screen;
    }

    int progress = 0;

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        int start = (getX() + 19) - progress;
        if (isOpening)
        {
            if (progress < 110) progress++;
            if (progress >= 110)
            {
                progress = 110;
                isClosing = false;
                isOpening = false;
                isOpen = true;
            }
        }
        if (isClosing)
        {
            if (progress > 0) progress--;
            if (progress <= 0)
            {
                progress = 0;
                isClosing = false;
                isOpening = false;
                isOpen = false;
            }
        }

        if (isOpening || isClosing || isOpen)
        {
            screenBuilder.drawDefaultBackground(screen, poseStack, start, getY(), progress, progress * 2, 256, 256);
        }
        if (!isOpening && !isClosing && !isOpen || progress < 20)
        {
            screenBuilder.drawDefaultBackground(screen, poseStack, getX() - 1, getY(), 20, 20, 256, 256);
        }
        drawCenteredString(poseStack, Minecraft.getInstance().font, this.getMessage(), getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2, -1);
        if (isOpen && !isOpening && !isClosing)
        {
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.getX() - 84,
                    this.getY() + 20, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.getX() - 84,
                    this.getY() + 40, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.getX() - 84,
                    this.getY() + 60, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.getX() - 84,
                    this.getY() + 80, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.getX() - 84,
                    this.getY() + 100, -1);

        }
    }

    @Override
    public void onClick(double d, double e)
    {
        super.onClick(d, e);
        if (isOpen)
        {
            isOpening = false;
            isClosing = true;
        } else
        {
            isOpening = true;
            isClosing = false;
        }
    }
}
