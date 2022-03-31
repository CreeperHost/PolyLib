package net.creeperhost.polylib.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
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
    public Visibility render(PoseStack poseStack, ToastComponent toastComponent, long l)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        toastComponent.blit(poseStack, 0, 0, 0, 0, this.width(), this.height());
        if(iconResourceLocation != null)
        {
            renderImage(poseStack, toastComponent, iconResourceLocation);
        }
        toastComponent.getMinecraft().font.draw(poseStack, this.title, 30.0f, 12.0f, -1);
        //Test code
//        if(progress < 1.0F) progress += 0.0001F;

        GuiComponent.fill(poseStack, 3, 28, 157, 29, -1);
        float f = Mth.clampedLerp(this.lastProgress, this.progress, (float)(l - this.lastProgressTime) / 100.0f);
        int i = this.progress >= this.lastProgress ? -16755456 : -11206656;
        GuiComponent.fill(poseStack, 3, 28, (int)(3.0f + 154.0f * f), 29, i);
        this.lastProgress = f;
        this.lastProgressTime = l;
        if(progress >= 1.0F)
        {
            return Visibility.HIDE;
        }
        return Visibility.SHOW;
    }

    public void updateProgress(float progress)
    {
        this.progress = progress;
    }
}
