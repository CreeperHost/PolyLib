package net.creeperhost.testmod.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.creeperhost.polylib.fabric.client.ResourceReloadListenerWrapper;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class TestModFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        TestMod.init();


        if (Platform.getEnv() == EnvType.CLIENT)
        {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListenerWrapper(TestModTextures::getUploader, new ResourceLocation(TestMod.MOD_ID, "gui_atlas_reload")));
        }
    }
}
