package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric bridge for {@link PolyBlockEvents#BLOCK_DROPS}.
 * Fires after block drops have been spawned into the world (informational).
 * <p>
 * Note: modifications to the entity list do not affect already-spawned item entities.
 * NeoForge fires this event before drops spawn, allowing modification.
 */
@Mixin(Block.class)
public abstract class FabricBlockDropsMixin
{
    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("TAIL"))
    private static void polylib$onDropResources(BlockState state, Level level, BlockPos pos,
            BlockEntity be, Entity entity, ItemStack tool, CallbackInfo ci)
    {
        if (!(level instanceof ServerLevel sl)) return;
        List<ItemStack> stacks = Block.getDrops(state, sl, pos, be, entity, tool);
        List<ItemEntity> entities = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks)
        {
            entities.add(new ItemEntity(sl, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
        }
        PolyBlockEvents.BLOCK_DROPS.invoker().onBlockDrops(sl, pos, state, entities);
    }
}
