package net.creeperhost.polylib.client.screen.widget.buttons;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PolyButton extends Button
{
    public static final WidgetSprites SPRITES = new WidgetSprites(new ResourceLocation("widget/button"), new ResourceLocation("widget/button_disabled"), new ResourceLocation("widget/button_highlighted"));

    public PolyButton(int x, int y, int xSize, int ySize, Component component, OnPress onPress, CreateNarration createNarration)
    {
        super(x, y, xSize, ySize, component, onPress, createNarration);
    }
}
