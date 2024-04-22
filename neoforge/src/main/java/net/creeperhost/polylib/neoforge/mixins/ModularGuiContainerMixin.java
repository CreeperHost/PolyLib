package net.creeperhost.polylib.neoforge.mixins;

import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Created by brandon3055 on 08/09/2023
 */
@Mixin (ModularGuiContainer.class)
public abstract class ModularGuiContainerMixin extends AbstractContainerScreen {

    public ModularGuiContainerMixin(AbstractContainerMenu arg, Inventory arg2, Component arg3) {
        super(arg, arg2, arg3);
    }

    private ModularGuiContainer getThis() {
        return (ModularGuiContainer) (Object) this;
    }

    @Override
    protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
        if (getThis().modularGui.vanillaSlotRendering()) {
            super.renderSlotHighlight(guiGraphics, slot, mouseX, mouseY, partialTick);
        }
    }
}
