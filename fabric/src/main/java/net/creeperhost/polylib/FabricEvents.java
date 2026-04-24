package net.creeperhost.polylib;

import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricEvents {
    public static void init(){
        ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) -> {
            PolyLevelEvents.LEVEL_SAVE.invoker().onSave(server.overworld());
        });
    }
}
