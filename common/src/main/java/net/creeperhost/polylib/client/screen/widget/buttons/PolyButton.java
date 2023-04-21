package net.creeperhost.polylib.client.screen.widget.buttons;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PolyButton extends AbstractButton
{
    public PolyButton(int i, int j, int k, int l, Component component)
    {
        super(i, j, k, l, component);
    }

    @Override
    public void onPress()
    {

    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
