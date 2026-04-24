package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyGuiEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.HeartType.class)
public class MixinHeartTypeFabric {

    @Inject(method = "forPlayer", at = @At("RETURN"), cancellable = true)
    private static void onForPlayer(Player player, CallbackInfoReturnable<Gui.HeartType> cir) {
        Gui.HeartType originalType = cir.getReturnValue();
        
        net.creeperhost.polylib.event.events.client.PolyHeartType polyType = switch (originalType) {
            case NORMAL -> net.creeperhost.polylib.event.events.client.PolyHeartType.NORMAL;
            case POISONED -> net.creeperhost.polylib.event.events.client.PolyHeartType.POISONED;
            case WITHERED -> net.creeperhost.polylib.event.events.client.PolyHeartType.WITHERED;
            case ABSORBING -> net.creeperhost.polylib.event.events.client.PolyHeartType.ABSORBING;
            case FROZEN -> net.creeperhost.polylib.event.events.client.PolyHeartType.FROZEN;
            default -> net.creeperhost.polylib.event.events.client.PolyHeartType.NORMAL;
        };
        
        net.creeperhost.polylib.event.events.client.PolyHeartType[] result = new net.creeperhost.polylib.event.events.client.PolyHeartType[] { polyType };
        PolyGuiEvents.PLAYER_HEART_TYPE.invoker().onPlayerHeartType(player, result);
        
        if (result[0] != polyType) {
            Gui.HeartType newType = switch (result[0]) {
                case NORMAL -> Gui.HeartType.NORMAL;
                case POISONED -> Gui.HeartType.POISONED;
                case WITHERED -> Gui.HeartType.WITHERED;
                case ABSORBING -> Gui.HeartType.ABSORBING;
                case FROZEN -> Gui.HeartType.FROZEN;
                default -> Gui.HeartType.NORMAL; // Fallback
            };
            cir.setReturnValue(newType);
        }
    }
}
