package net.creeperhost.testmod.fabric;

import net.creeperhost.testmod.TestMod;
import net.fabricmc.api.ModInitializer;

public class TestModFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        TestMod.init();
    }
}
