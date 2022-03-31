package net.creeperhost.polylib.client.screenbuilder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class ScreenBuilder
{
    public static final ResourceLocation DEFAULT_RESOURCE_LOCATION = new ResourceLocation(PolyLib.MOD_ID, "textures/gui_sheet.png");
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
        GuiComponent.blit(poseStack, x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2, textureXSize, textureYSize );
        GuiComponent.blit(poseStack, x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2, textureXSize, textureYSize);
        GuiComponent.blit(poseStack, x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2, textureXSize, textureYSize);
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

    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    public int percentage(int MaxValue, int CurrentValue)
    {
        if (CurrentValue == 0) return 0;
        return (int) ((CurrentValue * 100.0f) / MaxValue);
    }
}
