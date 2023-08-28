package net.creeperhost.polylib.client.modulargui.elements;

import com.google.common.base.Suppliers;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.GuiTextures;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 28/08/2023
 */
public class GuiButton extends GuiElement<GuiButton> {
    public static final int LEFT_CLICK = 0;
    public static final int RIGHT_CLICK = 1;
    public static final int MIDDLE_CLICK = 2;

    private final Map<Integer, Runnable> onClick = new HashMap<>();
    private final Map<Integer, Runnable> onPress = new HashMap<>();
    private boolean pressed = false;
    private Holder<SoundEvent> pressSound = SoundEvents.UI_BUTTON_CLICK;
    private Holder<SoundEvent> releaseSound = null;
    private Supplier<Boolean> disabled = Suppliers.memoize(() -> false);
    private Supplier<Boolean> toggleState;

    /**
     * In its default state this is a blank, invisible element that can fire callbacks when pressed.
     * To make an actual usable button, ether use one of the builtin static create methods,
     * Or add your own elements to make this button look and function in a way that meets your needs.
     *
     * @param parent parent {@link GuiParent}.
     */
    public GuiButton(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    /**
     * This event is fired immediately when this button is left-clicked.
     * This is the logic used by most vanilla gui buttons.
     *
     * @see #onPress(Runnable)
     */
    public GuiButton onClick(Runnable onClick) {
        return onClick(onClick, LEFT_CLICK);
    }

    /**
     * This event is fired immediately when this button is clicked with the specified mouse button.
     * This is the logic used by most vanilla gui buttons.
     * Note: You can apply one listener per mouse button.
     *
     * @see #onPress(Runnable, int)
     */
    public GuiButton onClick(Runnable onClick, int mouseButton) {
        this.onClick.put(mouseButton, onClick);
        return this;
    }

    /**
     * This event is fired when the button is pressed and then released using the left mosue button.
     * The event is only fired if the cursor is still over the button when left click is released.
     * This is the standard logic for most buttons in the world, but not vanillas.
     * Note: You can apply one listener per mouse button.
     *
     * @see #onPress(Runnable, int)
     */
    public GuiButton onPress(Runnable onPress) {
        return onPress(onPress, LEFT_CLICK);
    }

    /**
     * This event is fired when the button is pressed and then released using the specified mouse button.
     * The event is only fired if the cursor is still over the button when left click is released.
     * This is the standard logic for most buttons in the world, but not vanillas.
     *
     * @see #onPress(Runnable, int)
     */
    public GuiButton onPress(Runnable onPress, int mouseButton) {
        this.onPress.put(mouseButton, onPress);
        return this;
    }

    /**
     * Allows set the disabled status of this button
     * Note: This is not the same as {@link #setEnabled(boolean)} the "enabled" allows you to completely disable an element.
     * This "disabled" status is specific to {@link GuiButton},
     * When disabled via this method a button is still visible but greyed out / not clickable.
     */
    public GuiButton setDisabled(boolean disabled) {
        this.disabled = Suppliers.memoize(() -> disabled);
        return this;
    }

    /**
     * Allows you to install a suppler that controls the disabled state of this button.
     * Note: This is not the same as {@link #setEnabled(Supplier)} the "enabled" allows you to completely disable an element.
     * This "disabled" status is specific to {@link GuiButton},
     * When disabled via this method a button is still visible but greyed out / not clickable.
     */
    public GuiButton setDisabled(Supplier<Boolean> disabled) {
        this.disabled = disabled;
        return this;
    }

    /**
     * Allows this button to be used as a toggle or radio button.
     * This method allows you to install a suppler that controls the current "selected / toggled" state.
     *
     * @param toggleState supplier that indicates weather or not this button should currently render as pressed/selected.
     */
    public GuiButton setToggleMode(@Nullable Supplier<Boolean> toggleState) {
        this.toggleState = toggleState;
        return this;
    }

    /**
     * @return the "disabled" status.
     * @see #setDisabled(boolean)
     * @see #setDisabled(Supplier)
     */
    public boolean isDisabled() {
        return disabled.get();
    }

    /**
     * @return true if this button is currently pressed by the user (left click held down on button)
     */
    public boolean isPressed() {
        return pressed && hoverTime() > 0;
    }

    public boolean toggleState() {
        return toggleState != null && toggleState.get();
    }

    /**
     * Sets the sound to be played when this button is pressed.
     */
    public GuiButton setPressSound(Holder<SoundEvent> pressSound) {
        this.pressSound = pressSound;
        return this;
    }

    /**
     * Sets the sound to be played when this button is released.
     */
    public GuiButton setReleaseSound(Holder<SoundEvent> releaseSound) {
        this.releaseSound = releaseSound;
        return this;
    }

    public Holder<SoundEvent> getPressSound() {
        return pressSound;
    }

    public Holder<SoundEvent> getReleaseSound() {
        return releaseSound;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || isDisabled()) return false;
        Runnable onClick = this.onClick.get(button);
        Runnable onPress = this.onPress.get(button);
        boolean consume = false;
        if (onClick != null) {
            onClick.run();
            consume = true;
        }
        if (onPress != null) {
            consume = true;
        }
        if (getPressSound() != null) {
            mc().getSoundManager().play(SimpleSoundInstance.forUI(getPressSound(), 1F));
        }
        pressed = true;
        return consume;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button, boolean consumed) {
        consumed = super.mouseReleased(mouseX, mouseY, button, consumed);
        Runnable onPress = this.onPress.get(button);
        if (!isDisabled()) {
            if (!consumed && pressed && onPress != null) {
                onPress.run();
                consumed = true;
            }
            if (getReleaseSound() != null && (toggleState == null || !toggleState.get())) {
                mc().getSoundManager().play(SimpleSoundInstance.forUI(getReleaseSound(), 1F));
            }
        }
        pressed = false;
        return consumed;
    }


