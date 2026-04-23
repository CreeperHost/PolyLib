package net.creeperhost.polylib;

import net.creeperhost.polylib.init.DataComps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class PolyLibFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        PolylibCommon.registerConfig();
        PolylibCommon.init();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            PolyLibClientFabric.init();
        }

        if (DataComps.isDataEnabled()) {
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_toggle_active"), DataComps.ITEM_TOGGLE_ACTIVE);
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_energy"), DataComps.ITEM_ENERGY);
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_tile_data"), DataComps.ITEM_TILE_DATA);
        }
    }
}
