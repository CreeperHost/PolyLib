package net.creeperhost.polylib.blocks;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 19/02/2024
 */
public class PolyEntityBlock extends PolyBlock implements EntityBlock {
    public static final String POLY_TILE_DATA_TAG = "poly_tile_data";
    private Supplier<BlockEntityType<? extends PolyBlockEntity>> blockEntityType = null;
    private boolean enableTicking;

    public PolyEntityBlock(Properties properties) {
        super(properties);
    }

    public PolyEntityBlock setBlockEntity(Supplier<BlockEntityType<? extends PolyBlockEntity>> blockEntityType, boolean enableTicking) {
        this.blockEntityType = blockEntityType;
        this.enableTicking = enableTicking;
        return this;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return blockEntityType.get().create(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        if (enableTicking && blockEntityType.get() == entityType) {
            return (e, e2, e3, tile) -> ((PolyBlockEntity) tile).tick();
        }
        return null;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (blockAccess.getBlockEntity(pos) instanceof RedstoneEmitter emitter) {
            return emitter.getWeakPower(state, side);
        }
        return super.getSignal(state, blockAccess, pos, side);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (blockAccess.getBlockEntity(pos) instanceof RedstoneEmitter emitter) {
            return emitter.getStrongPower(state, side);
        }
        return super.getDirectSignal(state, blockAccess, pos, side);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (level.getBlockEntity(pos) instanceof ChangeListener listener) {
            listener.onNeighborChange(blockIn, fromPos, isMoving);
        }
        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult)
    {
        if (level.getBlockEntity(pos) instanceof InteractableBlock interactable) {
            return interactable.onBlockUse(blockState, level, pos, player, blockHitResult);
        }
        return super.useWithoutItem(blockState, level, pos, player, blockHitResult);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof InteractableBlock interactable) {
            interactable.onBlockAttack(player);
        }
        super.attack(state, level, pos, player);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof DataRetainingBlock retaining) {
            if (stack.hasTag() && stack.getTag().contains(POLY_TILE_DATA_TAG)) {
                retaining.readFromItemStack(stack.getTagElement(POLY_TILE_DATA_TAG));
            }
        }

        if (blockEntity instanceof PolyBlockEntity polyBlock && stack.hasCustomHoverName()) {
            polyBlock.setCustomName(stack.getHoverName());
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof DataRetainingBlock retaining && retaining.saveToItem() && (level instanceof ServerLevel || !isCTRLKeyDown())) {
            CompoundTag nbt = new CompoundTag();
            ((DataRetainingBlock) blockEntity).writeToItemStack(nbt, false);
            if (!nbt.isEmpty()) {
                stack.getOrCreateTag().put(POLY_TILE_DATA_TAG, nbt);
            }
        }

        if (blockEntity instanceof Nameable && ((Nameable) blockEntity).hasCustomName()) {
            stack.setHoverName(((Nameable) blockEntity).getName());
        }

        return stack;
    }

    private boolean isCTRLKeyDown() {
        return Screen.hasControlDown();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack heldStack) {
        ItemStack stack = ItemStack.EMPTY;

        if (blockEntity instanceof DataRetainingBlock retaining && retaining.saveToItem()) {
            CompoundTag nbt = new CompoundTag();
            retaining.writeToItemStack(nbt, true);
            if (!nbt.isEmpty()) {
                stack = new ItemStack(this, 1);
                stack.getOrCreateTag().put(POLY_TILE_DATA_TAG, nbt);
            }
        }

        if (blockEntity instanceof Nameable nameable && nameable.hasCustomName()) {
            if (stack.isEmpty()) stack = new ItemStack(this, 1);
            stack.setHoverName(nameable.getName());
        }

        if (!stack.isEmpty()) {
            player.awardStat(Stats.BLOCK_MINED.get(this));
            player.causeFoodExhaustion(0.005F);

            popResource(level, pos, stack);
            //Remove tile to make sure no one else can mess with it and dupe its contents.
            level.removeBlockEntity(pos);
        } else {
            super.playerDestroy(level, player, pos, state, blockEntity, heldStack);
        }
    }
}
