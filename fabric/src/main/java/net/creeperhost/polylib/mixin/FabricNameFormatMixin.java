package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyPlayerEvents#NAME_FORMAT}.
 * Fires when a player's display name is being computed.
 */
@Mixin(ServerPlayer.class)
public abstract class FabricNameFormatMixin
{
    //TODO
//    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
//    private void polylib$onGetDisplayName(CallbackInfoReturnable<Component> cir)
//    {
//        ServerPlayer self = (ServerPlayer)(Object) this;
//        Component[] name = { cir.getReturnValue() };
//        PolyPlayerEvents.NAME_FORMAT.invoker().onNameFormat(self, name);
//        cir.setReturnValue(name[0]);
//    }
}
