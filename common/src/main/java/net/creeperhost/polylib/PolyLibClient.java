package net.creeperhost.polylib;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.client.modulargui.lib.CursorHelper;
import net.creeperhost.polylib.development.DevelopmentTools;
import net.creeperhost.polylib.mulitblock.MultiblockRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class PolyLibClient
{
    public static void init()
    {
        ClientTickEvent.CLIENT_PRE.register(instance -> MultiblockRegistry.tickStart(instance.level));
        CursorHelper.init();
        ModularGuiInjector.init();
        if (Platform.isDevelopmentEnvironment())
        {
            DevelopmentTools.initClient();
        }
    }

    public static Player getClientPlayer() {
        if (PolyLibPlatform.isClientSide()) {
            return _getClientPlayer();
        }
        return null;
    }

    private static Player _getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @ExpectPlatform
    public static ToolTipResult postRenderTooltipPre(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font font, @NotNull ClientTooltipPositioner positioner) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static PolyLibClient.ToolTipColour postTooltipColour(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, int backgroundStart, int backgroundEnd , int borderStart, int borderEnd, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static List<ClientTooltipComponent> postGatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void onItemDecorate(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
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
