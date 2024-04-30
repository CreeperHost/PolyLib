package net.creeperhost.polylib.blocks;

import net.creeperhost.polylib.blocks.RedstoneActivatedBlock.RSMode;
import net.creeperhost.polylib.containers.PolyBlockContainerMenu;
import net.creeperhost.polylib.data.DataManagerBlock;
import net.creeperhost.polylib.data.TileDataManager;
import net.creeperhost.polylib.data.serializable.AbstractDataStore;
import net.creeperhost.polylib.data.serializable.BooleanData;
import net.creeperhost.polylib.data.serializable.EnumData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Base class for all poly block entities. This implements the data manager system as well as a bunch of other useful features.
 * Designed to work with {@link PolyBlockEntity}
 * <p>
 * Created by brandon3055 on 19/02/2024
 */
public class PolyBlockEntity extends BlockEntity implements Nameable, DataManagerBlock, DataRetainingBlock {

    private final TileDataManager<PolyBlockEntity> dataManager = new TileDataManager<>(this);
    private final Set<Player> accessingPlayers = new HashSet<>();
    private Component customName = null;
    private int tick = 0;

    private EnumData<RSMode> redstoneMode = null;
    private BooleanData isPowered = null;

    public PolyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        if (this instanceof RedstoneActivatedBlock) {
            redstoneMode = register("rs_mode", new EnumData<>(RSMode.ALWAYS_ACTIVE), SAVE_BOTH, SYNC, CLIENT_CONTROL);
            isPowered = register("rs_powered", new BooleanData(false), SAVE, SYNC);
        }
    }

    @Override
    public TileDataManager<PolyBlockEntity> getDataManager() {
        return dataManager;
    }

    public <D extends AbstractDataStore<?>> D register(String name, D data, int... flags) {
        return dataManager.register(name, data, flags);
    }

    /**
     * This tick method can be enabled when setting the {@link BlockEntityType} in {@link PolyEntityBlock} via {@link PolyEntityBlock#setBlockEntity(Supplier, boolean)}
     * When overriding this you must call super.tick() in order for Data Manager synchronization to work.
     */
    public void tick() {
        tick++;
        dataManager.tick();
    }

    //=== Container ===

    /**
     * @return a set of players currently accessing this tile's container.
     * playerAccessTracking must be enabled in this tile's constructor in order for this to work.
     */
    public Set<Player> getAccessingPlayers() {
        accessingPlayers.removeIf(e -> !(e.containerMenu instanceof PolyBlockContainerMenu<?> container) || container.tile != this); //Clean up set
        return accessingPlayers;
    }

    public void onPlayerOpenContainer(Player player) {
        accessingPlayers.add(player);
    }

    public void onPlayerCloseContainer(Player player) {
        accessingPlayers.remove(player);
        accessingPlayers.removeIf(e -> !(e.containerMenu instanceof PolyBlockContainerMenu<?> container) || container.tile != this); //Clean up set
    }

    public int getAccessDistanceSq() {
        return 64;
    }

    //=== Name ===

    @Override
    public Component getName() {
        if (hasCustomName()) {
            return getCustomName();
        }
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(@Nullable Component customName) {
        this.customName = customName;
    }

    @Override
    public Component getDisplayName() {
        return Nameable.super.getDisplayName();
    }

    //=== Network ===

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider)
    {
        return saveWithoutMetadata(provider);
    }

    //=== Data ===


    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider)
    {
        super.saveAdditional(compoundTag, provider);
        dataManager.save(compoundTag);
        writeExtraData(compoundTag);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider)
    {
        super.loadAdditional(compoundTag, provider);
        dataManager.load(compoundTag);
        readExtraData(compoundTag);
    }


    @Override
    public void writeToItemStack(CompoundTag nbt, boolean willHarvest) {
        dataManager.saveToItem(nbt);
        writeExtraData(nbt);
    }

    @Override
    public void readFromItemStack(CompoundTag nbt) {
        dataManager.loadFromItem(nbt);
        readExtraData(nbt);
    }

    /**
     * Convenience method for writing extra data to both the tile NBT, and item NBT when harvested.
     */
    public void writeExtraData(CompoundTag nbt) {

    }

    /**
     * Convenience method for reading extra data from both the tile NBT, and item NBT.
     */
    public void readExtraData(CompoundTag nbt) {

    }

    //=== Misc ===

    /**
     * Only works on ticking tiles that call super.update()
     *
     * @return an internal tick timer specific to this tile
     */
    public int getTime() {
        return tick;
    }

    /**
     * Only works on ticking tiles that call super.update()
     *
     * @return true once every 'tickInterval' based on the tiles internal timer.
     */
    public boolean onInterval(int tickInterval) {
        return tick % tickInterval == 0;
    }

    //=== Redstone Control ===

    public RSMode getRSMode() {
        if (!(this instanceof RedstoneActivatedBlock)) {
            throw new IllegalStateException("Tile does not implement IRSSwitchable");
        }
        return redstoneMode.get();
    }

    public void setRSMode(RSMode mode) {
        if (!(this instanceof RedstoneActivatedBlock)) {
            throw new IllegalStateException("Tile does not implement IRSSwitchable");
        }
        if (level.isClientSide){
            sendDataValueToServer(redstoneMode, mode);
        }else {
            redstoneMode.set(mode);
        }
    }

    public void cycleRSMode(boolean reverse) {
        redstoneMode.set(redstoneMode.get().next(reverse));
    }

    /**
     * If this BlockEntity implements {@link RedstoneActivatedBlock} this method can be used to check if the tile is currently allowed to run.
     * This takes the current redstone state as well as the current control mode into consideration.
     *
     * @return true if the current RS control mode allows the block to run given its current redstone state.
     * Will also default to true if this is not a RedstoneActivatedBlock
     */
    public boolean isTileEnabled() {
        if (this instanceof RedstoneActivatedBlock) {
            return redstoneMode.get().canRun(isPowered.get());
        }
        return true;
    }

    public void onNeighborChange(Block fromBlock, BlockPos fromPos, boolean isMoving) {
        if (this instanceof RedstoneActivatedBlock) {
            boolean lastSignal = isPowered.get();
            isPowered.set(level.hasNeighborSignal(worldPosition));
            if (isPowered.get() != lastSignal) {
                onSignalChange(isPowered.get());
            }
        }
    }

    public void onSignalChange(boolean newSignal) {

    }
}
