package net.creeperhost.polylib.client.toast;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.function.Supplier;

public class ProgressToast implements Toast
{
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/toast.png");
    private final Component title;
    private Supplier<Double> progress;
    private double lastProgress;
    private long lastProgressTime;
    private final ResourceLocation iconResourceLocation;

    public ProgressToast(Component title, Supplier<Double> progress)
    {
        this.title = title;
        this.progress = progress;
        this.iconResourceLocation = null;
    }

    public ProgressToast(Component title, Supplier<Double> progress, ResourceLocation resourceLocation)
    {
        this.title = title;
        this.progress = progress;
        this.iconResourceLocation = resourceLocation;
    }

    public ProgressToast(Component title, double progress)
    {
        this.title = title;
        this.progress = () -> progress;
        this.iconResourceLocation = null;
    }

    public ProgressToast(Component title, double progress, ResourceLocation resourceLocation)
    {
        this.title = title;
        this.progress = () -> progress;
        this.iconResourceLocation = resourceLocation;
    }

    @Override
    public Visibility getWantedVisibility() {
        if (progress.get() >= 1.0F)
        {
            return Visibility.HIDE;
        }
        return Visibility.SHOW;
    }

    @Override
    public void update(ToastManager toastManager, long l) {}

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l)
    {
        //x, y, u, v, width, height, texWidth, texHeight
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, 0, 0, 0, 0, width(), height(), width(), height());
        int wrapWidth = 125;
        int xOffset = 30;
        if (iconResourceLocation != null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, iconResourceLocation, 5, (height() - 22) / 2, 0, 0, 22, 22, 22, 22);
        } else {
            wrapWidth += 23;
            xOffset -= 23;
        }

        if (title != null) {
            guiGraphics.drawWordWrap(Minecraft.getInstance().font, title, xOffset, 7, wrapWidth, 0xFFFF88FF);
        }

        guiGraphics.fill(3, 28, 157, 29, -1);
        double f = Mth.clampedLerp(this.lastProgress, this.progress.get(), (float) (l - this.lastProgressTime) / 100.0f);
        int i = this.progress.get() >= this.lastProgress ? -16755456 : -11206656;
        guiGraphics.fill(3, 28, (int) (3.0f + 154.0f * f), 29, i);
        this.lastProgress = f;
        this.lastProgressTime = l;
    }

    public void updateProgress(double progress)
    {
        this.progress = () -> progress;
    }

}