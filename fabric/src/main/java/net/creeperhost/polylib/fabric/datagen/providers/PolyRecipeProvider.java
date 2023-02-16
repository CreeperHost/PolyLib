package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PolyRecipeProvider extends FabricRecipeProvider
{
    private final ModuleType moduleType;
    private final Path basePath;
    private final List<RecipeBuilder> values = new ArrayList<>();


    public PolyRecipeProvider(FabricDataGenerator dataGenerator, ModuleType moduleType)
    {
        super(dataGenerator);
        this.moduleType = moduleType;
        basePath = Path.of("").toAbsolutePath().getParent().getParent();

        PolyLib.LOGGER.info("PolyRecipeProvider created for " + dataGenerator.getModId() + " " + moduleType.name());
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> exporter)
    {
        values.forEach((recipeBuilder) -> recipeBuilder.save(exporter));
    }

    public void add(RecipeBuilder recipeBuilder, ModuleType moduleType)
    {
        if (this.moduleType == moduleType) values.add(recipeBuilder);
    }

    @Override
    public void run(CachedOutput writer)
    {
        //If values is empty don't generate an empty array json file
        if (values.isEmpty()) return;
        recipePathProvider.root = appendPath(moduleType);
        super.run(writer);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return basePath.resolve(moduleType.name().toLowerCase() + "/src/generated/resources/data");
    }
}
