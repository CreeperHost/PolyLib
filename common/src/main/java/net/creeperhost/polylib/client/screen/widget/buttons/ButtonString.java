package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ButtonString extends PolyButton
{
    private final Supplier<Component> displayGetter;
    private final RenderPlace renderPlace;

    public enum RenderPlace
    {
        EXACT, CENTRED
    }

    public ButtonString(int xPos, int yPos, int width, int height, Supplier<Component> displayGetter, RenderPlace renderPlace, OnPress onPress)
    {
        super(xPos, yPos, width, height, displayGetter.get(), onPress, DEFAULT_NARRATION);
        this.displayGetter = displayGetter;
        this.renderPlace = renderPlace;
    }

    public ButtonString(int xPos, int yPos, int width, int height, Component displayString, OnPress onPress)
    {
        this(xPos, yPos, width, height, () -> displayString, RenderPlace.CENTRED, onPress);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i)
    {
        if (hasText())
        {
            return super.mouseClicked(d, e, i);
        }
        return false;
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partial)
    {
        this.visible = hasText();
        if (this.visible)
        {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            Component buttonText = displayGetter.get();

            if (renderPlace == RenderPlace.CENTRED)
            {
                GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, buttonText, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFF);
            }
            else
            {
                GuiComponent.drawString(poseStack, Minecraft.getInstance().font, buttonText, getX(), getY(), 0xFFFFFF);
            }
        }
    }

    public boolean hasText()
    {
        return !displayGetter.get().toString().isEmpty();
    }
}
