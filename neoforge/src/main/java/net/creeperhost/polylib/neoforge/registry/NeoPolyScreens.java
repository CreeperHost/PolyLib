package net.creeperhost.polylib.neoforge.registry;

import net.creeperhost.polylib.register.PolyScreens;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Hooks {@link PolyScreens} into NeoForge's {@code RegisterMenuScreensEvent}.
 *
 * <p>Call {@link #registerToBus(IEventBus)} from your mod constructor (client dist only):
 * <pre>{@code
 * if (FMLLoader.getDist().isClient()) {
 *     NeoPolyScreens.registerToBus(bus);
 * }
 * }</pre>
 */
public class NeoPolyScreens {

    public static void registerToBus(IEventBus bus) {
        bus.addListener(NeoPolyScreens::onRegisterMenuScreens);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        PolyScreens.flush();
    }
}
