package net.creeperhost.polylib.client.screenbuilder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.fluid.ScreenFluidRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreenBuilder
{
    public static final ResourceLocation DEFAULT_RESOURCE_LOCATION = new ResourceLocation(PolyLib.MOD_ID,
            "textures/gui_sheet.png");
    public final ResourceLocation resourceLocation;

    public ScreenBuilder()
    {
        this.resourceLocation = DEFAULT_RESOURCE_LOCATION;
    }

    public ScreenBuilder(ResourceLocation resourceLocation)
    {
        this.resourceLocation = resourceLocation;
    }

    public void drawDefaultBackground(Screen screen, PoseStack poseStack, int x, int y, int width, int height, int textureXSize, int textureYSize)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, x, y, 0, 0, width / 2, height / 2, textureXSize, textureYSize);
        GuiComponent.blit(poseStack, x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2, textureXSize,
                textureYSize);
        GuiComponent.blit(poseStack, x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2, textureXSize,
                textureYSize);
        GuiComponent.blit(poseStack, x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2,
                height / 2, textureXSize, textureYSize);
    }

    public void drawPlayerSlots(Screen screen, PoseStack poseStack, int posX, int posY, boolean center, int textureXSize, int textureYSize)
    {
        RenderSystem.setShaderTexture(0, resourceLocation);
        if (center)
        {
            posX -= 81;
        }
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                GuiComponent.blit(poseStack, posX + x * 18, posY + y * 18, 150, 0, 18, 18, textureXSize, textureYSize);
            }
        }
        for (int x = 0; x < 9; x++)
        {
            GuiComponent.blit(poseStack, posX + x * 18, posY + 58, 150, 0, 18, 18, textureXSize, textureYSize);
        }
    }

    public void drawSlot(Screen gui, PoseStack poseStack, int posX, int posY, int textureXSize, int textureYSize)
    {
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, posX, posY, 150, 0, 18, 18, textureXSize, textureYSize);
    }

    public void drawProgressBar(Screen gui, PoseStack poseStack, int progress, int maxProgress, int x, int y, int mouseX, int mouseY)
    {
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, x, y, 150, 18, 23, 15, 256, 256);

        int j = (int) ((double) progress / (double) maxProgress * 24);
        if (j < 0) j = 0;
        GuiComponent.blit(poseStack, x, y, 173, 18, j, 16, 256, 256);

        if (isInRect(x, y, 26, 15, mouseX, mouseY))
        {
            int percentage = percentage(maxProgress, progress);
            List<Component> list = new ArrayList<>();
            list.add(Component.literal(getPercentageColour(percentage) + "" + percentage + "%"));
            gui.renderTooltip(poseStack, list, Optional.empty(), mouseX, mouseY);
        }
    }

    public void drawTankWithOverlay(Screen gui, PoseStack poseStack, FluidStack fluidStack, int capacity, int x, int y, int height, int mouseX, int mouseY)
    {
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, x, y, 228, 18, 22, 56, 256, 256);

        ScreenFluidRenderer screenFluidRenderer = new ScreenFluidRenderer(capacity, 16, height, 0);
        screenFluidRenderer.render(x + 3, y + 3, fluidStack);
        RenderSystem.setShaderTexture(0, resourceLocation);
        GuiComponent.blit(poseStack, x + 3, y + 3, 231, 74, 16, 50, 256, 256);

        if (isInRect(x, y, 14, height, mouseX, mouseY))
        {
            List<Component> list = new ArrayList<>();
            if (fluidStack.getFluid() != null)
            {
                list.add(Component.literal(
                        fluidStack.getAmount() + " / " + capacity + " " + fluidStack.getName().getString()));
            } else
            {
                list.add(Component.literal("empty"));
            }
            gui.renderTooltip(poseStack, list, Optional.empty(), mouseX, mouseY);
        }
    }

    public void drawBar(Screen screen, PoseStack poseStack, int x, int y, int height, int value, int maxValue, int mouseX, int mouseY, Component tooltip)
    {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, resourceLocation);

        int draw = (int) ((double) value / (double) maxValue * (height - 2));
        GuiComponent.blit(poseStack, x, y, 1, 150, 13, height, 256, 256);
        GuiComponent.blit(poseStack, x + 1, y + height - draw - 1, 14, 150, 12, draw, 256, 256);

        if (isInRect(x, y, 14, height, mouseX, mouseY))
        {
            screen.renderTooltip(poseStack, tooltip, mouseX, mouseY);
        }
        poseStack.popPose();
    }

    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    public ChatFormatting getPercentageColour(int percentage)
    {
        if (percentage <= 10)
        {
            return ChatFormatting.RED;
        } else if (percentage >= 75)
        {
            return ChatFormatting.GREEN;
        } else
        {
            return ChatFormatting.YELLOW;
        }
    }

    public int percentage(int MaxValue, int CurrentValue)
    {
        if (CurrentValue == 0) return 0;
        return (int) ((CurrentValue * 100.0f) / MaxValue);
    }
}
