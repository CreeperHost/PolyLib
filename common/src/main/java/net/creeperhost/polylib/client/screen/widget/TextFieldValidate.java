package net.creeperhost.polylib.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aaron on 19/05/2017.
 */
public class TextFieldValidate extends EditBox
{
    protected final Pattern pattern;

    public TextFieldValidate(Font font, int x, int y, int width, int height, String regexStr, String s)
    {
        super(font, x, y, width, height, Component.translatable(s));
        pattern = Pattern.compile(regexStr);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void insertText(String textWrite)
    {
        int prevPos = this.getCursorPosition();
        String beforeWrite = this.getValue();
        super.insertText(textWrite);
        String afterWrite = this.getValue();
        Matcher matcher = pattern.matcher(afterWrite);
        if (!matcher.matches())
        {
            this.setValue(beforeWrite);
            this.setCursorPosition(prevPos);
        }
    }

    @Override
    public void setValue(String string)
    {
        Matcher matcher = pattern.matcher(string);
        if (matcher.matches())
        {
            super.setValue(string);
        }
    }
}
