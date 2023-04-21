package net.creeperhost.polylib.client.screen.widget.buttons;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class PolyButton extends Button
{
    public PolyButton(int x, int y, int xSize, int ySize, Component component, OnPress onPress, CreateNarration createNarration)
    {
        super(x, y, xSize, ySize, component, onPress, createNarration);
    }
}
