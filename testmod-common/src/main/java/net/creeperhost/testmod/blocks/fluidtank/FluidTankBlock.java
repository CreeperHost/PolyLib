package net.creeperhost.testmod.blocks.fluidtank;

import net.creeperhost.polylib.blocks.BlockFacing;
import net.creeperhost.polylib.inventory.fluid.FluidManager;
import net.creeperhost.polylib.inventory.fluid.IPolyFluidStorage;
import net.creeperhost.polylib.inventory.fluid.PolyFluidStack;
import net.creeperhost.polylib.platform.Services;
import net.creeperhost.testmod.TestModCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Test block that exposes a simple PolyLib fluid tank block entity.
 */
public class FluidTankBlock extends BlockFacing {
    /**
     * Creates a fluid tank block using the supplied block properties.
     *
     * @param properties block construction properties supplied by the registry
     */
    public FluidTankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!isFluidBucket(stack)) {
            return openTankMenu(level, pos, player);
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        IPolyFluidStorage storage = FluidManager.getHandler(level.getBlockEntity(pos), hitResult.getDirection());
        if (storage == null) {
            return InteractionResult.PASS;
        }

        if (stack.is(Items.BUCKET)) {
            PolyFluidStack drained = storage.drain(FluidManager.BUCKET, false);
            Item bucket = drained.isEmpty() ? Items.AIR : drained.getFluid().getBucket();
            if (!drained.isEmpty() && bucket != Items.AIR) {
                replaceHeldBucket(player, hand, stack, new ItemStack(bucket));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }

        Fluid bucketFluid = getFluidForBucket(stack.getItem());
        PolyFluidStack resource = new PolyFluidStack(bucketFluid, FluidManager.BUCKET);
        if (storage.fill(resource, true) == FluidManager.BUCKET) {
            storage.fill(resource, false);
            replaceHeldBucket(player, hand, stack, new ItemStack(Items.BUCKET));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    /**
     * Creates the backing block entity that stores the tank's fluid contents.
     *
     * @param blockPos block position
     * @param blockState block state at the position
     * @return the new fluid tank block entity
     */
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidTankBlockEntity(blockPos, blockState);
    }

    /**
     * Logs the current tank contents when a player shift-interacts with an empty hand.
     *
     * @param state current block state
     * @param level level containing the block
     * @param pos block position
     * @param player interacting player
     * @param hitResult hit context for the interaction
     * @return success once the interaction has been handled
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player.isShiftKeyDown()) {
            IPolyFluidStorage storage = FluidManager.getHandler(level.getBlockEntity(pos), hitResult.getDirection());
            if (storage != null) {
                PolyFluidStack fluid = storage.getFluid();
                String fluidId = fluid.isEmpty() ? "empty" : BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString();
                TestModCommon.LOGGER.info("Fluid tank contains {} droplets of {} / {}", fluid.getAmount(), fluidId, storage.getCapacity());
            }
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide()) {
            return openTankMenu(level, pos, player);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult openTankMenu(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof MenuProvider menuProvider) {
                Services.REGISTER_HELPER.openMenu((ServerPlayer) player, menuProvider, buf -> buf.writeBlockPos(pos));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private boolean isFluidBucket(ItemStack stack) {
        if (stack.is(Items.BUCKET)) {
            return true;
        }
        return getFluidForBucket(stack.getItem()) != Fluids.EMPTY;
    }

    private Fluid getFluidForBucket(Item item) {
        return BuiltInRegistries.FLUID.stream()
                .filter(fluid -> fluid != Fluids.EMPTY && fluid.getBucket() == item)
                .findFirst()
                .orElse(Fluids.EMPTY);
    }

    private void replaceHeldBucket(Player player, InteractionHand hand, ItemStack heldStack, ItemStack replacement) {
        if (player.getAbilities().instabuild) {
            return;
        }
        if (heldStack.getCount() == 1) {
            player.setItemInHand(hand, replacement);
            return;
        }
        heldStack.shrink(1);
        if (!player.getInventory().add(replacement)) {
            player.drop(replacement, false);
        }
    }
}
