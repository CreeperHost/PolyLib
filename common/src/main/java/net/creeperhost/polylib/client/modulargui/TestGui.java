package net.creeperhost.polylib.client.modulargui;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.lib.GuiBuilder;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.sprite.GuiTextures;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Created by brandon3055 on 19/08/2023
 */
public class TestGui implements GuiBuilder {

    @Override
    public void buildGui(ModularGui gui) {


        gui.initStandardGui(100, 100);
        gui.setGuiTitle(Component.literal("Hello World!"));

        GuiElement<?> root = gui.getRoot();

        GuiElement<?> test = new GuiElement(root) {
            @Override
            public void render(GuiRender render, double mouseX, double mouseY, float partialTicks) {
                super.render(render, mouseX, mouseY, partialTicks);
                GuiGraphics graphics = new GuiGraphics(render.mc(), render.buffers());
//                graphics.fillGradient((int) xMin(), (int) yMin(), (int) xMax(), (int) yMax(), 0xFFFFFFFF, 0xFFFFFFFF);

                Material material = GuiTextures.get("light/button");
                VertexConsumer consumer = material.buffer(render.buffers(), TestGui::makeType);
                drawSprite(consumer, xMin(), yMin(), xSize(), xSize(), material.sprite());

                graphics.drawString(font(), "Hello World??", (int) xMin() + 10, (int) yMin() + 10, 0x000000, false);



            }
        };

        test.constrain(TOP, Constraint.match(root.get(TOP)));
        test.constrain(LEFT, Constraint.match(root.get(LEFT)));
        test.constrain(WIDTH, Constraint.literal(100));
        test.constrain(HEIGHT, Constraint.literal(10));


    }

    all the stuff like this will be in GuiRender so it will have access to the PoseStask, I just need to pass in the material and do the thing.
    public static void drawSprite(VertexConsumer builder, double x, double y, double width, double height, TextureAtlasSprite sprite) {
        //@formatter:off
        builder.vertex(x,          y + height, 0).color(1F, 1F, 1F, 1F).uv(sprite.getU0(), sprite.getV1()).endVertex();
        builder.vertex(x + width,  y + height, 0).color(1F, 1F, 1F, 1F).uv(sprite.getU1(), sprite.getV1()).endVertex();
        builder.vertex(x + width,  y,          0).color(1F, 1F, 1F, 1F).uv(sprite.getU1(), sprite.getV0()).endVertex();
        builder.vertex(x,          y,          0).color(1F, 1F, 1F, 1F).uv(sprite.getU0(), sprite.getV0()).endVertex();
        //@formatter:on
    }

    //Probably put this in GuiRender?
    public static RenderType makeType(ResourceLocation location) {
        return RenderType.create("sprite_type", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader))
                .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false));
    }
}
