package net.creeperhost.polylib.items;

import net.creeperhost.polylib.holders.LevelHolder;
import net.creeperhost.polylib.holders.PlayerHolder;
import net.creeperhost.polylib.helpers.RegistryNameHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Deprecated(forRemoval = true)
public class PolyItem extends Item
{
    public PolyItem(Properties properties)
    {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> useItem(LevelHolder levelHolder, PlayerHolder playerHolder, InteractionHand interactionHand)
    {
        return InteractionResultHolder.pass(playerHolder.getPlayer().getItemInHand(interactionHand));
    }

    @Deprecated
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand)
    {
        return useItem(new LevelHolder(level), new PlayerHolder(player), interactionHand);
    }

    public InteractionResult useItemOn(UseOnContext useOnContext)
    {
        return InteractionResult.PASS;
    }

    @Deprecated
    @Override
    public InteractionResult useOn(@NotNull UseOnContext useOnContext)
    {
        return useItemOn(useOnContext);
    }

    public Optional<ResourceLocation> getRegistryName()
    {
        return RegistryNameHelper.getRegistryName(this);
    }

    public static class PolyItemProperties extends Item.Properties
    {

    }
}
