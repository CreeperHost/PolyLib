package net.creeperhost.polylib.client.screen.widget.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ButtonNoBlend extends PolyButton
{
    public ButtonNoBlend(int x, int y, int width, int height, Component component, OnPress onPress)
    {
        super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int j, float f)
    {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int k = this.active ? 16777215 : 10526880;
        this.renderString(guiGraphics, minecraft.font, k | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
