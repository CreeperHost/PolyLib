package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#PLAYER_LOAD} and {@link PolyPlayerEvents#PLAYER_SAVE}.
 */
@Mixin(ServerPlayer.class)
public abstract class FabricPlayerLoadSaveMixin
{
    //TODO
//    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
//    private void polylib$onLoad(CompoundTag tag, CallbackInfo ci)
//    {
//        ServerPlayer self = (ServerPlayer)(Object) this;
//        PolyPlayerEvents.PLAYER_LOAD.invoker().onPlayerFile(self, self.getStringUUID());
//    }
//
//    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
//    private void polylib$onSave(CompoundTag tag, CallbackInfo ci)
//    {
//        ServerPlayer self = (ServerPlayer)(Object) this;
//        PolyPlayerEvents.PLAYER_SAVE.invoker().onPlayerFile(self, self.getStringUUID());
//    }
}
