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

    public static final DeferredRegister<DataComponentType<?>> DATA = DeferredRegister.create(PolyLib.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<Boolean>> ITEM_TOGGLE_ACTIVE = DATA.register("item_toggle_active", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final RegistrySupplier<DataComponentType<Long>> ITEM_ENERGY = DATA.register("item_energy", () -> DataComponentType.<Long>builder().persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG).build());
    public static final RegistrySupplier<DataComponentType<CustomData>> ITEM_TILE_DATA = DATA.register("item_tile_data", () -> DataComponentType.<CustomData>builder().persistent(CustomData.CODEC).build());

}
