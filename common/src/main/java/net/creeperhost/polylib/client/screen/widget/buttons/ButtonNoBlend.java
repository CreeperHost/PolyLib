package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ButtonNoBlend extends PolyButton
{
    public ButtonNoBlend(int x, int y, int width, int height, Component component)
    {
        super(x, y, width, height, component);
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int k = this.getYImage(this.isHovered);
        this.blit(poseStack, this.getX(), this.getY(), 0, 46 + k * 20, this.width / 2, this.height);
        this.blit(poseStack, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + k * 20, this.width / 2,
                this.height);
        this.renderBg(poseStack, minecraft, i, j);
        int l = this.active ? 16777215 : 10526880;
        drawCenteredString(poseStack, font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2,
                l | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
