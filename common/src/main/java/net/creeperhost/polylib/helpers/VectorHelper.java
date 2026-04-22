package net.creeperhost.polylib.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class VectorHelper
{
    public static BlockHitResult getLookingAt(Player player, int range)
    {
        return getLookingAt(player, ClipContext.Fluid.NONE, range);
    }

    public static BlockHitResult getLookingAt(Player player, ClipContext.Fluid rayTraceFluid, int range)
    {
        Level world = player.level();

        Vec3 look = player.getLookAngle();
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 end = new Vec3(player.getX() + look.x * (double) range,
                player.getY() + player.getEyeHeight() + look.y * (double) range,
                player.getZ() + look.z * (double) range);

        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, player);

        return world.clip(context);
    }
}
