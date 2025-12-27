package net.creeperhost.polylib.client.toast;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SimpleToast implements Toast {
    private static final Identifier BG_TEXTURE = Identifier.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/toast.png");
    private final Component title;
    private final Component description;
    private ItemStack displayIconStack = ItemStack.EMPTY;
    private Identifier iconResourceLocation;
    private Toast.Visibility visibility = Toast.Visibility.SHOW;

    public SimpleToast(Component title, Component description) {
        this.title = title;
        this.description = description;
    }

    public SimpleToast(Component title, Component description, ItemStack itemStack) {
        this.title = title;
        this.description = description;
        this.displayIconStack = itemStack;
    }

    public SimpleToast(Component title, Component description, Identifier resourceLocation) {
        this.title = title;
        this.description = description;
        this.iconResourceLocation = resourceLocation;
    }

    @Override
    public Visibility getWantedVisibility() {
        if (title != null) {
            return visibility;
        }
        return Visibility.HIDE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l) {
        //x, y, u, v, width, height, texWidth, texHeight
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, 0, 0, 0, 0, width(), height(), width(), height());
        int wrapWidth = 125;
        int xOffset = 30;
        if (iconResourceLocation != null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, iconResourceLocation, 5, (height() - 22) / 2, 0, 0, 22, 22, 22, 22);
        } else if (!displayIconStack.isEmpty()) {
            new GuiRender(guiGraphics).renderItem(displayIconStack, 5, (height() - 22) / 2D, 22);
        } else {
            wrapWidth += 23;
            xOffset -= 23;
        }

        if (title != null) {
            List<FormattedCharSequence> list = font.split(description, wrapWidth);
            int n = 0xFF88FF;
            if (list.isEmpty()) {
                guiGraphics.drawWordWrap(Minecraft.getInstance().font, title, xOffset, 7, wrapWidth, n | 0xFF000000);
            } else if (list.size() == 1) {
                guiGraphics.drawString(Minecraft.getInstance().font, title, xOffset, 7, n | 0xFF000000);
                guiGraphics.drawString(Minecraft.getInstance().font, list.getFirst(), xOffset, 18, -1);
            } else {
                if (l < 1500L) {
                    int k = Mth.floor(Mth.clamp((float) (1500L - l) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    guiGraphics.drawString(Minecraft.getInstance().font, title, xOffset, 11, n | k);
                } else {
                    int k = Mth.floor(Mth.clamp((float) (l - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    int m = this.height() / 2 - list.size() * font.lineHeight / 2;
                    for (FormattedCharSequence formattedCharSequence : list) {
                        guiGraphics.drawString(Minecraft.getInstance().font, formattedCharSequence, xOffset, m, 0xFFFFFF | k);
                        m += font.lineHeight;
                    }
                }
            }
            visibility = l >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        } else {
            visibility = Visibility.HIDE;
        }
    }

    @Override
    public void update(ToastManager toastManager, long l) {}
}