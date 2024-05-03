package net.creeperhost.polylib.blocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This class can be implemented on any {@link BlockEntity}, then as long as that entity belongs to a {@link PolyEntityBlock},
 * It will keep its data when harvested.
 * <p>
 * Created by brandon3055 on 19/02/2024
 */
public interface DataRetainingBlock {

    /**
     * @param nbt         Write any data you wish to retain to this compound.
     *                    This will be a new empty compound so need to worry about tag conflicts.
     * @param willHarvest This will be true if the block is actually about to be harvested.
     *                    vs for example, using pick-block to copy the block.
     */
    void writeToItemStack(HolderLookup.Provider provider, CompoundTag nbt, boolean willHarvest);

    /**
     * @param nbt Will contain all data written in {@link #writeToItemStack(CompoundTag, boolean)}.
     *            Use this to restore the block ewntity state.
     */
    void readFromItemStack(HolderLookup.Provider provider, CompoundTag nbt);

    /**
     * @return false to completely disable tile data saving and restore default harvest logic. Needed because IDataRetainingTile is now implemented on {@link PolyBlockEntity} by default.
     */
    default boolean saveToItem() {
        return true;
    }
}
