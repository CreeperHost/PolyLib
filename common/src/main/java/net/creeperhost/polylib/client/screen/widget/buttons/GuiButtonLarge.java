package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screen.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiButtonLarge extends PolyButton
{
    private final String description;
    private final ItemStack stack;

    public GuiButtonLarge(int x, int y, int widthIn, int heightIn, String buttonText, String description, ItemStack stack)
    {
        super(x, y, widthIn, heightIn, Component.translatable(buttonText));
        this.width = 200;
        this.height = 20;
        this.visible = true;
        this.active = true;
        this.width = widthIn;
        this.height = heightIn;
        this.setMessage(Component.translatable(buttonText));
        this.description = description;
        this.stack = stack;
    }

    @Override
    public void renderButton(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            int k = this.getYImage(this.isHovered);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            ScreenHelper.drawContinuousTexturedBox(matrixStack, this.getX(), this.getY(), 0, 46 + k * 20, this.width, this.height,
                    200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(matrixStack, mc, mouseX, mouseY);
            int color = 14737632;

            List<FormattedCharSequence> newstring = ComponentRenderUtils.wrapComponents(
                    Component.translatable(description), width - 12, mc.font);
            int start = getY() + 40;

            for (FormattedCharSequence s : newstring)
            {
                int left = ((this.getX() + 4));
                mc.font.drawShadow(matrixStack, s, left, start += 10, -1);
            }

            Component buttonText = this.getMessage();

            drawCenteredString(matrixStack, mc.font, buttonText, this.getX() + this.width / 2, this.getY() + 10, color);
            ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
            matrixStack.pushPose();
            renderItem.renderGuiItem(stack, (this.getX()) + (width / 2) - 8, (this.getY()) + 24);
            matrixStack.popPose();
        }
    }
}
