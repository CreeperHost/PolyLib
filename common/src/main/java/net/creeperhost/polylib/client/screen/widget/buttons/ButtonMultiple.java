package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ButtonMultiple extends PolyButton
{
    private final ResourceLocation resourceLocation;
    private final int index;

    public ButtonMultiple(int xPos, int yPos, int index, ResourceLocation resourceLocation, OnPress onPress)
    {
        super(xPos, yPos, 20, 20, Component.empty(), onPress, DEFAULT_NARRATION);
        this.index = index;
        this.resourceLocation = resourceLocation;
    }

    public int getY()
    {
        if (!active) return 40;
        return isHovered ? this.height : 0;
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        if (this.visible)
        {
            RenderSystem.setShaderTexture(0, resourceLocation);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(poseStack, getX(), getY(), index * 20, getY(), this.width, this.height);
        }
    }
}
