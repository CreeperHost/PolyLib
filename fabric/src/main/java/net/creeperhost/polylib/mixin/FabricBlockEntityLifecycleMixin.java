package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyBlockEntityEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyBlockEntityEvents#BLOCK_ENTITY_LOAD} and
 * {@link PolyBlockEntityEvents#BLOCK_ENTITY_UNLOAD}.
 */
@Mixin(Level.class)
public abstract class FabricBlockEntityLifecycleMixin
{
//    @Inject(method = "addBlockEntity", at = @At("RETURN"))
//    private void polylib$onAddBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir)
//    {
//        if (cir.getReturnValue())
//        {
//            PolyBlockEntityEvents.BLOCK_ENTITY_LOAD.invoker().onBlockEntityLoad(blockEntity);
//        }
//    }

    @Inject(method = "removeBlockEntity", at = @At("HEAD"))
    private void polylib$onRemoveBlockEntity(net.minecraft.core.BlockPos pos, CallbackInfo ci)
    {
        Level self = (Level)(Object) this;
        BlockEntity be = self.getBlockEntity(pos);
        if (be != null)
        {
            PolyBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onBlockEntityUnload(be);
        }
    }
}
