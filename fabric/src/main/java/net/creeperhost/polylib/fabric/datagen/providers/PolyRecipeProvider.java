package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PolyRecipeProvider extends FabricRecipeProvider
{
    private final ModuleType moduleType;
    private final Map<ResourceLocation, RecipeBuilder> values = new HashMap<>();
    private final HolderGetter<Item> items;

    public PolyRecipeProvider(FabricDataOutput dataOutput, ModuleType moduleType, CompletableFuture<HolderLookup.Provider> registryLookup)
    {
        super(dataOutput, registryLookup);
        this.moduleType = moduleType;
        items = registryLookup.join().lookupOrThrow(Registries.ITEM);
    }

    public void add(RecipeBuilder recipeBuilder, ResourceLocation id, ModuleType moduleType)
    {
        if (this.moduleType == moduleType) values.put(id, recipeBuilder);
    }

    public void add(RecipeBuilder recipeBuilder, ModuleType moduleType)
    {
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(recipeBuilder.getResult().asItem());
        add(recipeBuilder, resourceLocation, moduleType);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter)
    {
        return new RecipeProvider(registryLookup, exporter)
        {
            @Override
            public void buildRecipes()
            {
                values.forEach((resourceLocation, recipeBuilder) -> recipeBuilder.save(exporter));
            }
        };
    }

    @Override
    public @NotNull String getName()
    {
        return PolyLib.MOD_ID;
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }

    //Has helpers
    public Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike)
    {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(this.items, new ItemLike[]{itemLike}));
    }

    public Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tagKey)
    {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(items, tagKey));
    }
}
