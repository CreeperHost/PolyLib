package net.creeperhost.polylib.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.input.MouseButtonEvent;

public class ScreenListEntry extends AbstractSelectionList.Entry
{
    protected final Minecraft mc;
    protected final ScreenList list;

    public ScreenListEntry(ScreenList<?> list)
    {
        this.list = list;
        this.mc = Minecraft.getInstance();
    }

    //Do nothing, We don't want the default render
    @Override
    public void renderContent(GuiGraphics guiGraphics, int i, int j, boolean bl, float f) {
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        list.setSelected(this);
        return false;
    }
}
