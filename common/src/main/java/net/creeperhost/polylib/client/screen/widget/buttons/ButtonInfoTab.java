package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ButtonInfoTab extends Button
{
    boolean isOpen = false;
    boolean isOpening = false;
    boolean isClosing = false;
    ScreenBuilder screenBuilder = new ScreenBuilder();
    Screen screen;

    public ButtonInfoTab(Screen screen, int i, int j, int k, int l, Component component, OnPress onPress)
    {
        super(i, j, k, l, component, onPress);
        this.screen = screen;
    }

    int progress = 0;

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        int start = (x + 19) - progress;
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
            screenBuilder.drawDefaultBackground(screen, poseStack, start, y, progress, progress * 2, 256, 256);
        }
        if (!isOpening && !isClosing && !isOpen || progress < 20)
        {
            screenBuilder.drawDefaultBackground(screen, poseStack, x - 1, y, 20, 20, 256, 256);
        }
        drawCenteredString(poseStack, Minecraft.getInstance().font, this.getMessage(), this.x + this.width / 2,
                this.y + (this.height - 8) / 2, -1);
        if (isOpen && !isOpening && !isClosing)
        {
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.x - 84,
                    this.y + 20, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.x - 84,
                    this.y + 40, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.x - 84,
                    this.y + 60, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.x - 84,
                    this.y + 80, -1);
            drawString(poseStack, Minecraft.getInstance().font, Component.literal("This is a sentence"), this.x - 84,
                    this.y + 100, -1);

        }
    }

    @Override
    public void onPress()
    {
        super.onPress();
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
