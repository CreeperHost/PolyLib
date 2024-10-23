package net.creeperhost.polylib.helpers;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeHelper
{
    public static Set<Recipe<?>> findRecipesByType(RecipeType<?> typeIn, Level world)
    {
        Set<RecipeHolder<?>> recipeHolders = world != null ? world.getServer().getRecipeManager().getRecipes().stream().filter(
                recipe -> recipe.value().getType() == typeIn).collect(Collectors.toSet()) : Collections.emptySet();

        //TODO test if this worked
        Set<Recipe<?>> recipes = Collections.emptySet();
        for (RecipeHolder<?> recipeHolder : recipeHolders)
        {
            recipes.add(recipeHolder.value());
        }

        return recipes;
    }
}
