package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

/**
 * Fabric bridge for {@link PolyPlayerEvents#CONTAINER_OPEN}.
 */
@Mixin(net.minecraft.server.level.ServerPlayer.class)
public abstract class FabricContainerOpenMixin
{
    @Inject(method = "openMenu", at = @At("RETURN"))
    private void polylib$onOpenMenu(MenuProvider provider, CallbackInfoReturnable<OptionalInt> cir)
    {
        net.minecraft.server.level.ServerPlayer self = (net.minecraft.server.level.ServerPlayer)(Object) this;
        AbstractContainerMenu menu = self.containerMenu;
        if (menu != null && !cir.getReturnValue().isEmpty())
        {
            PolyPlayerEvents.CONTAINER_OPEN.invoker().onContainerOpen(self, menu);
        }
    }
}
