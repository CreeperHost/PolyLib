package net.creeperhost.testmod;

import net.fabricmc.api.ModInitializer;

public class TestModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TestModCommon.init();
    }
}
