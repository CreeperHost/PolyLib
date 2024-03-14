package net.creeperhost.polylib.containers;

import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.data.DataManagerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Created by brandon3055 on 19/02/2024
 */
public abstract class PolyBlockContainerMenu<T extends PolyBlockEntity> extends ModularGuiContainerMenu implements DataManagerContainer {

    public T tile;
    public Player player;

    public PolyBlockContainerMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        super(type, windowId, playerInv);
        this.player = playerInv.player;
        this.tile = getClientTile(playerInv, extraData);
    }

    public PolyBlockContainerMenu(@Nullable MenuType<?> type, int windowId, Inventory playerInv, T tile) {
        super(type, windowId, playerInv);
        this.player = playerInv.player;
        this.tile = tile;
        this.tile.onPlayerOpenContainer(playerInv.player);
    }

    protected static <T extends PolyBlockEntity> T getClientTile(Inventory playerInv, FriendlyByteBuf extraData) {
        return (T) playerInv.player.level().getBlockEntity(extraData.readBlockPos());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        tile.onPlayerCloseContainer(player);
    }

    @Override
    public boolean stillValid(Player player) {
        if (tile.getLevel().getBlockEntity(tile.getBlockPos()) != tile) {
            return false;
        } else {
            return player.distanceToSqr((double) tile.getBlockPos().getX() + 0.5D, (double) tile.getBlockPos().getY() + 0.5D, (double) tile.getBlockPos().getZ() + 0.5D) <= tile.getAccessDistanceSq();
        }
    }

    @Override
    public T getBlockEntity() {
        return (T) tile;
    }
}
