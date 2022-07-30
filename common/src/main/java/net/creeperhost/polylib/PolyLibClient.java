package net.creeperhost.polylib;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.client.screen.ScreenHooks;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.client.screen.screencreator.ScreenCreationSetup;
import net.creeperhost.polylib.helpers.ItemTagHelper;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

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
                    ItemTagHelper.getAllTags(stack).forEach(itemTagKey -> lines.add(Component.literal(ChatFormatting.GRAY + itemTagKey.location().toString())));
                }
            });

            ClientGuiEvent.INIT_POST.register((screen, access) ->
            {
                if(screen instanceof PauseScreen)
                {
                    ScreenHooks.addRenderableWidget(screen, new Button(10, 10, 20, 20, Component.literal("P"),
                            button -> Minecraft.getInstance().setScreen(new ScreenCreationSetup())));
                }
            });
        }
    }
}
