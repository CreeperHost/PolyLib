package net.creeperhost.polylib;

import net.creeperhost.polylib.init.DataComps;
import net.creeperhost.polylib.platform.NeoForgeNetworkHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Constants.MOD_ID)
public class PolyLibNeoForge
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public PolyLibNeoForge(IEventBus eventBus)
    {
        PolylibCommon.registerConfig();
        PolylibCommon.init();

        eventBus.addListener(NeoForgeNetworkHelper::onRegisterPayloads);

        if (FMLLoader.getCurrent().getDist().isClient()) {
            PolyLibClientNeoForge.init(eventBus);
        }

        if (DataComps.isDataEnabled()) {
            COMPONENTS.register("item_toggle_active", () -> DataComps.ITEM_TOGGLE_ACTIVE);
            COMPONENTS.register("item_energy", () -> DataComps.ITEM_ENERGY);
            COMPONENTS.register("item_tile_data", () -> DataComps.ITEM_TILE_DATA);
        }

        COMPONENTS.register(eventBus);
    }
}
