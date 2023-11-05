package net.creeperhost.polylib;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class PolyLibClient
{
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