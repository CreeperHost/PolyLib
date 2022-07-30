package net.creeperhost.polylib.client.screen.screencreator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screen.screencreator.widgets.DraggaleWidget;
import net.creeperhost.polylib.client.screen.screencreator.widgets.ProgressWidget;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScreenCreator extends Screen
{
    int imageWidth;
    int imageHeight;
    int leftPos;
    int topPos;

    ScreenBuilder screenBuilder = new ScreenBuilder();
    List<DraggaleWidget> draggaleWidgetList = new ArrayList<>();

    public ScreenCreator(Component component, int imageWidth, int imageHeight)
    {
        super(component);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    protected void init()
    {
        super.init();
        leftPos = (this.width - imageWidth) / 2;
        topPos = (this.height - imageHeight) / 2;

        addRenderableWidget(new Button(width - 50, 0, 20, 20, Component.literal("^"), button ->
        {

        }));

        addRenderableWidget(new Button(width - 50, height - 20, 20, 20, Component.literal("v"), button ->
        {

        }));

        addDragWidget(width - 50, 40, 20, 20, new Button(width - 50, 40, 20, 20, Component.empty(), button -> {}));
        addDragWidget(width - 50, 80, 20, 20, new ProgressWidget(width - 50, 80, 20, 20, Component.empty(), button -> {}));

    }

    public void addDragWidget(int x, int y, int width, int height, Button widget)
    {
        DraggaleWidget draggaleWidget = new DraggaleWidget(x, y, width, height, widget);
        draggaleWidgetList.add(draggaleWidget);
        addRenderableWidget(draggaleWidget);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        screenBuilder.drawDefaultBackground(this, poseStack, leftPos, topPos, imageWidth, imageHeight, 256, 256);
        renderList(poseStack, i, j, f);

        super.render(poseStack, i, j, f);
    }

    public void renderList(PoseStack poseStack, int mouseX, int mouseY, float partial)
    {
        int listTop = 0;
        int listLeft = width - 80;

        screenBuilder.drawDefaultBackground(this, poseStack, listLeft, listTop, 80, height, 256, 256);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g)
    {
        for (DraggaleWidget draggaleWidget : draggaleWidgetList)
        {
            if(draggaleWidget.isMouseOver(d, e))
            {
                if (draggaleWidget.mouseDragged(d, e, i, f, g))
                {
                    draggaleWidget.setX((int) d);
                    draggaleWidget.setY((int) e);
                    return true;
                }
            }
        }
        return super.mouseDragged(d, e, i, f, g);
    }
}
