package net.creeperhost.polylib;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.helpers.ItemTagHelper;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.stream.Stream;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvent.CLIENT_PRE.register(instance -> MultiblockRegistry.tickStart(instance.level));
        if(Platform.isDevelopmentEnvironment())
        {
            ClientTooltipEvent.ITEM.register((stack, lines, flag) ->
            {
                if(flag.isAdvanced())
                {
                    lines.add(Component.literal(ChatFormatting.DARK_PURPLE + "Tags: "));
                    ItemTagHelper.getAllTags(stack).forEach(itemTagKey ->
                    {
                        lines.add(Component.literal(ChatFormatting.GRAY + itemTagKey.location().toString()));
                    });
                }
            });
        }
    }
}
