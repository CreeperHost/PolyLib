package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBrewingEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

/**
 * Fabric bridge for {@link PolyBrewingEvents#BREW_PRE} and {@link PolyBrewingEvents#BREW_POST}.
 * Injects into the private static {@code doBrew} method of {@code BrewingStandBlockEntity}.
 */
@Mixin(BrewingStandBlockEntity.class)
public abstract class FabricBrewingMixin
{
    @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
    private static void polylib$onBrewPre(Level level, BlockPos pos,
            NonNullList<ItemStack> items, CallbackInfo ci)
    {
        CancelContext ctx = new CancelContext();
        PolyBrewingEvents.BREW_PRE.invoker().onBrewPre(items, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }

    @Inject(method = "doBrew", at = @At("RETURN"))
    private static void polylib$onBrewPost(Level level, BlockPos pos,
            NonNullList<ItemStack> items, CallbackInfo ci)
    {
        PolyBrewingEvents.BREW_POST.invoker().onBrewPost(items);
    }
}
