package net.creeperhost.polylib.client.screen.screencreator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.screenbuilder.ScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ScreenCreationSetup extends Screen
{
    boolean renderNewScreen = false;
    ScreenBuilder screenBuilder = new ScreenBuilder();
    EditBox imageWidthBox;
    EditBox imageHeightBox;

    int imageWidth;
    int imageHeight;
    int leftPos;
    int topPos;

    public ScreenCreationSetup()
    {
        super(Component.literal("Screen Builder Screen"));
    }

    @Override
    protected void init()
    {
        super.init();
        imageWidth = 190;
        imageHeight = 220;
        leftPos = (this.width - imageWidth) / 2;
        topPos = (this.height - imageHeight) / 2;

        addRenderableWidget(imageWidthBox = new EditBox(font, leftPos + 60, topPos + 10, 80, 16, Component.literal("imageWidthBox")));
        addRenderableWidget(imageHeightBox = new EditBox(font, leftPos + 60, topPos + 30, 80, 16, Component.literal("imageHeightBox")));
        imageWidthBox.setValue(String.valueOf(imageWidth));
        imageHeightBox.setValue(String.valueOf(imageHeight));

        addRenderableWidget(new Button(leftPos + 60, topPos + imageWidth - 10, 60, 20, Component.literal("NEW"), button ->
        {
            Minecraft.getInstance().setScreen(new ScreenCreator(Component.literal("TEST"), Integer.parseInt(imageWidthBox.getValue()), Integer.parseInt(imageHeightBox.getValue())));
            renderNewScreen = true;
        }));

        addRenderableWidget(new Button(leftPos + 60, topPos + imageWidth - 30, 60, 20, Component.literal("LOAD"), button ->
        {

        }));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f)
    {
        screenBuilder.drawDefaultBackground(this, poseStack, leftPos, topPos, imageWidth, imageHeight, 256, 256);
        font.draw(poseStack, "Width:", leftPos + 20, topPos + 12, 0);
        font.draw(poseStack, "Height:", leftPos + 20, topPos + 32, 0);

        super.render(poseStack, i, j, f);

    }

    @Override
    public void renderBackground(@NotNull PoseStack poseStack)
    {
        super.renderBackground(poseStack);
    }
}
