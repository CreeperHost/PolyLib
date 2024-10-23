package net.creeperhost.polylib.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//Thanks Pup, Copied from Mek
public abstract class WrappedShapelessRecipe implements CraftingRecipe
{
    private final ShapelessRecipe internal;

    protected WrappedShapelessRecipe(ShapelessRecipe internal)
    {
        this.internal = internal;
    }

    public ShapelessRecipe getInternal()
    {
        return internal;
    }

    @NotNull
    @Override
    public CraftingBookCategory category()
    {
        return internal.category();
    }

    @Override
    public abstract @NotNull ItemStack assemble(CraftingInput recipeInput, HolderLookup.Provider provider);

    @Override
    public boolean matches(CraftingInput recipeInput, Level level)
    {
        return internal.matches(recipeInput, level);
    }

    @Override
    public @NotNull List<RecipeDisplay> display()
    {
        return internal.display();
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory()
    {
        return internal.recipeBookCategory();
    }

    @Override
    public @NotNull RecipeType<CraftingRecipe> getType()
    {
        return internal.getType();
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer()
    {
        return internal.getSerializer();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput)
    {
        return internal.getRemainingItems(craftingInput);
    }

    @Override
    public boolean isSpecial()
    {
        return internal.isSpecial();
    }

    @Override
    public @NotNull String group()
    {
        return internal.group();
    }
}
