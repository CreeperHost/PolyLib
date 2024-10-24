package net.creeperhost.polylib.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class PolyToast implements Toast
{
    public static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("toast/system");

    public void renderImage(GuiGraphics guiGraphics, ResourceLocation resourceLocation) {
        RenderSystem.setShaderTexture(0, resourceLocation);
        RenderSystem.enableBlend();
        guiGraphics.blit(RenderType::guiTextured, resourceLocation, 8, 8, 0, 0, 16, 16, 16, 16, 256, 256);
        RenderSystem.enableBlend();
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
