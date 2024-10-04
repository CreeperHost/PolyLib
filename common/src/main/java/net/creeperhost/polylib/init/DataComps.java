package net.creeperhost.polylib.init;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.polylib.PolyLib;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.CustomData;

/**
 * Created by brandon3055 on 03/05/2024
 */
public class DataComps {

    private static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(PolyLib.MOD_ID, Registries.DATA_COMPONENT_TYPE);
    private static RegistrySupplier<DataComponentType<Boolean>> ITEM_TOGGLE_ACTIVE;
    private static RegistrySupplier<DataComponentType<Long>> ITEM_ENERGY;
    private static RegistrySupplier<DataComponentType<CustomData>> ITEM_TILE_DATA;

    private static boolean dataEnabled = false;
    private static boolean activatedByMod = false;

    public static void init() {
        if (!PolyLib.configData.serverOnlySupport){
            _registerData();
        }
    }

    public static void registerData() {
        activatedByMod = true;
        _registerData();
    }

    private static void _registerData() {
        if (dataEnabled) return;
        dataEnabled = true;

        DATA.register();
        ITEM_TOGGLE_ACTIVE = DATA.register("item_toggle_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
        ITEM_ENERGY = DATA.register("item_energy", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
        ITEM_TILE_DATA = DATA.register("item_tile_data", () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).build());
    }

    private static void checkEnabled() {
        if (!dataEnabled) {
            throw new RuntimeException("A mod is attempting to use PolyLib's Item Data, But Item data has not been enabled. The offending mod needs to call PolyLib.initPolyItemData() in its init.\nYou can work around this issue by setting serverOnlySupport to false in PolyLib's Config.");
        }
        if (!activatedByMod) {
            PolyLib.LOGGER.warn("A mod is using PolyLib's Item Data system without calling PolyLib.initPolyItemData() in its init method. This will break in later versions.");
        }
    }

    public static DataComponentType<Boolean> getItemToggleActive() {
        checkEnabled();
        return ITEM_TOGGLE_ACTIVE.get();
    }

    public static DataComponentType<Long> getItemEnergy() {
        checkEnabled();
        return ITEM_ENERGY.get();
    }

    public static DataComponentType<CustomData> getItemTileData() {
        checkEnabled();
        return ITEM_TILE_DATA.get();
    }
}
