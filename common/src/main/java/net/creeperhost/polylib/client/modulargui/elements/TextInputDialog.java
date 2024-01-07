package net.creeperhost.polylib.client.modulargui.elements;

import com.mojang.blaze3d.platform.InputConstants;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 07/01/2024
 */
public class TextInputDialog extends GuiElement<TextInputDialog> {

    public GuiTextField textField;
    @Nullable
    public Consumer<String> resultCallback;

    public TextInputDialog(ModularGui gui) {
        super(gui.getRoot());
        this.setOpaque(true);
    }

    public TextInputDialog setResultCallback(Consumer<String> resultCallback) {
        this.resultCallback = resultCallback;
        return this;
    }

    public void accept() {
        if (resultCallback != null) {
            resultCallback.accept(textField.getValue());
        }
        close();
    }

    public void close(){
        getParent().removeChild(this);
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        if (key == InputConstants.KEY_ESCAPE) {
            close();
        }
        return true;
    }

    public static TextInputDialog simpleDialog(GuiElement<?> parent, Component title) {
        return simpleDialog(parent, title, "");
    }

    public static TextInputDialog simpleDialog(GuiElement<?> parent, Component title, String defaultText) {
        TextInputDialog dialog = new TextInputDialog(parent.getModularGui());
        Constraints.bind(GuiRectangle.toolTipBackground(dialog), dialog);

        GuiText titleText = new GuiText(dialog, title)
                .setWrap(true)
                .constrain(TOP, relative(dialog.get(TOP), 5))
                .constrain(LEFT, relative(dialog.get(LEFT), 5))
                .constrain(RIGHT, relative(dialog.get(RIGHT), -5))
                .autoHeight();

        GuiRectangle textBg = new GuiRectangle(dialog)
                .fill(0xA0202020)
                .constrain(TOP, relative(titleText.get(BOTTOM), 3))
                .constrain(LEFT, relative(dialog.get(LEFT), 5))
                .constrain(RIGHT, relative(dialog.get(RIGHT), -5))
                .constrain(HEIGHT, literal(14));

        dialog.textField = new GuiTextField(textBg)
                .setEnterPressed(dialog::accept);
        Constraints.bind(dialog.textField, textBg, 0, 3, 0, 3);

        GuiButton accept = GuiButton.flatColourButton(dialog, () -> Component.translatable("gui.ok"), hovered -> hovered ? 0xFF44AA44 : 0xFF118811)
                .onPress(dialog::accept)
                .constrain(TOP, relative(textBg.get(BOTTOM), 3))
                .constrain(LEFT, match(textBg.get(LEFT)))
                .constrain(RIGHT, midPoint(textBg.get(LEFT), textBg.get(RIGHT), -1))
                .constrain(HEIGHT, literal(14));

        GuiButton cancel = GuiButton.flatColourButton(dialog, () -> Component.translatable("gui.cancel"), hovered -> hovered ? 0xFFAA4444 : 0xFF881111)
                .onPress(dialog::close)
                .constrain(TOP, relative(textBg.get(BOTTOM), 3))
                .constrain(LEFT, midPoint(textBg.get(LEFT), textBg.get(RIGHT), 1))
                .constrain(RIGHT, match(textBg.get(RIGHT)))
                .constrain(HEIGHT, literal(14));

        ModularGui gui = parent.getModularGui();
        dialog.constrain(TOP, midPoint(gui.get(TOP), gui.get(BOTTOM), -20));
        dialog.constrain(LEFT, midPoint(gui.get(LEFT), gui.get(RIGHT), -100));
        dialog.constrain(BOTTOM, relative(cancel.get(BOTTOM), 5));
        dialog.constrain(WIDTH, literal(200));
        dialog.textField.setValue(defaultText);
        dialog.textField.setFocus(true);
        return dialog;
    }
}
