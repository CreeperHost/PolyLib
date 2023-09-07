package net.creeperhost.polylib.client.screen.widget.buttons;

import net.creeperhost.polylib.data.EnumArrowButton;
import net.minecraft.network.chat.Component;

@Deprecated(since = "1.20.1")
public class ButtonArrow extends PolyButton
{
    public ButtonArrow(int i, int j, int k, int l, EnumArrowButton arrowButton, OnPress onPress)
    {
        super(i, j, k, l, Component.empty(), onPress, DEFAULT_NARRATION);
    }
}
