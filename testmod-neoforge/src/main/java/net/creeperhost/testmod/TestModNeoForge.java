package net.creeperhost.testmod;

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

        if (FMLLoader.getCurrent().getDist().isClient()) {
            TestModNeoForgeClient.init(container, bus);
        }
    }
}
