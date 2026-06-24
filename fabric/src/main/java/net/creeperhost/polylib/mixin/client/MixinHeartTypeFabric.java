package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.event.events.client.PolyGuiEvents;
import net.creeperhost.polylib.event.events.client.PolyHeartType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.Hud$HeartType")
public class MixinHeartTypeFabric {

    @Inject(method = "forPlayer", at = @At("RETURN"), cancellable = true)
    private static void onForPlayer(Player player, CallbackInfoReturnable<Enum<?>> cir) {
        Enum<?> originalType = cir.getReturnValue();
        PolyHeartType polyType = switch (originalType.name()) {
            case "POISIONED" -> PolyHeartType.POISONED;
            case "WITHERED" -> PolyHeartType.WITHERED;
            case "ABSORBING" -> PolyHeartType.ABSORBING;
            case "FROZEN" -> PolyHeartType.FROZEN;
            case "CONTAINER" -> PolyHeartType.CONTAINER;
            default -> PolyHeartType.NORMAL;
        };

        PolyHeartType[] result = new PolyHeartType[] { polyType };
        PolyGuiEvents.PLAYER_HEART_TYPE.invoker().onPlayerHeartType(player, result);

        if (result[0] != polyType) {
            Enum<?> newType = switch (result[0]) {
                case POISONED -> enumValue(originalType, "POISIONED");
                case WITHERED -> enumValue(originalType, "WITHERED");
                case ABSORBING -> enumValue(originalType, "ABSORBING");
                case FROZEN -> enumValue(originalType, "FROZEN");
                case CONTAINER -> enumValue(originalType, "CONTAINER");
                default -> enumValue(originalType, "NORMAL");
            };
            cir.setReturnValue(newType);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Enum<?> enumValue(Enum<?> source, String name) {
        return Enum.valueOf((Class) source.getDeclaringClass(), name);
    }
}
