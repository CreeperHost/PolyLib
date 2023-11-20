package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.fabric.client.ResourceReloadListenerWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class PolyLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PolyLib.init();
        if (Platform.getEnv() == EnvType.CLIENT)
        {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListenerWrapper(PolyTextures::getUploader, new ResourceLocation(PolyLib.MOD_ID, "gui_atlas_reload")));
        }
    }
}
