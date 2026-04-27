package net.creeperhost.testmod;

import net.creeperhost.polylib.neoforge.registry.NeoPolyRegistry;
import net.creeperhost.polylib.neoforge.registry.NeoPolyScreens;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(TestModCommon.MOD_ID)
public class TestModNeoForge
{
    public TestModNeoForge(ModContainer container, IEventBus bus) {
        TestModCommon.LOGGER.info("[TESTMOD-DEBUG] constructor called, dist={}", FMLLoader.getCurrent().getDist());
        TestModCommon.init();
        NeoPolyRegistry.registerToBus(bus, TestModCommon.MOD_ID);

        if (FMLLoader.getCurrent().getDist().isClient()) {
            NeoPolyScreens.registerToBus(bus);
            TestModNeoForgeClient.init(container, bus);
        }
    }
}
