package net.creeperhost.polylib.client.screen.widget.buttons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ButtonItemStack extends AbstractWidget
{
    private final ItemStack itemStack;

    public ButtonItemStack(int i, int j, int k, int l, Component component, ItemStack itemStack)
    {
        super(i, j, k, l, component);
        this.itemStack = itemStack;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        super.render(poseStack, i, j, f);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        itemRenderer.renderGuiItem(itemStack, getX() + 2, getY() + 1);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }
}
