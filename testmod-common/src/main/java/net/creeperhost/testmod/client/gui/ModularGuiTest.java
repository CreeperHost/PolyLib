package net.creeperhost.testmod.client.gui;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.DynamicTextures;
import net.creeperhost.polylib.client.modulargui.lib.GuiBuilder;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Axis;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Direction;
import net.creeperhost.polylib.client.modulargui.sprite.PolyTextures;
import net.creeperhost.testmod.TestMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.literal;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.relative;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 19/08/2023
 */
public class ModularGuiTest implements GuiBuilder, DynamicTextures {

    private String BACKGROUND_TEXTURE;

    @Override
    public void makeTextures(Function<DynamicTexture, String> textures) {
        BACKGROUND_TEXTURE = dynamicTexture(textures, new ResourceLocation(PolyLib.MOD_ID, "textures/gui/dynamic/gui_vanilla"), new ResourceLocation(TestMod.MOD_ID, "textures/gui/test_gui"), 200, 200, 4);
    }

    @Override
    public GuiElement<?> createRootElement(ModularGui gui) {
        return new GuiTexture(gui, TestModTextures.get(BACKGROUND_TEXTURE));
    }

    @Override
    public void buildGui(ModularGui gui) {
        gui.initStandardGui(200, 200);
        gui.setGuiTitle(Component.literal("Hello World!"));

        GuiElement<?> root = gui.getRoot();

        var textField = GuiTextField.create(root, 0xFF000000, 0xFFFFFFFF, 0xE0E0E0);
        textField.container
                .constrain(LEFT, relative(root.get(LEFT), 0))
                .constrain(RIGHT, relative(root.get(RIGHT), 0))
                .constrain(TOP, relative(root.get(BOTTOM), 2))
                .constrain(HEIGHT, literal(14));
        textField.primary
                .setSuggestion(Component.literal("I suggest you type something!"))
                .setMaxLength(1000);

        new GuiElement(root) {
            @Override
            public void render(GuiRender render, double mouseX, double mouseY, float partialTicks) {
                render.drawString(render.mc().getFps() + " FPS", 5, 5, 0xFFFFFFFF, true);
            }
        }.setPos(1, 1).setSize(1, 1);


        GuiText title = new GuiText(root, gui.getGuiTitle())
                .setTextColour(0x404040)
                .setShadow(false)
                .constrain(TOP, relative(root.get(TOP), 5))
                .constrain(HEIGHT, Constraint.literal(8))
                .constrain(LEFT, relative(root.get(LEFT), 5))
                .constrain(RIGHT, relative(root.get(RIGHT), -5));

        GuiProgressIcon progressRight = new GuiProgressIcon(root)
                .setBackground(PolyTextures.get("widgets/progress_arrow_empty"))
                .setAnimated(PolyTextures.get("widgets/progress_arrow_full"))
                .setProgress(() -> (System.currentTimeMillis() % 1000) / 1000D)
                .constrain(TOP, relative(title.get(BOTTOM), 2))
                .constrain(LEFT, relative(root.get(LEFT), 5))
                .constrain(WIDTH, literal(22))
                .constrain(HEIGHT, literal(16));

        GuiProgressIcon progressDown = new GuiProgressIcon(root)
                .setBackground(PolyTextures.get("widgets/progress_arrow_empty"))
                .setAnimated(PolyTextures.get("widgets/progress_arrow_full"))
                .setDirection(Direction.DOWN)
                .setProgress(() -> (System.currentTimeMillis() % 1000) / 1000D)
                .constrain(TOP, relative(title.get(BOTTOM), 2))
                .constrain(LEFT, relative(progressRight.get(RIGHT), 5))
                .constrain(WIDTH, literal(16))
                .constrain(HEIGHT, literal(22));

        GuiProgressIcon progressLeft = new GuiProgressIcon(root)
                .setBackground(PolyTextures.get("widgets/progress_arrow_empty"))
                .setAnimated(PolyTextures.get("widgets/progress_arrow_full"))
                .setDirection(Direction.LEFT)
                .setProgress(() -> (System.currentTimeMillis() % 1000) / 1000D)
                .constrain(TOP, relative(title.get(BOTTOM), 2))
                .constrain(LEFT, relative(progressDown.get(RIGHT), 5))
                .constrain(WIDTH, literal(22))
                .constrain(HEIGHT, literal(16));

        GuiProgressIcon progressUp = new GuiProgressIcon(root)
                .setBackground(PolyTextures.get("widgets/progress_arrow_empty"))
                .setAnimated(PolyTextures.get("widgets/progress_arrow_full"))
                .setDirection(Direction.UP)
                .setProgress(() -> (System.currentTimeMillis() % 1000) / 1000D)
                .constrain(TOP, relative(title.get(BOTTOM), 2))
                .constrain(LEFT, relative(progressLeft.get(RIGHT), 5))
                .constrain(WIDTH, literal(16))
                .constrain(HEIGHT, literal(22));



        GuiElement<?> scrollBackground = new GuiRectangle(root).shadedRect(0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b)
                .constrain(TOP, relative(progressUp.get(BOTTOM), 2))
                .constrain(BOTTOM, relative(root.get(BOTTOM), -16))
                .constrain(LEFT, relative(root.get(LEFT), 5))
                .constrain(RIGHT, relative(root.get(RIGHT), -16));


        GuiScrolling scroll = new GuiScrolling(scrollBackground);
        Constraints.bindTo(scroll, scrollBackground, 1);

        var xScrollBar = GuiSlider.vanillaScrollBar(root, Axis.X);
        xScrollBar.container
                .constrain(LEFT, Constraint.match(scrollBackground.get(LEFT)))
                .constrain(RIGHT, Constraint.match(scrollBackground.get(RIGHT)))
                .constrain(TOP, relative(scrollBackground.get(BOTTOM), 1))
                .constrain(HEIGHT, Constraint.literal(10));
        xScrollBar.primary
                .setSliderState(scroll.scrollState(Axis.X))
                .setScrollableElement(scroll);

        var yScrollBar = GuiSlider.vanillaScrollBar(root, Axis.Y);
        yScrollBar.container
                .constrain(TOP, Constraint.match(scrollBackground.get(TOP)))
                .constrain(BOTTOM, Constraint.match(scrollBackground.get(BOTTOM)))
                .constrain(LEFT, relative(scrollBackground.get(RIGHT), 1))
                .constrain(WIDTH, Constraint.literal(10));
        yScrollBar.primary
                .setSliderState(scroll.scrollState(Axis.Y))
                .setScrollableElement(scroll);




//        GuiRectangle scrollBgY = new GuiRectangle(root)
//                .shadedRect(0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b)
//                .constrain(TOP, Constraint.match(scrollBackground.get(TOP)))
//                .constrain(BOTTOM, Constraint.match(scrollBackground.get(BOTTOM)))
//                .constrain(LEFT, Constraint.relative(scrollBackground.get(RIGHT), 1))
//                .constrain(WIDTH, Constraint.literal(10));
//
//        GuiSlider scrollY = new GuiSlider(root, Axis.Y)
//                .setSliderState(scroll.scrollState(Axis.Y))
//                .setScrollableElement(scroll, true);
//        Constraints.bindTo(scrollY, scrollBgY);
//
//        GuiTexture scrollHandleY = new GuiTexture(scrollY.getSlider()).dynamicTexture();
//        scrollHandleY.setMaterial(GuiTextures.getter(() -> scrollHandleY.hovered() || scrollY.isDragging() ? "dynamic/button_highlight_borderless" : "dynamic/button_borderless"));
//        Constraints.bindTo(scrollHandleY, scrollY.getSlider(), 1);
//
//        GuiRectangle scrollBgX = new GuiRectangle(root)
//                .shadedRect(0xFF373737, 0xFFffffff, 0xFF8b8b8b, 0xFF8b8b8b)
//                .constrain(LEFT, Constraint.match(scrollBackground.get(LEFT)))
//                .constrain(RIGHT, Constraint.match(scrollBackground.get(RIGHT)))
//                .constrain(TOP, Constraint.relative(scrollBackground.get(BOTTOM), 1))
//                .constrain(HEIGHT, Constraint.literal(10));
//
//        GuiSlider scrollX = new GuiSlider(root, Axis.X)
//                .setSliderState(scroll.scrollState(Axis.X))
//                .setScrollableElement(scroll, true);
//        Constraints.bindTo(scrollX, scrollBgX);
//
//        GuiTexture scrollHandleX = new GuiTexture(scrollX.getSlider()).dynamicTexture();
//        scrollHandleX.setMaterial(GuiTextures.getter(() -> scrollHandleX.hovered() || scrollX.isDragging() ? "dynamic/button_highlight_borderless" : "dynamic/button_borderless"));
//        Constraints.bindTo(scrollHandleX, scrollX.getSlider(), 1);

//        scroll.enableScissor = false;
//        scroll.getContentElement().setRenderCull(null);
        GuiElement<?> content = scroll.getContentElement();

        RandomSource randy = RandomSource.create(0);
        Random random = new Random(0);
        List<Item> itemList = BuiltInRegistries.ITEM.stream().toList();

        GuiButton lastButton = null;

//        Fast-er
        int yOffset = 0;
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            lastButton = GuiButton.vanillaAnimated(content, Component.literal("Button " + i), () -> System.out.println("Click " + finalI))
                    .constrain(TOP, relative(content.get(TOP), yOffset))
                    .constrain(LEFT, Constraint.match(content.get(LEFT)))
                    .constrain(WIDTH, Constraint.literal(scroll.getValue(WIDTH)))
                    .constrain(HEIGHT, Constraint.literal(20));
            yOffset += 22;

            lastButton.setTooltip(Component.literal("Button Tool Tip " + i));


            GuiButton finalLastButton = lastButton;
//            new GuiItemStack(lastButton, new ItemStack(itemList.get(random.nextInt(itemList.size()))))
            new GuiItemStack(lastButton, new ItemStack(BuiltInRegistries.ITEM.getRandom(randy).map(Holder.Reference::value).orElse(Items.BARRIER)))
                    .constrain(TOP, Constraint.relative(lastButton.get(TOP), () -> finalLastButton.isPressed() ? 1.5 : 2.5))
                    .constrain(RIGHT, Constraint.relative(lastButton.get(RIGHT), () -> finalLastButton.isPressed() ? -4.5 : -3.5))
                    .constrain(WIDTH, Constraint.literal(16))
                    .constrain(HEIGHT, Constraint.literal(16));
        }


//        //Very, Very Inefficient
//        for (int i = 0; i < 2000; i++) {
//            int finalI = i;
//            Constraint topConstraint = lastButton == null ? match(content.get(TOP)) : relative(lastButton.get(BOTTOM), 2);
//            lastButton = GuiButton.vanillaAnimated(content, Component.literal("Button " + i), () -> System.out.println("Click " + finalI))
//                    .constrain(TOP, topConstraint)
//                    .constrain(LEFT, Constraint.match(content.get(LEFT)))
//                    .constrain(WIDTH, Constraint.literal(scroll.getValue(WIDTH) * 2))
//                    .constrain(HEIGHT, Constraint.literal(20));
//
//            lastButton.setTooltip(Component.literal("Button Tool Tip " + i));
//
//
//            GuiButton finalLastButton = lastButton;
//            new GuiItemStack(lastButton, new ItemStack(itemList.get(random.nextInt(itemList.size()))))
//                    .constrain(TOP, Constraint.relative(lastButton.get(TOP), () -> finalLastButton.isPressed() ? 1.5 : 2.5))
//                    .constrain(RIGHT, Constraint.relative(lastButton.get(RIGHT), () -> finalLastButton.isPressed() ? -4.5 : -3.5))
//                    .constrain(WIDTH, Constraint.literal(16))
//                    .constrain(HEIGHT, Constraint.literal(16));
//
//        }

        new GuiItemStack(content, () -> gui.mc().player.getItemBySlot(EquipmentSlot.MAINHAND))
                .constrain(TOP, Constraint.relative(lastButton.get(BOTTOM), 20))
                .constrain(LEFT, Constraint.relative(lastButton.get(RIGHT), 20))
                .constrain(WIDTH, Constraint.literal(100))
                .constrain(HEIGHT, Constraint.literal(100));


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
