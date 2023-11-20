package net.creeperhost.polylib.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.List;

/**
 * Created by Aaron on 28/04/2017.
 */
public class ScreenWell
{
    private final Minecraft mc;
    public final int top;
    public final int bottom;
    public final int right;
    public final int left;
    public List<String> lines;
    private boolean centeredF;
    private String title;

    public ScreenWell(Minecraft mcIn, int width, int topIn, int bottomIn, String title, List<String> linesToDraw, boolean centred, int left)
    {
        this.title = title;
        this.lines = linesToDraw;
        this.centeredF = centred;
        this.mc = mcIn;
        this.top = topIn;
        this.bottom = bottomIn;
        this.left = left;
        this.right = width;
    }

    @SuppressWarnings("Duplicates")
    public void render(GuiGraphics guiGraphics)
    {
        Font fontRenderer = Minecraft.getInstance().font;

        int titleWidth = fontRenderer.width(title);
        guiGraphics.fill(left, top, right, bottom, 0x66000000);

        guiGraphics.drawString(fontRenderer, title, this.left + ((this.right - this.left) / 2) - (titleWidth / 2), this.top + 2, 0xFFFFFF, true);

        int topStart = this.top + 15;

        for (String line : lines)
        {
            if (centeredF)
            {
                int stringWidth = fontRenderer.width(line);
                guiGraphics.drawString(fontRenderer, I18n.get(line), this.left + ((this.right - this.left) / 2) - (stringWidth / 2), topStart, 0xFFFFFF, true);
            } else
            {
                guiGraphics.drawString(fontRenderer, I18n.get(line), this.left, topStart, 0xFFFFFF, true);
            }
            topStart += 10;
        }
    }
}
