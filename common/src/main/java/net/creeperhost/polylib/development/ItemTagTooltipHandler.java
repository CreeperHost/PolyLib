package net.creeperhost.polylib.development;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.creeperhost.polylib.helpers.ItemTagHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.stream.Stream;

public class ItemTagTooltipHandler
{
    public static void init()
    {
        ClientTooltipEvent.ITEM.register((stack, lines, flag) ->
        {
            if(flag.isAdvanced())
            {
                Stream<TagKey<Item>> tags = ItemTagHelper.getAllTags(stack);
                if(tags != null && !tags.toList().isEmpty())
                {
                    lines.add(Component.literal(" "));
                    ItemTagHelper.getAllTags(stack).forEach(itemTagKey -> lines.add(Component.literal(ChatFormatting.GRAY + itemTagKey.location().toString())));
                }
            }
        });
    }
}
