package net.creeperhost.testmod.client.gui;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.dynamic;

/**
 * Created by brandon3055 on 30/03/2024
 */
public class MainMenuGuiInjection implements GuiProvider {

    @Override
    public void buildGui(ModularGui gui) {
        TitleScreen screen = (TitleScreen) gui.getScreen();
        gui.initFullscreenGui();
        GuiElement<?> root = gui.getRoot();

        GuiButton testButton = GuiButton.vanilla(root, Component.literal("Test Button"))
                .onClick(() -> {});
        Constraints.size(testButton, 100, 20);
        Constraints.placeInside(testButton, root, Constraints.LayoutPos.TOP_LEFT, 10, 10);

        GuiManipulable draggable = new GuiManipulable(root);
        draggable.addMoveHandle(20);
        draggable.enableCursors(true);
        Constraints.size(draggable, 100, 20);
        Constraints.placeInside(draggable, root, Constraints.LayoutPos.TOP_RIGHT, -10, 10);

        Constraints.bind(GuiRectangle.toolTipBackground(draggable.getContentElement()), draggable.getContentElement());
        Constraints.bind(new GuiText(draggable.getContentElement(), Component.literal("Drag Me!")), draggable.getContentElement());

        //Draw some rainbow rectangles around the buttons because why not!
        for (Widget renderable : screen.renderables) {
            if (!(renderable instanceof Button button) || renderable instanceof PlainTextButton) continue;

            new GuiRectangle(root)
                    .border(() -> 0xFF000000 | Color.getHSBColor((System.currentTimeMillis() % 5000) / 5000F, 1F, 1F).getRGB())
                    .constrain(GeoParam.TOP, dynamic(() -> button.y - 1D))
                    .constrain(GeoParam.LEFT, dynamic(() -> button.x - 1D))
                    .constrain(GeoParam.WIDTH, dynamic(() -> button.getWidth() + 2D))
                    .constrain(GeoParam.HEIGHT, dynamic(() -> button.getHeight() + 2D));
        }

        //Gotta have the DVD!
        GuiDVD dvd = new GuiDVD(root);
        Constraints.size(dvd, 40, 40);
        Constraints.placeInside(dvd, root, Constraints.LayoutPos.BOTTOM_LEFT, 20, -20);
        GuiTexture disc = new GuiTexture(dvd.getContentElement(), Material.fromRawTexture(new ResourceLocation("textures/item/music_disc_13.png")));
        Constraints.bind(disc, dvd.getContentElement());
        dvd.start();
        dvd.onBounce(bounce -> disc.setColour(0xFF000000 | ChatFormatting.getById(1 + (bounce % 15)).getColor()));
        GuiText text = new GuiText(dvd.getContentElement(), Component.literal("DVD"));
        Constraints.size(text, 100, 8);
        Constraints.placeInside(text, dvd.getContentElement(), Constraints.LayoutPos.BOTTOM_CENTER, 0, -8);
    }
}
