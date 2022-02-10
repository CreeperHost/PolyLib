package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.PolyLib;
import net.fabricmc.api.ModInitializer;

public class PolyLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PolyLib.init();
    }
}
