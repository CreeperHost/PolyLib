package net.creeperhost.polylib.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;

public class PolyToast implements Toast
{
    public static final ResourceLocation TEXTURE = new ResourceLocation("toast/system");

    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long l)
    {
        return null;
    }

    //TODO scale the image
    public void renderImage(GuiGraphics guiGraphics, ToastComponent toastComponent, ResourceLocation resourceLocation)
    {
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.enableBlend();
        guiGraphics.blit(resourceLocation, 8, 8, 0, 0, 16, 16, 16, 16);
        RenderSystem.enableBlend();
    }
}
