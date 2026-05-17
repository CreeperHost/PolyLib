package net.creeperhost.testmod;

import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.testmod.init.TestClientEvents;
import net.creeperhost.testmod.init.TestDebugEntries;
import net.creeperhost.testmod.init.TestScreens;
import net.minecraft.client.gui.screens.TitleScreen;

public class TestModClientCommon
{
    public static void init()
    {
        TestClientEvents.init();
        TestScreens.init();
        TestDebugEntries.init();
        ModularGuiInjector.registerInjection(e -> e instanceof TitleScreen, e -> new MainMenuGuiInjection());
    }
}
