package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.creeperhost.polylib.fabric.datagen.PolyDataGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PolyRecipeProvider extends FabricRecipeProvider
{
    private final ModuleType moduleType;
    private final List<RecipeBuilder> values = new ArrayList<>();

    public PolyRecipeProvider(FabricDataGenerator dataGenerator, FabricDataOutput dataOutput, ModuleType moduleType)
    {
        super(dataOutput);
        this.moduleType = moduleType;

        PolyLib.LOGGER.info("PolyRecipeProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    public void add(RecipeBuilder recipeBuilder, ModuleType moduleType)
    {
        if (this.moduleType == moduleType) values.add(recipeBuilder);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter)
    {
        values.forEach((recipeBuilder) -> recipeBuilder.save(exporter));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer)
    {
        return super.run(writer);
    }

    //    @Override
//    public void run(CachedOutput writer)
//    {
//        //If values is empty don't generate an empty array json file
//        if (values.isEmpty()) return;
//        recipePathProvider.root = appendPath(moduleType);
//        super.run(writer);
//    }

    public Path appendPath(ModuleType moduleType)
    {
        return PolyDataGen.getPathFromModuleType(moduleType).resolve("data");
    }
}
