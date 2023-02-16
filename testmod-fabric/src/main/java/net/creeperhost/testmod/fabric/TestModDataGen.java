package net.creeperhost.testmod.fabric;

import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.creeperhost.polylib.fabric.datagen.providers.*;
import net.creeperhost.testmod.init.TestBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;

public class TestModDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        System.out.println("COMMON :" + PolyDataGen.COMMON);
        System.out.println("FORGE :" + PolyDataGen.FORGE);
        System.out.println("FABRIC :" + PolyDataGen.FABRIC);

        for (ModuleType value : ModuleType.values())
        {
            PolyLanguageProvider languageProvider = new PolyLanguageProvider(fabricDataGenerator, value);
            //        PolyBlockLootProvider blockLootProvider = new PolyBlockLootProvider(fabricDataGenerator, value);
            //        PolyBlockTagProvider blockTagProvider = new PolyBlockTagProvider(fabricDataGenerator, value);
            //        PolyItemTagProvider itemTagProvider = new PolyItemTagProvider(fabricDataGenerator, blockTagProvider, value);
            //        PolyRecipeProvider recipeProvider = new PolyRecipeProvider(fabricDataGenerator, value);
            //        PolyAdvancementProvider advancementProvider = new PolyAdvancementProvider(fabricDataGenerator, value);
            //        PolyModelProvider modelProvider = new PolyModelProvider(fabricDataGenerator, value);
            //
            languageProvider.add("itemGroup.testmod.creative_tab", "FORGE", ModuleType.FORGE);
            languageProvider.add("itemGroup.testmod.creative_tab", "FABRIC", ModuleType.FABRIC);
            languageProvider.add("itemGroup.testmod.creative_tab", "COMMON", ModuleType.COMMON);

            //        TestBlocks.BLOCKS.forEach(blockRegistrySupplier ->
            //        {
            //            blockLootProvider.addSelfDrop(blockRegistrySupplier.get(), value);
            //            blockTagProvider.add(BlockTags.MINEABLE_WITH_PICKAXE, blockRegistrySupplier.get(), value);
            //            modelProvider.addSimpleBlockModel(blockRegistrySupplier.get(), new ResourceLocation("minecraft", "block/stone"), value);
            //        });
            //
            fabricDataGenerator.addProvider(languageProvider);
            //        fabricDataGenerator.addProvider(blockLootProvider);
            //        fabricDataGenerator.addProvider(blockTagProvider);
            //        fabricDataGenerator.addProvider(itemTagProvider);
            //        fabricDataGenerator.addProvider(recipeProvider);
            //        fabricDataGenerator.addProvider(advancementProvider);
            //        fabricDataGenerator.addProvider(modelProvider);
        }
    }
}
