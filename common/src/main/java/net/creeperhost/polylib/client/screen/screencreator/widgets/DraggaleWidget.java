package net.creeperhost.polylib.client.screen.screencreator.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class DraggaleWidget extends Button
{
    private final Button widget;

    public DraggaleWidget(int x, int y, int width, int height, Button widget)
    {
        super(x, y, width, height, Component.empty(), button ->
        {
        });
        this.widget = widget;
    }

    public void setX(int x)
    {
        this.x = x;
        this.widget.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
        this.widget.y = y;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        widget.render(poseStack, i, j, f);
    }

    public Widget getWidget()
    {
        return widget;
    }
}
