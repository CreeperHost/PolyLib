package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyPlayerEvents#TAB_LIST_NAME_FORMAT}.
 * Fires when the tab-list display name for a player is queried.
 * Modify {@code name[0]} to change the displayed name.
 */
@Mixin(ServerPlayer.class)
public abstract class FabricTabListNameFormatMixin
{
    @Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
    private void polylib$onGetTabListDisplayName(CallbackInfoReturnable<Component> cir)
    {
        Component[] name = { cir.getReturnValue() };
        PolyPlayerEvents.TAB_LIST_NAME_FORMAT.invoker().onTabListNameFormat((ServerPlayer) (Object) this, name);
        if (name[0] != cir.getReturnValue())
        {
            cir.setReturnValue(name[0]);
        }
    }
}
