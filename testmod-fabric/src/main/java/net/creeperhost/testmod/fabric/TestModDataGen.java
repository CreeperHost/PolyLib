package net.creeperhost.testmod.fabric;

import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.creeperhost.polylib.fabric.datagen.providers.*;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import static net.minecraft.data.recipes.RecipeProvider.has;

public class TestModDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        System.out.println("COMMON :" + PolyDataGen.COMMON);
        System.out.println("FORGE :" + PolyDataGen.FORGE);
        System.out.println("FABRIC :" + PolyDataGen.FABRIC);
        var pack = fabricDataGenerator.createPack();

        pack.addProvider((output, registriesFuture) -> {
            PolyLanguageProvider languageProvider = new PolyLanguageProvider(output, ModuleType.COMMON);
            languageProvider.add(TestItems.MACHINE_TEST_ITEMBLOCK.get(), "Test Machine", ModuleType.COMMON);
            languageProvider.add(TestItems.MULTIBLOCK_TEST_ITEMBLOCK.get(), "Test Multiblock block", ModuleType.COMMON);
            languageProvider.add(TestItems.INVENTORY_TEST_ITEMBLOCK.get(), "inventory Test Block", ModuleType.COMMON);

            return languageProvider;
        });


//        for(ModuleType value : ModuleType.values())
//        {
//            pack.addProvider((output, registriesFuture) -> new PolyLanguageProvider(output, value));
//        }

//        for (ModuleType value : ModuleType.values())
//        {
//            //Ignore Quilt
//            if(value == ModuleType.QUILT) continue;
//
//            PolyLanguageProvider languageProvider = new PolyLanguageProvider(fabricDataGenerator, value);
//            PolyBlockLootProvider blockLootProvider = new PolyBlockLootProvider(fabricDataGenerator, value);
//            //TODO broken
////            PolyBlockTagProvider blockTagProvider = new PolyBlockTagProvider(fabricDataGenerator, value);
////            PolyItemTagProvider itemTagProvider = new PolyItemTagProvider(fabricDataGenerator, blockTagProvider, value);
//            PolyRecipeProvider recipeProvider = new PolyRecipeProvider(fabricDataGenerator, value);
//            PolyAdvancementProvider advancementProvider = new PolyAdvancementProvider(fabricDataGenerator, value);
//            PolyModelProvider modelProvider = new PolyModelProvider(fabricDataGenerator, value);
//
//            languageProvider.add("itemGroup.testmod.creative_tab", "FORGE", ModuleType.FORGE);
//            languageProvider.add("itemGroup.testmod.creative_tab", "FABRIC", ModuleType.FABRIC);
//            languageProvider.add("itemGroup.testmod.creative_tab", "COMMON", ModuleType.COMMON);
//
//            TestBlocks.BLOCKS.forEach(blockRegistrySupplier ->
//            {
//                blockLootProvider.addSelfDrop(blockRegistrySupplier.get(), value);
//                blockTagProvider.add(BlockTags.MINEABLE_WITH_PICKAXE, blockRegistrySupplier.get(), value);
//                modelProvider.addSimpleBlockModel(blockRegistrySupplier.get(), new ResourceLocation("minecraft", "block/stone"), value);
//            });
//
//            recipeProvider.add(RecipeProvider.slabBuilder(TestBlocks.INVENTORY_TEST_BLOCK.get(), Ingredient.of(
//                    Items.LEAD)).unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN)), ModuleType.COMMON);
//
//            itemTagProvider.add(ItemTags.ANVIL, TestItems.INVENTORY_TEST_ITEMBLOCK.get(), ModuleType.COMMON);
//
//            fabricDataGenerator.addProvider(languageProvider);
//            fabricDataGenerator.addProvider(blockLootProvider);
//            fabricDataGenerator.addProvider(blockTagProvider);
//            fabricDataGenerator.addProvider(itemTagProvider);
//            fabricDataGenerator.addProvider(recipeProvider);
//            fabricDataGenerator.addProvider(advancementProvider);
//            fabricDataGenerator.addProvider(modelProvider);
//        }
    }
}
