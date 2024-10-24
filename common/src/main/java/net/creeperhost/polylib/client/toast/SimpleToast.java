package net.creeperhost.polylib.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SimpleToast extends PolyToast {
    private final Component title;
    private final Component description;
    private ItemStack displayIconStack = ItemStack.EMPTY;
    private ResourceLocation iconResourceLocation;
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

    public SimpleToast(Component title, Component description, ResourceLocation resourceLocation) {
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
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, 0, 0, 0, 0, this.width(), this.height(), 256, 256);
        if (iconResourceLocation != null) {
            renderImage(guiGraphics, iconResourceLocation);
        }

        if (title != null) {
            List<FormattedCharSequence> list = font.split(description, 125);
            int n = 0xFF88FF;
            if (list.size() == 1) {
                guiGraphics.drawString(Minecraft.getInstance().font, title, 30, 7, n | 0xFF000000);
                guiGraphics.drawString(Minecraft.getInstance().font, list.get(0), 30, 18, -1);
            } else {
                if (l < 1500L) {
                    int k = Mth.floor(Mth.clamp((float) (1500L - l) / 300.0f, 0.0f, 1.0f) * 255.0f) << 24 | 0x4000000;
                    guiGraphics.drawString(Minecraft.getInstance().font, title, 30, 11, n | k);
                } else {
                    int k = Mth.floor(Mth.clamp((float) (l - 1500L) / 300.0f, 0.0f, 1.0f) * 252.0f) << 24 | 0x4000000;
                    int m = this.height() / 2 - list.size() * font.lineHeight / 2;
                    for (FormattedCharSequence formattedCharSequence : list) {
                        guiGraphics.drawString(Minecraft.getInstance().font, formattedCharSequence, 30, m, 0xFFFFFF | k);
                        m += font.lineHeight;
                    }
                }
            }
            if (!displayIconStack.isEmpty()) {
                guiGraphics.renderItem(displayIconStack, 8, 8);
            }
            visibility = l >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        } else {
            visibility = Visibility.HIDE;
        }
    }
}
