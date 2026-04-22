package net.creeperhost.polylib.init;

import com.mojang.serialization.Codec;
import net.creeperhost.polylib.PolylibCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.CustomData;

/**
 * Created by brandon3055 on 03/05/2024
 */
public class DataComps {

    public static final DataComponentType<Boolean> ITEM_TOGGLE_ACTIVE = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final DataComponentType<Long> ITEM_ENERGY = DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build();
    public static final DataComponentType<CustomData> ITEM_TILE_DATA = DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).build();

    private static boolean activatedByMod = false;

    public static boolean isDataEnabled() {
        if (activatedByMod) return true;
        return !PolylibCommon.configData.serverOnlySupport;
    }

    public static void registerData() {
        activatedByMod = true;
    }

    public static DataComponentType<Boolean> getItemToggleActive() {
        return ITEM_TOGGLE_ACTIVE;
    }

    public static DataComponentType<Long> getItemEnergy() {
        return ITEM_ENERGY;
    }

    public static DataComponentType<CustomData> getItemTileData() {
        return ITEM_TILE_DATA;
    }
}
