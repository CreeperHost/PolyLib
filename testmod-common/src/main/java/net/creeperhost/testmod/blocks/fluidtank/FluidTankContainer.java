package net.creeperhost.testmod.blocks.fluidtank;

import net.creeperhost.polylib.client.modulargui.lib.container.DataSync;
import net.creeperhost.polylib.containers.PolyBlockContainerMenu;
import net.creeperhost.polylib.data.serializable.FluidData;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.creeperhost.testmod.init.TestContainers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Container menu for the test fluid tank.
 */
@SuppressWarnings("unchecked")
public class FluidTankContainer extends PolyBlockContainerMenu<FluidTankBlockEntity> {
    /**
     * Synchronized tank contents for the client-side GUI.
     */
    public final DataSync<PolyFluidStack> fluid;

    /**
     * Creates the client-side tank menu from its opening payload.
     *
     * @param windowId menu window id
     * @param playerInv player inventory
     * @param extraData payload containing the tank block position
     */
    public FluidTankContainer(int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(TestContainers.FLUID_TANK_CONTAINER.get(), windowId, playerInv, extraData);
    }

    /**
     * Creates the client-side tank menu from its opening payload.
     *
     * @param type menu type
     * @param windowId menu window id
     * @param playerInv player inventory
     * @param extraData payload containing the tank block position
     */
    public FluidTankContainer(@Nullable MenuType<?> type, int windowId, Inventory playerInv, FriendlyByteBuf extraData) {
        super(type, windowId, playerInv, extraData);
        fluid = new DataSync<>(this, new FluidData(), () -> tile.getFluidTank().getFluid());
    }

    /**
     * Creates the server-side tank menu.
     *
     * @param windowId menu window id
     * @param playerInv player inventory
     * @param tile backing tank block entity
     */
    public FluidTankContainer(int windowId, Inventory playerInv, FluidTankBlockEntity tile) {
        super(TestContainers.FLUID_TANK_CONTAINER.get(), windowId, playerInv, tile);
        fluid = new DataSync<>(this, new FluidData(), () -> tile.getFluidTank().getFluid());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }
}
