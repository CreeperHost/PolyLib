package net.creeperhost.testmod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugStick extends Item
{
    public DebugStick(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext)
    {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        if(level.getBlockEntity(blockPos) != null)
        {

        }
        return super.useOn(useOnContext);
    }
}
