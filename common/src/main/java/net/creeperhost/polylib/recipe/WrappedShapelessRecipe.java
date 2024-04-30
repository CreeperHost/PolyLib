package net.creeperhost.polylib.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
    public abstract @NotNull ItemStack assemble(CraftingContainer container, HolderLookup.Provider provider);


    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level world)
    {
        return internal.matches(inv, world) && !assemble(inv, world.registryAccess()).isEmpty();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return internal.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider)
    {
        return internal.getResultItem(provider);
    }

    @NotNull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer inv)
    {
        return internal.getRemainingItems(inv);
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients()
    {
        return internal.getIngredients();
    }

    @Override
    public boolean isSpecial()
    {
        return internal.isSpecial();
    }

    @NotNull
    @Override
    public String getGroup()
    {
        return internal.getGroup();
    }

    @NotNull
    @Override
    public ItemStack getToastSymbol()
    {
        return internal.getToastSymbol();
    }

    @Override
    public boolean isIncomplete()
    {
        return internal.isIncomplete();
    }
}
