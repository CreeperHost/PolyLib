package net.creeperhost.polylib.fabric.datagen.providers;

import net.creeperhost.polylib.fabric.datagen.ModuleType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PolyRecipeProvider extends FabricRecipeProvider
{
    private final ModuleType moduleType;
    private final Path basePath;
    private final Map<RecipeBuilder, Consumer<FinishedRecipe>> values = new HashMap<>();


    public PolyRecipeProvider(FabricDataGenerator dataGenerator, ModuleType moduleType)
    {
        super(dataGenerator);
        this.moduleType = moduleType;
        basePath = Path.of("").toAbsolutePath().getParent().getParent().getParent();
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> exporter)
    {
        values.forEach((recipeBuilder, consumer) -> recipeBuilder.save(exporter));
    }

    public void add(RecipeBuilder recipeBuilder, Consumer<FinishedRecipe> consumer, ModuleType moduleType)
    {
        if(this.moduleType == moduleType)
            values.put(recipeBuilder, consumer);
    }

    @Override
    public void run(CachedOutput writer)
    {
        //If values is empty don't generate an empty array json file
        if(values.isEmpty()) return;
        recipePathProvider.root = appendPath(moduleType);
        super.run(writer);
    }

    public Path appendPath(ModuleType moduleType)
    {
        return basePath.resolve(moduleType.name().toLowerCase() + "/src/generated/resources");
    }
}
