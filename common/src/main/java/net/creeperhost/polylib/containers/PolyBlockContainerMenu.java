package net.creeperhost.polylib.containers;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

/**
 * Base container menu for block-entity backed modular GUIs.
 * <p>
 * This binds a {@link ModularGuiContainerMenu} to a {@link PolyBlockEntity}, provides
 * the {@link DataManagerContainer} bridge used by PolyLib's data manager packets, and
 * handles the standard open, close, and access-distance checks for the backing tile.
 * <p>
 * Created by brandon3055 on 19/02/2024
 *
 * @param <T> the concrete block entity type this menu is attached to
 */
public abstract class PolyBlockContainerMenu<T extends PolyBlockEntity> extends ModularGuiContainerMenu implements DataManagerContainer {

    /**
     * The block entity backing this menu.
     */
    public T tile;

    /**
     * The player whose inventory opened this menu.
     */
    public Player player;

    /**
     * Constructs the client-side menu instance from the block position written to the
     * menu's extra data buffer.
     * <p>
     * The buffer is expected to contain the backing block entity's {@link net.minecraft.core.BlockPos}.
     *
     * @param type      the registered menu type, or {@code null} when appropriate for the current loader path
     * @param windowId  the vanilla container id for this menu instance
     * @param playerInv the inventory of the player opening the menu
     * @param extraData the client menu payload containing the backing block position
     */
    public PolyBlockContainerMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        super(type, windowId, playerInv);
        this.player = playerInv.player;
        this.tile = getClientTile(playerInv, extraData);
    }

    /**
     * Constructs the server-side menu instance for the supplied block entity.
     * <p>
     * This records the opening player on the tile via {@link PolyBlockEntity#onPlayerOpenContainer(Player)}
     * so {@link PolyBlockEntity#getAccessingPlayers()} can report active viewers.
     *
     * @param type      the registered menu type, or {@code null} when appropriate for the current loader path
     * @param windowId  the vanilla container id for this menu instance
     * @param playerInv the inventory of the player opening the menu
     * @param tile      the block entity backing this menu
     */
    public PolyBlockContainerMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInv, T tile) {
        super(type, windowId, playerInv);
        this.player = playerInv.player;
        this.tile = tile;
        this.tile.onPlayerOpenContainer(playerInv.player);
    }

    /**
     * Reads the backing block entity for a client-side menu from the opening payload.
     *
     * @param playerInv the inventory of the player opening the menu
     * @param extraData the client menu payload containing the backing block position
     * @param <T>       the expected concrete block entity type
     * @return the block entity at the position read from {@code extraData}
     */
    protected static <T extends PolyBlockEntity> T getClientTile(Inventory playerInv, FriendlyByteBuf extraData) {
        return (T) playerInv.player.level().getBlockEntity(extraData.readBlockPos());
    }

    /**
     * Removes this player from the backing tile's active container viewer set.
     */
    @Override
    public void removed(Player player) {
        super.removed(player);
        tile.onPlayerCloseContainer(player);
    }

    /**
     * Keeps the menu open only while the original block entity is still present and
     * the player remains within the tile's configured access distance.
     *
     * @see PolyBlockEntity#getAccessDistanceSq()
     */
    @Override
    public boolean stillValid(Player player) {
        if (tile.getLevel().getBlockEntity(tile.getBlockPos()) != tile) {
            return false;
        } else {
            return player.distanceToSqr((double) tile.getBlockPos().getX() + 0.5D, (double) tile.getBlockPos().getY() + 0.5D, (double) tile.getBlockPos().getZ() + 0.5D) <= tile.getAccessDistanceSq();
        }
    }

    /**
     * Returns the block entity used as the target for {@link DataManagerContainer} packet routing.
     */
    @Override
    public T getBlockEntity() {
        return (T) tile;
    }
}
