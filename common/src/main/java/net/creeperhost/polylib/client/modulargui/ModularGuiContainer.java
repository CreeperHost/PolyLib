package net.creeperhost.polylib.client.modulargui;

import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerGuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.container.ContainerScreenAccess;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Container screen implementation for {@link ModularGui}.
 * <p>
 * Created by brandon3055 on 08/09/2023
 */
public class ModularGuiContainer<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements ContainerScreenAccess<T> {

    public final ModularGui modularGui;
    /**
     * Flag used to disable vanilla slot highlight rendering.
     */
    private boolean renderingSlots = false;

    public ModularGuiContainer(T containerMenu, Inventory inventory, ContainerGuiProvider<T> provider) {
        super(containerMenu, inventory, Component.empty());
        provider.setMenuAccess(this);
        this.modularGui = new ModularGui(provider);
        this.modularGui.setScreen(this);
        addRenderableOnly(this::renderModularGui);
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        addRenderableOnly(this::renderModularGui);
    }

    public ModularGui getModularGui() {
        return modularGui;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return modularGui.getGuiTitle();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return modularGui.closeOnEscape();
    }

    @Override
    protected void init() {
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        GuiElement<?> root = modularGui.getRoot();
        topPos = (int) root.getValue(GeoParam.TOP);
        leftPos = (int) root.getValue(GeoParam.LEFT);
        imageWidth = (int) root.getValue(GeoParam.WIDTH);
        imageHeight = (int) root.getValue(GeoParam.HEIGHT);

        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        GuiRender render = new GuiRender(graphics);
        if (!handleFloatingItemRender(render, mouseX, mouseY) && !renderHoveredStackToolTip(render, mouseX, mouseY)) {
            modularGui.renderOverlay(render, partialTicks);
        }
    }

    private void renderModularGui(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        modularGui.render(new GuiRender(graphics), partialTicks);
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (modularGui.renderBackground()) {
            super.extractBackground(guiGraphics, i, j, f);
        }
    }

    protected boolean handleFloatingItemRender(GuiRender render, int mouseX, int mouseY) {
        if (modularGui.vanillaSlotRendering()) return false;
        boolean ret = false;

        ItemStack stack = draggingItem.isEmpty() ? menu.getCarried() : draggingItem;
        if (!stack.isEmpty()) {
            int yOffset = draggingItem.isEmpty() ? 8 : 16;
            String countOverride = null;
            if (!draggingItem.isEmpty() && isSplittingStack) {
                stack = stack.copyWithCount(Mth.ceil((float) stack.getCount() / 2.0F));
            } else if (isQuickCrafting && quickCraftSlots.size() > 1) {
                stack = stack.copyWithCount(this.quickCraftingRemainder);
                if (stack.isEmpty()) {
                    countOverride = ChatFormatting.YELLOW + "0";
                }
            }
            renderFloatingItem(render, stack, mouseX - 8, mouseY - yOffset, countOverride);
            ret = true;
        }

        if (snapbackData != null) {
            float f = Mth.clamp((float)(Util.getMillis() - snapbackData.time()) / 100.0F, 0.0F, 1.0F);
            int i = snapbackData.end().x - snapbackData.start().x;
            int j = snapbackData.end().y - snapbackData.start().y;
            int k = snapbackData.start().x + (int)((float)i * f);
            int l = snapbackData.start().y + (int)((float)j * f);
            render.graphics().nextStratum();
            //TODO
//            renderFloatingItem(render.graphics(), snapbackData.item(), k, l, (String)null);
            ret = true;
            if (f >= 1.0F) {
                snapbackData = null;
            }
        }

        return ret;
    }

    protected boolean renderHoveredStackToolTip(GuiRender guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            GuiElement<?> handler = modularGui.getSlotHandler(hoveredSlot);
            if (handler != null && (handler.blockMouseOver(handler, mouseX, mouseY) || !handler.isMouseOver())) {
                return false;
            }
            ItemStack itemStack = this.hoveredSlot.getItem();
            guiGraphics.toolTipWithImage(this.getTooltipFromContainerItem(itemStack), itemStack.getTooltipImage(), itemStack, mouseX, mouseY, 0xf0100010, 0xf0100010, 0x505000ff, 0x5028007f);
            return true;
        }
        return false;
    }

    @Override
    protected void containerTick() {
        modularGui.tick();
    }

    @Override
    public void removed() {
        super.removed();
        modularGui.onGuiClose();
    }

    //=== Input Pass-though ===//

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        modularGui.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        return modularGui.mouseClicked(mouseButtonEvent, bl) || super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        return modularGui.mouseReleased(mouseButtonEvent) || super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return modularGui.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return modularGui.keyPressed(keyEvent) || super.keyPressed(keyEvent);
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return modularGui.keyReleased(keyEvent) || super.keyReleased(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return modularGui.charTyped(characterEvent) || super.charTyped(characterEvent);
    }

    //=== AbstractContainerMenu Overrides ===//

    //TODO
