package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.data.EnumArrowButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ButtonArrow extends Button
{
    public ButtonArrow(int i, int j, int k, int l, EnumArrowButton arrowButton, OnPress onPress)
    {
        super(i, j, k, l, Component.empty(), onPress);
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        super.renderButton(poseStack, i, j, f);
    }
}
