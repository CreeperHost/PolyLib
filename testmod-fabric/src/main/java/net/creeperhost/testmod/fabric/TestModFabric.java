package net.creeperhost.testmod.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

public class TestModFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        TestMod.init();

        if (Platform.getEnv() == EnvType.CLIENT)
        {
//            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListenerWrapper(TestModTextures::getAtlasHolder, ResourceLocation.fromNamespaceAndPath(TestMod.MOD_ID, "gui_atlas_reload")));
        }
    }
}
