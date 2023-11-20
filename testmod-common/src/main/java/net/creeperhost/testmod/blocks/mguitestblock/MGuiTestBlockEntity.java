package net.creeperhost.testmod.blocks.mguitestblock;

import dev.architectury.fluid.FluidStack;
import net.creeperhost.polylib.inventory.item.SimpleItemInventory;
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

public class MGuiTestBlockEntity extends BlockEntity implements MenuProvider {
    private static Random randy = new Random();
    public int progress = 0;
    public final SimpleItemInventory inventory = new SimpleItemInventory(this, 3);
    public final SimpleItemInventory outputInv = new SimpleItemInventory(this, 3 * 16);

    public int tankCapacity = 16000;
    public int energy = 0;
    public int maxEnergy = 32000;
    public FluidStack lavaStorage = FluidStack.create(Fluids.LAVA, 0);
    public FluidStack waterStorage = FluidStack.create(Fluids.WATER, 0);

    public MGuiTestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TestBlocks.MGUI_TEST_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public void tick() {
        if (level != null && !level.isClientSide) {
            if (progress == 0) {
                if (lavaStorage.isEmpty()) {
                    lavaStorage = FluidStack.create(Fluids.LAVA, 1000 + (randy.nextInt(tankCapacity/1000) * 1000));
                }
                if (waterStorage.isEmpty()) {
                    waterStorage = FluidStack.create(Fluids.WATER, 1000 + (randy.nextInt(tankCapacity/1000) * 1000));
                }

                if (energy < 1000) {
                    energy += 1000 + randy.nextInt(maxEnergy - 2000);
                }
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
                energy -= 426;
            }
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
