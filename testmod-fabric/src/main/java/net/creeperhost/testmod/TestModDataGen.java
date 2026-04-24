package net.creeperhost.testmod;

import net.creeperhost.testmod.datagen.TestModFabricLangProvider;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestCreativeTabs;
import net.creeperhost.testmod.init.TestItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TestModDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        // Touch each init class to trigger the static field initializers.
        // Those initializers call registerItem/registerBlock/registerCreativeTab
        // which populate PolyLangContributions — without calling .init() which
        // would attempt to write to the already-frozen registry.
        TestItems.class.getName();
        TestBlocks.class.getName();
        TestCreativeTabs.class.getName();

        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(TestModFabricLangProvider::new);
    }
}
