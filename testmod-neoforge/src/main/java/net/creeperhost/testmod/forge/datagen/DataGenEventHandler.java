package net.creeperhost.testmod.forge.datagen;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.neoforge.datagen.providers.DynamicTextureProvider;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockGui;
import net.creeperhost.testmod.client.gui.ModularGuiTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Created by brandon3055 on 07/09/2023
 */
@EventBusSubscriber (bus = EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            DynamicTextureProvider textureProvider = new DynamicTextureProvider(gen, event.getExistingFileHelper(), TestMod.MOD_ID);

            textureProvider.addDynamicTextures(new ModularGuiTest());
            textureProvider.addDynamicTextures(new MGuiTestBlockGui());
            textureProvider.addDynamicTexture(ResourceLocation.fromNamespaceAndPath(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla.png"), ResourceLocation.fromNamespaceAndPath(TestMod.MOD_ID, "textures/gui/test_dynamic_texture.png"), 200, 200, 4, 4, 4, 4);

            gen.addProvider(true, textureProvider);
        }
    }
}