    /**
     * Creates a new gui button that looks and acts exactly like a standard vanilla button.
     */
    public static GuiButton vanilla(@NotNull GuiParent<?> parent, Runnable onClick) {
        return vanilla(parent).onClick(onClick);
    }

    /**
     * Creates a new gui button that looks and acts exactly like a standard vanilla button.
     */
    public static GuiButton vanilla(@NotNull GuiParent<?> parent) {
        GuiButton button = new GuiButton(parent);
        GuiTexture texture = new GuiTexture(button, GuiTextures.getter(() -> button.toggleState() ? "dynamic/button_highlight" : "dynamic/button_vanilla"));
        texture.dynamicTexture();
        GuiRectangle highlight = new GuiRectangle(button).border(() -> button.hoverTime() > 0 ? 0xFFFFFFFF : 0);

        Constraints.bindTo(texture, button);
        Constraints.bindTo(highlight, button);

        //TODO GuiLabel

        return button;
    }

    /**
     * Creates a vanilla button with a "press" animation.
     */
    public static GuiButton vanillaAnimated(@NotNull GuiParent<?> parent, Runnable onPress) {
        return vanillaAnimated(parent).onPress(onPress);
    }

    //TODO Could use a quad-sliced texture for this.
    /**
     * Creates a vanilla button with a "press" animation.
     */
    public static GuiButton vanillaAnimated(@NotNull GuiParent<?> parent) {
        GuiButton button = new GuiButton(parent);
        GuiTexture texture = new GuiTexture(button, GuiTextures.getter(() -> button.toggleState() || button.isPressed() ? "dynamic/button_pressed" : "dynamic/button_vanilla"));
        texture.dynamicTexture();
        GuiRectangle highlight = new GuiRectangle(button).border(() -> button.hoverTime() > 0 ? 0xFFFFFFFF : 0);

        Constraints.bindTo(texture, button);
        Constraints.bindTo(highlight, button);

        //TODO GuiLabel

        return button;
    }
}
