package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiButtonLarge extends PolyButton
{
    private final Component description;
    private final ItemStack stack;

    public GuiButtonLarge(int x, int y, int width, int height, String buttonText, Component description, ItemStack stack, OnPress onPress)
    {
        super(x, y, width, height, Component.translatable(buttonText), onPress, DEFAULT_NARRATION);
        this.visible = true;
        this.active = true;
        this.setMessage(Component.translatable(buttonText));
        this.description = description;
        this.stack = stack;
    }

    public GuiButtonLarge(int x, int y, int width, int height, String buttonText, String description, ItemStack stack, OnPress onPress)
    {
        this(x, y, width, height, buttonText, Component.literal(description), stack, onPress);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        Minecraft mc = Minecraft.getInstance();
        int k = getYImage(isHovered);
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        ScreenHelper.drawContinuousTexturedBox(graphics.pose(), getX(), getY(), 0, 46 + k * 20, width, height, 200, 20, 2, 3, 2, 2, 0);
        int color = 14737632;

        List<FormattedCharSequence> newstring = ComponentRenderUtils.wrapComponents(description, width - 12, mc.font);
        int start = getY() + 40;

        for (FormattedCharSequence s : newstring) {
            int left = ((getX() + 4));
            graphics.drawString(mc.font, s, left, start += 10, -1, true);
        }

        graphics.drawCenteredString(mc.font, getMessage(), getX() + width / 2, getY() + 10, color);
        graphics.renderFakeItem(stack, (getX()) + (width / 2) - 8, (getY()) + 24);
    }

    protected int getYImage(boolean bl) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (bl) {
            i = 2;
        }
        return i;
    }
}
