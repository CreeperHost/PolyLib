package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyBlockEvents#NOTE_BLOCK_PLAY}.
 * Fires when a note block is triggered (triggerEvent type 0 = play note).
 */
@Mixin(NoteBlock.class)
public abstract class FabricNoteBlockMixin
{
    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void polylib$onTriggerEvent(BlockState state, Level level, BlockPos pos, int type, int data, CallbackInfoReturnable<Boolean> cir)
    {
        if (type == 0) // 0 = play note trigger
        {
            CancelContext ctx = new CancelContext();
            PolyBlockEvents.NOTE_BLOCK_PLAY.invoker().onNoteBlockPlay(level, pos, state, ctx);
            if (ctx.isCancelled())
            {
                cir.setReturnValue(false);
            }
        }
    }
}
