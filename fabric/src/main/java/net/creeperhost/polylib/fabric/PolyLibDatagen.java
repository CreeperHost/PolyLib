package net.creeperhost.polylib.fabric;

import dev.architectury.platform.Platform;
import net.creeperhost.polylib.generators.PolyDataGenerator;
import net.creeperhost.polylib.generators.PolyDataGeneratorRegistryEvent;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PolyLibDatagen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        PolyDataGeneratorRegistryEvent.REGISTER_EVENT.invoker().onRegister(of(fabricDataGenerator));
    }

    public static PolyDataGenerator of(FabricDataGenerator fabricDataGenerator)
    {
        return new PolyDataGenerator(fabricDataGenerator.getOutputFolder(), Platform.getMod(fabricDataGenerator.getModId()), fabricDataGenerator.isStrictValidationEnabled());
    }

}
