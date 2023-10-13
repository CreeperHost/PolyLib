package net.creeperhost.polylib.fabric.mixins;

import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Created by brandon3055 on 08/09/2023
 */
@Mixin (AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    private AbstractContainerScreen getThis() {
        return (AbstractContainerScreen) (Object) this;
    }

    @Redirect (
            method = "render",
            at = @At (
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"
            )
    )
    private void redirectHighlight(GuiGraphics guiGraphics, int i, int j, int k) {
        if (!(getThis() instanceof ModularGuiContainer screen) || screen.modularGui.vanillaSlotRendering()) {
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, i, j, k);
        }
    }
}
