package net.creeperhost.testmod.fabric;

import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.creeperhost.polylib.fabric.datagen.providers.*;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
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
            languageProvider.add(TestItems.MGUI_TEST_ITEMBLOCK.get(), "Modular Gui Test Block", ModuleType.COMMON);
            languageProvider.add(TestItems.CREATIVE_ENERGY_BLOCK.get(), "Creative Power source", ModuleType.COMMON);
            languageProvider.add(TestItems.TEST_ENERGY_ITEM.get(), "Test Energy Item", ModuleType.COMMON);

            return languageProvider;
        });

        pack.addProvider((output, registriesFuture) -> {
           PolyBlockLootProvider blockLootProvider = new PolyBlockLootProvider(output, ModuleType.COMMON);
           TestBlocks.BLOCKS.forEach(blockRegistrySupplier -> blockLootProvider.addSelfDrop(blockRegistrySupplier.get(), ModuleType.COMMON));
           return blockLootProvider;
        });

        pack.addProvider((output, registriesFuture) -> {
            PolyBlockTagProvider blockTagProvider = new PolyBlockTagProvider(output, registriesFuture, ModuleType.COMMON);
            TestBlocks.BLOCKS.forEach(blockRegistrySupplier -> blockTagProvider.add(BlockTags.MINEABLE_WITH_PICKAXE, blockRegistrySupplier.get(), ModuleType.COMMON));
            return blockTagProvider;
        });

        pack.addProvider((output, registriesFuture) -> {
           PolyRecipeProvider recipeProvider = new PolyRecipeProvider(output, ModuleType.COMMON);

           recipeProvider.add(RecipeProvider.slabBuilder(RecipeCategory.MISC,
                   TestBlocks.INVENTORY_TEST_BLOCK.get(),
                   Ingredient.of(Items.LEAD)).unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN)), ModuleType.COMMON);

            recipeProvider.add(RecipeProvider.slabBuilder(RecipeCategory.MISC,
                    TestBlocks.INVENTORY_TEST_BLOCK.get(),
                    Ingredient.of(Items.COPPER_BLOCK)).unlockedBy("has_log", has(ItemTags.LOGS_THAT_BURN)), new ResourceLocation("testmod", "namedrecipe"), ModuleType.COMMON);

           return recipeProvider;
        });

        pack.addProvider((output, registriesFuture) -> {
            PolyItemTagProvider itemTagProvider = new PolyItemTagProvider(output, registriesFuture, null, ModuleType.COMMON);
            TestItems.ITEMS.forEach(itemRegistrySupplier -> itemTagProvider.add(ItemTags.ANVIL, itemRegistrySupplier.get(), ModuleType.COMMON));
            return itemTagProvider;
        });

//        pack.addProvider((output, registriesFuture) -> {
//            PolyAdvancementProvider advancementProvider = new PolyAdvancementProvider(output, ModuleType.COMMON);
//            return advancementProvider;
//        });

        pack.addProvider((output, registriesFuture) -> {
           PolyModelProvider modelProvider = new PolyModelProvider(output, ModuleType.COMMON);
           modelProvider.addSimpleBlockModel(TestBlocks.INVENTORY_TEST_BLOCK.get(), new ResourceLocation("minecraft", "block/stone"), ModuleType.COMMON);
           modelProvider.addSimpleBlockModel(TestBlocks.MULTIBLOCK_TEST_BLOCK.get(), new ResourceLocation("minecraft", "block/stone"), ModuleType.COMMON);
           modelProvider.addSimpleBlockModel(TestBlocks.MGUI_TEST_BLOCK.get(), new ResourceLocation("minecraft", "block/stone_bricks"), ModuleType.COMMON);
           modelProvider.addSimpleBlockModel(TestBlocks.CREATIVE_ENERGY_BLOCK.get(), new ResourceLocation("minecraft", "block/stone"), ModuleType.COMMON);

           modelProvider.addSimpleItemModel(TestItems.TEST_ENERGY_ITEM.get(), ModuleType.COMMON);
           return modelProvider;
        });
    }
}
