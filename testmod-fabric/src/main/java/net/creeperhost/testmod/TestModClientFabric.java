package net.creeperhost.testmod;

import net.creeperhost.polylib.register.PolyScreens;
import net.fabricmc.api.ClientModInitializer;

public class TestModClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PolyScreens.flush();
    }
}
