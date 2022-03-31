package net.creeperhost.polylib.client.screen;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

import javax.annotation.Nullable;

public class ButtonHelper
{
    @Nullable
    public static Button findButton(String buttonString, Screen screen)
    {
        if(screen.children() != null && !screen.children().isEmpty())
        {
            for (GuiEventListener listener : screen.children())
            {
                if(!(listener instanceof Button button)) continue;

                if (button.getMessage().getString().equalsIgnoreCase(I18n.get(buttonString)))
                {
                    return button;
                }
            }
        }
        return null;
    }

    @Nullable
    public static AbstractWidget removeButton(String buttonString, Screen screen)
    {
        AbstractWidget widget = findButton(buttonString, screen);
        if(widget != null)
        {
            //We can't "remove" the button, so we just disable and hide it
            widget.visible = false;
            widget.active = false;
            return widget;
        }
        return null;
    }
}
