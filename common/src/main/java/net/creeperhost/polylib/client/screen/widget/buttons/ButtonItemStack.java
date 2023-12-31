package net.creeperhost.polylib.client.screen.widget.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ButtonItemStack extends Button
{
    private final ItemStack itemStack;

    public ButtonItemStack(int i, int j, int k, int l, Component component, ItemStack itemStack, OnPress onPress)
    {
        super(i, j, k, l, component, onPress, DEFAULT_NARRATION);
        this.itemStack = itemStack;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int i, int j, float f)
    {
        super.renderWidget(guiGraphics, i, j, f);
        guiGraphics.renderItem(itemStack, getX() + 2, getY() + 1);
    }
}
