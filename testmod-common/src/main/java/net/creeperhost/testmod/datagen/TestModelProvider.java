package net.creeperhost.testmod.datagen;

import net.creeperhost.polylib.generators.PolyDataGenerator;
import net.creeperhost.polylib.generators.PolyModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;

public class TestModelProvider extends PolyModelProvider
{
    public TestModelProvider(PolyDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator)
    {
        System.out.println("Hi from generateBlockStateModels()");

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator)
    {
        System.out.println("Hi from generateItemModels()");
    }
}
