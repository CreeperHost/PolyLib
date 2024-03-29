package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DropdownButton<E extends DropdownButton.IDropdownOption> extends PolyButton
{
    public boolean dropdownOpen;
    private E selected;
    private List<E> possibleVals;
    private Component baseButtonText;
    private final boolean dynamic;
    private final boolean drawHeader;
    public boolean wasJustClosed = false;
    Minecraft minecraft = Minecraft.getInstance();

    public DropdownButton(int x, int y, int widthIn, int heightIn, Component buttonText, E def, boolean dynamic, boolean drawHeader, OnPress onPress)
    {
        super(x, y, widthIn, heightIn, buttonText, onPress, DEFAULT_NARRATION);
        this.selected = def;
        possibleVals = (List<E>) def.getPossibleVals();
        baseButtonText = buttonText;
        this.dynamic = dynamic;
        this.drawHeader = drawHeader;
    }

    public DropdownButton(int x, int y, int widthIn, int heightIn, Component buttonText, E def, boolean dynamic, OnPress onPress)
    {
        this(x, y, widthIn, heightIn, buttonText, def, dynamic, true, onPress);
    }

    public DropdownButton(int x, int y, Component buttonText, E def, boolean dynamic, OnPress onPress)
    {
        this(x, y, 200, 20, buttonText, def, dynamic, onPress);
    }

    public boolean flipped = false;

    @SuppressWarnings("Duplicates")
    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            int drawY = getY();
            Font fontrenderer = minecraft.font;
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = mouseX >= this.getX() && mouseY >= drawY && mouseX < this.getX() + this.width && mouseY < drawY + this.height;
            int i = this.getHoverState(this.isHovered);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (drawHeader)
            {
                guiGraphics.blit(WIDGETS_LOCATION, this.getX(), drawY, 0, 46 + i * 20, this.width / 2, this.height);
                guiGraphics.blit(WIDGETS_LOCATION, this.getX() + this.width / 2, drawY, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
                int j = 14737632;

                if (!this.active)
                {
                    j = 10526880;
                } else if (this.isHovered)
                {
                    j = 16777120;
                }

                guiGraphics.drawCenteredString(fontrenderer, this.baseButtonText, this.getX() + this.width / 2, this.getX() + (this.height - 8) / 2, j);
            }

            if (dropdownOpen)
            {
                drawY += 1;
                int yOffset = height - 2;
                if (flipped)
                {
                    yOffset = -yOffset;
                    drawY -= 1;
                }
                for (int j = 0, possibleValsSize = possibleVals.size(); j < possibleValsSize; j++)
                {
                    E e = possibleVals.get(j);
                    drawY += yOffset;
                    boolean ourHovered = mouseX >= this.getX() && mouseY >= drawY && mouseX < this.getX() + this.width && mouseY < drawY + this.height - 2;

                    int subHovered = ourHovered ? 2 : 0;

                    RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    // TODO: Fix rendering being dodgy, but it is "good enough" to avoid spending too much time on right now
                    guiGraphics.blit(WIDGETS_LOCATION, this.getX(), drawY, 0, 46 + subHovered * 20 + 1, this.width / 2, this.height - 1);
                    guiGraphics.blit(WIDGETS_LOCATION, this.getX() + this.width / 2, drawY, 200 - this.width / 2, 46 + subHovered * 20 + 1, this.width / 2, this.height - 1);

                    String name = I18n.get(e.getTranslate(selected, true));
                    int textColour = 14737632;

                    guiGraphics.drawCenteredString(fontrenderer, name, this.getX() + this.width / 2, drawY + (this.height - 10) / 2, textColour);
                }
            }
        }
    }

    protected int getHoverState(boolean mouseOver)
    {
        return mouseOver ? 2 : active ? dropdownOpen ? 2 : 1 : 0;
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        boolean pressed = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if (dropdownOpen)
        {
            if (drawHeader)
            {
                if (pressed)
                {
                    close();
                    return false; // selection not changed, so no need to return true which will trigger actionPerformed.
                }
            }
            E clickedElement = getClickedElement(p_mouseClicked_1_, p_mouseClicked_3_);
            if (clickedElement != null)
            {
                setSelected(clickedElement);
                try
                {
                    ourOnPress();
                }
                catch (Exception ignored)
                {
                }
                close();
                return true;
            }
            close();
            return false;
        } else if (pressed)
        {
            if (drawHeader)
            {
                dropdownOpen = true;
                if (dynamic)
                {
                    selected.updateDynamic();
                    possibleVals = (List<E>) selected.getPossibleVals();
                }
            }
        }
        return false; // at this stage we've handled all the "true" options, so it ain't been pressed
    }

    public void ourOnPress()
    {
//        this
        //        .ourOnPress.onPress(this);
    }

    @Override
    public void onPress()
    {
        //        this.ourOnPress.onPress(this);
    }

    public void close()
    {
        dropdownOpen = false;
        wasJustClosed = true;
    }

    public E getSelected()
    {
        return selected;
    }

    public void setSelected(E selected)
    {
        try
        {
            this.selected = selected;
            updateDisplayString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updateDisplayString()
    {
        baseButtonText = Component.translatable(selected.getTranslate(selected, false), baseButtonText);
    }

    private E getClickedElement(double mouseX, double mouseY)
    {
        E clickedElement = null;
        int y = this.getY() + 1;

        int yOffset = height - 2;
        if (flipped)
        {
            yOffset = -yOffset;
            y -= 1;
        }
        for (IDropdownOption e : possibleVals)
        {
            y += yOffset;
            if (mouseX >= this.getX() && mouseY >= y && mouseX < this.getX() + this.width && mouseY < y + this.height - 2)
            {
                clickedElement = (E) e;
                break;
            }
        }
        return clickedElement;
    }

    public List<E> getPossibleVals()
    {
        return possibleVals;
    }

    public interface IDropdownOption
    {
        List<IDropdownOption> getPossibleVals();

        String getTranslate(IDropdownOption currentDO, boolean dropdownOpen);

        default void updateDynamic()
        {
        }
    }
}
