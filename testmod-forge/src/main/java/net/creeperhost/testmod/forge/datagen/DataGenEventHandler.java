package net.creeperhost.testmod.forge.datagen;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.forge.datagen.providers.DynamicTextureProvider;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.client.gui.ModularGuiTest;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by brandon3055 on 07/09/2023
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEventHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            DynamicTextureProvider textureProvider = new DynamicTextureProvider(gen, event.getExistingFileHelper(), TestMod.MOD_ID);

            textureProvider.addDynamicTextures(new ModularGuiTest());
            textureProvider.addDynamicTexture(new ResourceLocation(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla.png"), new ResourceLocation(TestMod.MOD_ID, "textures/gui/test_dynamic_texture.png"), 200, 200, 4, 4, 4, 4);

            gen.addProvider(true, textureProvider);
        }
    }
}
