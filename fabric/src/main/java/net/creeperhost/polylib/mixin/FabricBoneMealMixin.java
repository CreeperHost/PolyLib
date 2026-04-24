package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyPlayerEvents#BONE_MEAL}.
 */
@Mixin(BoneMealItem.class)
public abstract class FabricBoneMealMixin
{
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void polylib$onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir)
    {
        if (!(context.getPlayer() instanceof ServerPlayer player)) return;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.BONE_MEAL.invoker().onBoneMeal(player, level, pos, state, ctx);
        if (ctx.isCancelled()) cir.setReturnValue(InteractionResult.FAIL);
    }
}
