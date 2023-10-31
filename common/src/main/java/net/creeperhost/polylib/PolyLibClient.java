package net.creeperhost.polylib;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.client.screen.ScreenHooks;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.client.screen.screencreator.ScreenCreationSetup;
import net.creeperhost.polylib.development.DevelopmentTools;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvent.CLIENT_PRE.register(instance -> MultiblockRegistry.tickStart(instance.level));
        if (Platform.isDevelopmentEnvironment())
        {
            DevelopmentTools.initClient();

            ClientGuiEvent.INIT_POST.register((screen, access) ->
            {
                if (screen instanceof PauseScreen)
                {
                    ScreenHooks.addRenderableWidget(screen, new Button(10, 10, 20, 20, Component.literal("P"),
                            button -> Minecraft.getInstance().setScreen(new ScreenCreationSetup())));
                }
            });
        }

    }

    @ExpectPlatform
    public static PolyLibClient.ToolTipResult postRenderTooltipPre(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font font) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static PolyLibClient.ToolTipColour postTooltipColour(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, int backgroundStart, int backgroundEnd , int borderStart, int borderEnd, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<ClientTooltipComponent> postGatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void onItemDecorate(Font font, ItemStack stack, int xOffset, int yOffset) {
        throw new AssertionError();
    }

        public interface ToolTipResult {
        int getX();
        int getY();
        @NotNull Font getFont();
        boolean canceled();
    }

    public interface ToolTipColour {
        int getBackgroundStart();
        int getBackgroundEnd();
        int getBorderStart();
        int getBorderEnd();
    }
}
