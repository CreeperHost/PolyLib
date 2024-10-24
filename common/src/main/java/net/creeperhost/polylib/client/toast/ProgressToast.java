package net.creeperhost.polylib.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ProgressToast extends PolyToast
{
    private final Component title;
    private float progress;
    private float lastProgress;
    private long lastProgressTime;
    private final ResourceLocation iconResourceLocation;

    public ProgressToast(Component title, float progress, ResourceLocation resourceLocation)
    {
        this.title = title;
        this.progress = progress;
        this.iconResourceLocation = resourceLocation;
    }

    @Override
    public Visibility getWantedVisibility() {
        if (progress >= 1.0F)
        {
            return Visibility.HIDE;
        }
        return Visibility.SHOW;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l)
    {
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, 0, 0, 0, 0, this.width(), this.height(), 256, 256);
        if (iconResourceLocation != null)
        {
            renderImage(guiGraphics, iconResourceLocation);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, this.title, 30, 12, -1);


        guiGraphics.fill(3, 28, 157, 29, -1);
        float f = Mth.clampedLerp(this.lastProgress, this.progress, (float) (l - this.lastProgressTime) / 100.0f);
        int i = this.progress >= this.lastProgress ? -16755456 : -11206656;
        guiGraphics.fill(3, 28, (int) (3.0f + 154.0f * f), 29, i);
        this.lastProgress = f;
        this.lastProgressTime = l;
    }

    public void updateProgress(float progress)
    {
        this.progress = progress;
    }
}
