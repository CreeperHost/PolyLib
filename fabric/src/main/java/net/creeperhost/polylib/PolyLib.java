package net.creeperhost.polylib;

import net.creeperhost.polylib.init.DataComps;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class PolyLib implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        CommonClass.registerConfig();

        if (DataComps.isDataEnabled()) {
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_toggle_active"), DataComps.ITEM_TOGGLE_ACTIVE);
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_energy"), DataComps.ITEM_ENERGY);
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_tile_data"), DataComps.ITEM_TILE_DATA);
        }
        CommonClass.init();
    }
}
