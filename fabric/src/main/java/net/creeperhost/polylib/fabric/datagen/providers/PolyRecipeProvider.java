package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PolyRecipeProvider extends FabricRecipeProvider
{
    private final ModuleType moduleType;
    private final Map<ResourceLocation, RecipeBuilder> values = new HashMap<>();

    public PolyRecipeProvider(FabricDataOutput dataOutput, ModuleType moduleType)
    {
        super(dataOutput);
        this.moduleType = moduleType;
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
    public void buildRecipes(RecipeOutput exporter)
    {
        values.forEach((resourceLocation, recipeBuilder) -> recipeBuilder.save(exporter, resourceLocation));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer)
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return null;
        recipePathProvider.root = appendPath(moduleType);
        return super.run(writer);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }
}
