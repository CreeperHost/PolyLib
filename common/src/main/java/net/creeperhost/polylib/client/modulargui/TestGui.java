package net.creeperhost.polylib.client.modulargui;

import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiBuilder;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.sprite.GuiTextures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 19/08/2023
 */
public class TestGui implements GuiBuilder {

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        return new GuiTexture(gui, GuiTextures.get("dynamic/gui_vanilla")).dynamicTexture();
    }

    @Override
    public void buildGui(ModularGui gui) {
        gui.initStandardGui(200, 200);
        gui.setGuiTitle(Component.literal("Hello World!"));


//        Next lets get hover text working.
//        GuiText, Chat component hover!
//                Scroll element


        GuiElement<?> root = gui.getRoot();


        GuiElement<?> scrollBackground = new GuiRectangle(root)
                .shadedRect(0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b);
        Constraints.bindTo(scrollBackground, root, 10);

        GuiScrollingBase<?> scroll = new GuiScrollingBase(scrollBackground) {
            @Override
            public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {

                if (Screen.hasShiftDown())  {
                    horizontalScrollState().setPos(horizontalScrollState().getPos() + scroll * -0.05);
                } else {
                    verticalScrollState().setPos(verticalScrollState().getPos() + scroll * -0.05);
                }

                return super.mouseScrolled(mouseX, mouseY, scroll);
            }
        };
        Constraints.bindTo(scroll, scrollBackground, 2);
        GuiElement<?> content = scroll.getContentElement();

        GuiButton lastButton = null;
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            Constraint topConstraint = lastButton == null ? Constraint.match(content.get(TOP)) : Constraint.relative(lastButton.get(BOTTOM), 2);

            lastButton = GuiButton.vanillaAnimated(content, Component.literal("Scrolling s;fgjsdlkfgl sdljfh sdjklhf sdhf ldshlf hdsh fsdgf sdfgsd sdf Button " + i), () -> System.out.println("Click " + finalI))
                    .constrain(TOP, topConstraint)
                    .constrain(LEFT, Constraint.match(content.get(LEFT)))
                    .constrain(WIDTH, Constraint.literal(scroll.getValue(WIDTH) * 2))
                    .constrain(HEIGHT, Constraint.literal(20));

            lastButton.getLabel().setRotation(() -> 0D);
        }

//        GuiButton button1 = GuiButton.vanilla(root, Component.literal("Test Button"), () -> System.out.println("Click 1!"))
//                .constrain(WIDTH, literal(100))
//                .constrain(HEIGHT, literal(20))
//                .constrain(TOP, relative(root.get(TOP), 10))
//                .constrain(LEFT, relative(root.get(LEFT), 10));
//
//        button1.setTooltipSingle(Component.literal("This is a test tool tip!"));

//        button1.getLabel()
//                .setRotation(() -> (System.currentTimeMillis() % 10000) / 100D);

//        GuiButton button2 = GuiButton.vanillaAnimated(root, Component.literal("Test Button with word wrap enabled"), () -> System.out.println("Click 2!"))
//                .constrain(WIDTH, literal(100))
//                .constrain(HEIGHT, literal(20))
//                .constrain(TOP, relative(button1.get(BOTTOM), 5))
//                .constrain(LEFT, relative(root.get(LEFT), 10));
//        button2.getLabel()
//                .setWrap(true);
////                .setRotation(() -> (System.currentTimeMillis() % 10000) / 100D);
//
//        button2.setTooltip(Component.literal("Tool tip line one"),
//                Component.literal("Tool tip line two").withStyle(ChatFormatting.RED),
//                Component.literal("Tool tip line three").withStyle(ChatFormatting.UNDERLINE),
//                Component.literal("this is a very long tool tip line, testing one two three four five six seven bla bla bla bla testing 1 2 3 testing 1 2 3.").withStyle(ChatFormatting.AQUA ));
//
//        GuiButton button3 = GuiButton.vanilla(root, Component.literal("Test Button with text trim enabled"), () -> System.out.println("Click 3!"))
//                .constrain(WIDTH, literal(100))
//                .constrain(HEIGHT, literal(20))
//                .constrain(TOP, relative(button2.get(BOTTOM), 5))
//                .constrain(LEFT, relative(root.get(LEFT), 10));
//        button3.getLabel()
//                .setTrim(true);
////                .setRotation(() -> (System.currentTimeMillis() % 10000) / 100D);
//
//        GuiButton button4 = GuiButton.vanilla(root, Component.literal("Test button with text scrolling enabled"), () -> System.out.println("Click 4!"))
//                .constrain(WIDTH, literal(100))
//                .constrain(HEIGHT, literal(20))
//                .constrain(TOP, relative(button3.get(BOTTOM), 5))
//                .constrain(LEFT, relative(root.get(LEFT), 10));
//        button4.getLabel()
//                .setRotation(() -> (System.currentTimeMillis() % 10000) / 100D);





//        Constraints.bindTo();

//        test.constrain(TOP, Constraint.match(root.get(TOP)));
//        test.constrain(LEFT, Constraint.match(root.get(LEFT)));
//        test.constrain(WIDTH, Constraint.literal(100));
//        test.constrain(HEIGHT, Constraint.literal(10));


    }

    //Probably put this in GuiRender?

}
