package net.creeperhost.testmod;

import net.creeperhost.polylib.registry.PolyScreens;
import net.fabricmc.api.ClientModInitializer;

public class TestModClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PolyScreens.flush();
    }
}
