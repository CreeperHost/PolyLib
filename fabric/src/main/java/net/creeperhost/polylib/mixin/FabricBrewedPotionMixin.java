package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyPlayerEvents#BREWED_POTION}.
 * Fires at the end of a brewing operation. The player is the nearest player
 * within 8 blocks of the brewing stand; if no player is nearby, the event does not fire.
 */
@Mixin(BrewingStandBlockEntity.class)
public abstract class FabricBrewedPotionMixin
{
    @Inject(method = "doBrew", at = @At("TAIL"))
    private static void polylib$onDoBrew(Level level, BlockPos pos, NonNullList<ItemStack> items,
            CallbackInfo ci)
    {
        if (!(level instanceof ServerLevel sl)) return;
        Player player = sl.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false);
        if (player == null) return;
        // Potion output slots are 0, 1, 2
        for (int i = 0; i < 3; i++)
        {
            ItemStack result = items.get(i);
            if (!result.isEmpty())
            {
                PolyPlayerEvents.BREWED_POTION.invoker().onBrewedPotion(player, result);
            }
        }
    }
}
