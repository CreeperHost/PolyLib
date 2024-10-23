package net.creeperhost.polylib.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.resources.ResourceLocation;

public class PolyToast implements Toast
{
    public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("toast/system");

//    @Override
//    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long l)
//    {
//        return null;
//    }
//
    //TODO
    public void renderImage(GuiGraphics guiGraphics, Font font, ResourceLocation resourceLocation)
    {
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.enableBlend();
        //TODO
//        guiGraphics.blit(resourceLocation, 8, 8, 0, 0, 16, 16, 16, 16);
        RenderSystem.enableBlend();
    }

    @Override
    public Visibility getWantedVisibility()
    {
        return null;
    }

    @Override
    public void update(ToastManager toastManager, long l)
    {

    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l)
    {

    }
}
