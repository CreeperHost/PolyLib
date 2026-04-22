package net.creeperhost.polylib;

import net.creeperhost.polylib.init.DataComps;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Constants.MOD_ID)
public class PolyLib
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public PolyLib(IEventBus eventBus)
    {
        CommonClass.registerConfig();
        CommonClass.init();

        if (DataComps.isDataEnabled()) {
            COMPONENTS.register("item_toggle_active", () -> DataComps.ITEM_TOGGLE_ACTIVE);
            COMPONENTS.register("item_energy", () -> DataComps.ITEM_ENERGY);
            COMPONENTS.register("item_tile_data", () -> DataComps.ITEM_TILE_DATA);
        }

        COMPONENTS.register(eventBus);
    }
}