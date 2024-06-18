package net.creeperhost.testmod.blocks.mguitestblock;

import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.blocks.PolyBlockEntity;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.PolyBlockTank;
import net.creeperhost.polylib.inventory.item.SimpleItemInventory;
import net.creeperhost.polylib.inventory.items.BlockInventory;
import net.creeperhost.polylib.inventory.power.EnergyManager;
import net.creeperhost.polylib.inventory.power.IPolyEnergyStorageItem;
import net.creeperhost.polylib.inventory.power.PolyBlockEnergyStorage;
import net.creeperhost.polylib.inventory.power.PolyEnergyStorage;
import net.creeperhost.testmod.init.TestBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class MGuiTestBlockEntity extends PolyBlockEntity implements MenuProvider {
    private static Random randy = new Random();
    public int progress = 0;
    public final BlockInventory inventory = new BlockInventory(this, 3);
    public final BlockInventory outputInv = new BlockInventory(this, 3 * 16);
    public final BlockInventory energyItemInv = new BlockInventory(this, 2)
            .setStackValidator(EnergyManager::isEnergyItem);

//    public PolyBlockTank tank = new PolyBlockTank(this, 16 * FluidManager.BUCKET); //TODO add bucket fill / drain example
    public PolyEnergyStorage energy = new PolyBlockEnergyStorage(this, 128000);

    public int tankCapacity = 16000;
    public FluidStack lavaStorage = FluidStack.create(Fluids.LAVA, 0);
    public FluidStack waterStorage = FluidStack.create(Fluids.WATER, 0);

    public MGuiTestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TestBlocks.MGUI_TEST_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void tick() {
        super.tick();
        if (level.isClientSide) return;

        if (progress == 0) {
            if (lavaStorage.isEmpty()) {
                lavaStorage = FluidStack.create(Fluids.LAVA, 1000 + (randy.nextInt(16000 / 1000) * 1000));
            }
            if (waterStorage.isEmpty()) {
                waterStorage = FluidStack.create(Fluids.WATER, 1000 + (randy.nextInt(16000 / 1000) * 1000));
            }
//
//            if (energy < 1000) {
//                energy += 1000 + randy.nextInt(maxEnergy - 2000);
//            }


        }

        progress += 10;
        if (progress >= 100) {
            progress = 0;

            ItemStack output = new ItemStack(Items.COBBLESTONE);
            for (int i = 0; i < outputInv.getContainerSize(); i++) {
                ItemStack stack = outputInv.getItem(i);
                if (stack.isEmpty()) {
                    outputInv.setItem(i, output);
                } else if (ItemStack.isSameItem(stack, output) && stack.getCount() < stack.getMaxStackSize()) {
                    stack.grow(1);
                } else {
                    continue;
                }
                break;
            }
            lavaStorage.setAmount(lavaStorage.getAmount() - 1000);
            waterStorage.setAmount(waterStorage.getAmount() - 1000);
        }

        IPolyEnergyStorageItem chargeHandler = EnergyManager.getHandler(energyItemInv.getItem(0));
        if (chargeHandler != null && EnergyManager.transferEnergy(energy, chargeHandler) > 0) {
            energyItemInv.setItem(0, chargeHandler.getContainer());
        }

        IPolyEnergyStorageItem dischargeHandler = EnergyManager.getHandler(energyItemInv.getItem(1));
        if (dischargeHandler != null && EnergyManager.transferEnergy(dischargeHandler, energy) > 0) {
            energyItemInv.setItem(1, dischargeHandler.getContainer());
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("MGui Test Block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new MGuiTestBlockContainerMenu(id, inventory, this);
    }
}
