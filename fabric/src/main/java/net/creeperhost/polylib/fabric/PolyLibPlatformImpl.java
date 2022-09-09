package net.creeperhost.polylib.fabric;

import net.creeperhost.polylib.fabric.datagenerators.BlockTagGenerator;
import net.creeperhost.polylib.fabric.datagenerators.ItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.DataGenerator;

import java.nio.file.Path;

public class PolyLibPlatformImpl
{
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static void registerDefaultGenerators(DataGenerator dataGenerator)
    {
        if(dataGenerator instanceof FabricDataGenerator fabricDataGenerator)
        {
            fabricDataGenerator.addProvider(BlockTagGenerator::new);
            fabricDataGenerator.addProvider(ItemTagGenerator::new);
        }
    }
}
