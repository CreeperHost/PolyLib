package net.creeperhost.testmod;

import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.minecraft.client.gui.screens.TitleScreen;

public class TestModClientCommon
{
    public static void init()
    {
        ModularGuiInjector.registerInjection(e -> e instanceof TitleScreen, e -> new MainMenuGuiInjection());
    }
}
