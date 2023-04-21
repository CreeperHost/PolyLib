package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.data.EnumRedstoneState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ButtonRedstoneControl extends PolyButton
{
    private EnumRedstoneState state;
    private final Screen screen;

    public ButtonRedstoneControl(Screen screen, int i, int j, int k, int l, EnumRedstoneState state, OnPress onPress)
    {
        super(i, j, k, l, Component.empty(), onPress, DEFAULT_NARRATION);
        this.screen = screen;
        this.state = state;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        super.render(poseStack, i, j, f);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderGuiItem(state.getRenderStack(), getX() + 2, getY() + 1);
        if (isHovered) screen.renderTooltip(poseStack, Component.literal(state.getName()), i, j);
    }

    @Override
    public void onPress()
    {
        super.onPress();
        switch (state)
        {
            case IGNORED -> state = EnumRedstoneState.REQUIRED;
            case REQUIRED -> state = EnumRedstoneState.INVERTED;
            case INVERTED -> state = EnumRedstoneState.IGNORED;
        }
    }

    public EnumRedstoneState getState()
    {
        return state;
    }

    public void setState(EnumRedstoneState state)
    {
        this.state = state;
    }
}
