package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bridges {@link PolyPlayerEvents#ANIMAL_TAME} on Fabric.
 */
@Mixin(TamableAnimal.class)
public abstract class FabricAnimalTameMixin
{
    @Inject(method = "tame", at = @At("HEAD"), cancellable = true)
    private void polylib$onTame(Player player, CallbackInfo ci)
    {
        if (player instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.ANIMAL_TAME.invoker().onAnimalTame((Animal) (Object) this, sp, ctx);
            if (ctx.isCancelled()) ci.cancel();
        }
    }
}