//    @Override
//    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
//    }
//
//    @Override
//    public void renderSlot(GuiGraphics guiGraphics, Slot slot, int i, int j) {
//        if (modularGui.vanillaSlotRendering()) {
//            super.renderSlot(guiGraphics, slot, i, j);
//        } else {
//            renderingSlots = true;
//        }
//    }

    // TODO: Look into this - extractSlot was removed during the MC 26.1.2 compilation fix.
    // It should be re-evaluated: either properly ported to the new GuiGraphics API or replaced with an equivalent override.
    @Override
    public void extractSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int i, int j) {
        if (modularGui.vanillaSlotRendering()) {
            super.extractSlot(guiGraphics, slot, i, j);
        } else {
            renderingSlots = true;
        }
    }

    //Modular gui friendly version of the slot render
    @Override
    public void renderSlot(GuiRender render, Slot slot) {
        if (modularGui.vanillaSlotRendering()) return;
        int slotX = slot.x + leftPos;
        int slotY = slot.y + topPos;
        ItemStack slotStack = slot.getItem();
        boolean dragingToSlot = false;
        boolean dontRenderItem = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;

        ItemStack carriedStack = this.menu.getCarried();
        String countString = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !slotStack.isEmpty()) {
            slotStack = slotStack.copyWithCount(slotStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !carriedStack.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (AbstractContainerMenu.canItemQuickReplace(slot, carriedStack, true) && this.menu.canDragTo(slot)) {
                dragingToSlot = true;
                int k = Math.min(carriedStack.getMaxStackSize(), slot.getMaxStackSize(carriedStack));
                int l = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int m = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots.size(), this.quickCraftingType, carriedStack) + l;
                if (m > k) {
                    m = k;
                    countString = ChatFormatting.YELLOW.toString() + k;
                }

                slotStack = carriedStack.copyWithCount(m);
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }

        if (!dontRenderItem) {
            if (dragingToSlot) {
                //Highlights slots when doing a drag place operation.
                render.fill(slotX, slotY, slotX + 16, slotY + 16, 0x80ffffff);
            }
            render.renderItem(slotStack, slotX, slotY, 16, slot.x + (slot.y * this.imageWidth)); //TODO May want a random that does not change if the slot is moved.
            render.renderItemDecorations(slotStack, slotX, slotY, countString);
        }
    }

    //TODO
//    @Override
//    public boolean isHovering(Slot pSlot, double pMouseX, double pMouseY) {
//        boolean ret = super.isHovering(pSlot, pMouseX, pMouseY);
//        //Override the isHovering check before renderSlotHighlight is called.
//        if (ret && renderingSlots && pSlot.isActive()) {
//            //This breaks the default hoveredSlot assignment, so we need to handle that here.
//            hoveredSlot = pSlot;
//            return false;
//        }
//        return ret;
//    }

    @Override //Disable vanilla title and inventory name rendering
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int i, int j) {

    }

    @Override
    public void extractCarriedItem(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (modularGui.vanillaSlotRendering()) super.extractCarriedItem(graphics, mouseX, mouseY);
    }


    public void renderFloatingItem(GuiRender render, ItemStack itemStack, int x, int y, String string) {
        render.renderItem(itemStack, x, y);
        render.renderItemDecorations(itemStack, x, y - (this.draggingItem.isEmpty() ? 0 : 8), string);
    }

    @Override
    protected void slotClicked(Slot slot, int i, int j, ContainerInput clickType) {
        if (slot != null) {
            GuiElement<?> handler = modularGui.getSlotHandler(slot);
            if (handler != null && !handler.isEnabled()) return;
        }
        super.slotClicked(slot, i, j, clickType);
    }

    //TODO
//    @Override
//    public void renderSlotHighlightBack(GuiGraphics guiGraphics) {
//        if (modularGui.vanillaSlotRendering()) {
//            super.renderSlotHighlightBack(guiGraphics);
//        }
//    }
//
//    @Override
//    public void renderSlotHighlightFront(GuiGraphics guiGraphics) {
//        if (modularGui.vanillaSlotRendering()) {
//            super.renderSlotHighlightFront(guiGraphics);
//        }
//        renderingSlots = false;
//    }
}
