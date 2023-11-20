package net.creeperhost.polylib.client.modulargui.lib;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Divisor;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * This class contains an exact copy of all vanillas texture rendering.
 * Avoid using these methods if at all possible.
 * //TODO, Finish working out parameter names
 * <p>
 * Created by brandon3055 on 25/08/2023
 */
@Deprecated
public abstract class LegacyRender {

    public abstract PoseStack pose();

    public void blit(int xMin, int yMin, int z, int p_281329_, int p_283035_, TextureAtlasSprite p_281614_) {
        this.innerBlit(p_281614_.atlasLocation(), xMin, xMin + p_281329_, yMin, yMin + p_283035_, z, p_281614_.getU0(), p_281614_.getU1(), p_281614_.getV0(), p_281614_.getV1());
    }

    public void blit(int xMin, int yMin, int p_282618_, int p_282755_, int p_281717_, TextureAtlasSprite p_281874_, float p_283559_, float p_282730_, float p_283530_, float p_282246_) {
        this.innerBlit(p_281874_.atlasLocation(), xMin, xMin + p_282755_, yMin, yMin + p_281717_, p_282618_, p_281874_.getU0(), p_281874_.getU1(), p_281874_.getV0(), p_281874_.getV1(), p_283559_, p_282730_, p_283530_, p_282246_);
    }


    public void blit(ResourceLocation texture, int xMin, int yMin, int p_283134_, int p_282778_, int p_281478_, int p_281821_) {
        this.blit(texture, xMin, yMin, 0, (float)p_283134_, (float)p_282778_, p_281478_, p_281821_, 256, 256);
    }

    public void blit(ResourceLocation texture, int xMin, int yMin, int z, float p_283029_, float p_283061_, int p_282845_, int p_282558_, int p_282832_, int p_281851_) {
        this.blit(texture, xMin, xMin + p_282845_, yMin, yMin + p_282558_, z, p_282845_, p_282558_, p_283029_, p_283061_, p_282832_, p_281851_);
    }

    public void blit(ResourceLocation texture, int xMin, int yMin, int p_282058_, int p_281939_, float p_282285_, float p_283199_, int p_282186_, int p_282322_, int p_282481_, int p_281887_) {
        this.blit(texture, xMin, xMin + p_282058_, yMin, yMin + p_281939_, 0, p_282186_, p_282322_, p_282285_, p_283199_, p_282481_, p_281887_);
    }

    public void blit(ResourceLocation texture, int xMin, int yMin, float p_282809_, float p_282942_, int p_281922_, int p_282385_, int p_282596_, int p_281699_) {
        this.blit(texture, xMin, yMin, p_281922_, p_282385_, p_282809_, p_282942_, p_281922_, p_282385_, p_282596_, p_281699_);
    }

    void blit(ResourceLocation texture, int xMin, int xMax, int yMin, int yMax, int z, int p_282193_, int p_281980_, float p_282660_, float p_281522_, int texWidth, int texHeight) {
        this.innerBlit(texture, xMin, xMax, yMin, yMax, z, (p_282660_ + 0.0F) / (float)texWidth, (p_282660_ + (float)p_282193_) / (float)texWidth, (p_281522_ + 0.0F) / (float)texHeight, (p_281522_ + (float)p_281980_) / (float)texHeight);
    }

    void innerBlit(ResourceLocation texture, int xMin, int xMax, int yMin, int yMax, int z, float uMin, float uMax, float vMin, float vMax) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, (float)xMin, (float)yMin, (float)z).uv(uMin, vMin).endVertex();
        bufferbuilder.vertex(matrix4f, (float)xMin, (float)yMax, (float)z).uv(uMin, vMax).endVertex();
        bufferbuilder.vertex(matrix4f, (float)xMax, (float)yMax, (float)z).uv(uMax, vMax).endVertex();
        bufferbuilder.vertex(matrix4f, (float)xMax, (float)yMin, (float)z).uv(uMax, vMin).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    void innerBlit(ResourceLocation texture, int xMin, int xMax, int yMin, int yMax, int z, float uMin, float uMax, float vMin, float vMax, float red, float green, float blue, float alpha) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(matrix4f, (float)xMin, (float)yMin, (float)z).color(red, green, blue, alpha).uv(uMin, vMin).endVertex();
        bufferbuilder.vertex(matrix4f, (float)xMin, (float)yMax, (float)z).color(red, green, blue, alpha).uv(uMin, vMax).endVertex();
        bufferbuilder.vertex(matrix4f, (float)xMax, (float)yMax, (float)z).color(red, green, blue, alpha).uv(uMax, vMax).endVertex();
        bufferbuilder.vertex(matrix4f, (float)xMax, (float)yMin, (float)z).color(red, green, blue, alpha).uv(uMax, vMin).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.disableBlend();
    }

    public void blitNineSliced(ResourceLocation texture, int p_282275_, int p_281581_, int p_283274_, int p_281626_, int p_283005_, int p_282047_, int p_282125_, int p_283423_, int p_281424_) {
        this.blitNineSliced(texture, p_282275_, p_281581_, p_283274_, p_281626_, p_283005_, p_283005_, p_283005_, p_283005_, p_282047_, p_282125_, p_283423_, p_281424_);
    }

    public void blitNineSliced(ResourceLocation texture, int p_281513_, int p_281865_, int p_282482_, int p_282661_, int p_282068_, int p_281294_, int p_281681_, int p_281957_, int p_282300_, int p_282769_) {
        this.blitNineSliced(texture, p_281513_, p_281865_, p_282482_, p_282661_, p_282068_, p_281294_, p_282068_, p_281294_, p_281681_, p_281957_, p_282300_, p_282769_);
    }

    public void blitNineSliced(ResourceLocation texture, int xMin, int yMin, int p_283273_, int p_282043_, int p_281430_, int p_281412_, int p_282566_, int p_281971_, int p_282879_, int p_281529_, int p_281924_, int p_281407_) {
        p_281430_ = Math.min(p_281430_, p_283273_ / 2);
        p_282566_ = Math.min(p_282566_, p_283273_ / 2);
        p_281412_ = Math.min(p_281412_, p_282043_ / 2);
        p_281971_ = Math.min(p_281971_, p_282043_ / 2);
        if (p_283273_ == p_282879_ && p_282043_ == p_281529_) {
            this.blit(texture, xMin, yMin, p_281924_, p_281407_, p_283273_, p_282043_);
        } else if (p_282043_ == p_281529_) {
            this.blit(texture, xMin, yMin, p_281924_, p_281407_, p_281430_, p_282043_);
            this.blitRepeating(texture, xMin + p_281430_, yMin, p_283273_ - p_282566_ - p_281430_, p_282043_, p_281924_ + p_281430_, p_281407_, p_282879_ - p_282566_ - p_281430_, p_281529_);
            this.blit(texture, xMin + p_283273_ - p_282566_, yMin, p_281924_ + p_282879_ - p_282566_, p_281407_, p_282566_, p_282043_);
        } else if (p_283273_ == p_282879_) {
            this.blit(texture, xMin, yMin, p_281924_, p_281407_, p_283273_, p_281412_);
            this.blitRepeating(texture, xMin, yMin + p_281412_, p_283273_, p_282043_ - p_281971_ - p_281412_, p_281924_, p_281407_ + p_281412_, p_282879_, p_281529_ - p_281971_ - p_281412_);
            this.blit(texture, xMin, yMin + p_282043_ - p_281971_, p_281924_, p_281407_ + p_281529_ - p_281971_, p_283273_, p_281971_);
        } else {
            this.blit(texture, xMin, yMin, p_281924_, p_281407_, p_281430_, p_281412_);
            this.blitRepeating(texture, xMin + p_281430_, yMin, p_283273_ - p_282566_ - p_281430_, p_281412_, p_281924_ + p_281430_, p_281407_, p_282879_ - p_282566_ - p_281430_, p_281412_);
            this.blit(texture, xMin + p_283273_ - p_282566_, yMin, p_281924_ + p_282879_ - p_282566_, p_281407_, p_282566_, p_281412_);
            this.blit(texture, xMin, yMin + p_282043_ - p_281971_, p_281924_, p_281407_ + p_281529_ - p_281971_, p_281430_, p_281971_);
            this.blitRepeating(texture, xMin + p_281430_, yMin + p_282043_ - p_281971_, p_283273_ - p_282566_ - p_281430_, p_281971_, p_281924_ + p_281430_, p_281407_ + p_281529_ - p_281971_, p_282879_ - p_282566_ - p_281430_, p_281971_);
            this.blit(texture, xMin + p_283273_ - p_282566_, yMin + p_282043_ - p_281971_, p_281924_ + p_282879_ - p_282566_, p_281407_ + p_281529_ - p_281971_, p_282566_, p_281971_);
            this.blitRepeating(texture, xMin, yMin + p_281412_, p_281430_, p_282043_ - p_281971_ - p_281412_, p_281924_, p_281407_ + p_281412_, p_281430_, p_281529_ - p_281971_ - p_281412_);
            this.blitRepeating(texture, xMin + p_281430_, yMin + p_281412_, p_283273_ - p_282566_ - p_281430_, p_282043_ - p_281971_ - p_281412_, p_281924_ + p_281430_, p_281407_ + p_281412_, p_282879_ - p_282566_ - p_281430_, p_281529_ - p_281971_ - p_281412_);
            this.blitRepeating(texture, xMin + p_283273_ - p_282566_, yMin + p_281412_, p_281430_, p_282043_ - p_281971_ - p_281412_, p_281924_ + p_282879_ - p_282566_, p_281407_ + p_281412_, p_282566_, p_281529_ - p_281971_ - p_281412_);
        }
    }

    public void blitRepeating(ResourceLocation texture, int p_283575_, int p_283192_, int p_281790_, int p_283642_, int p_282691_, int p_281912_, int p_281728_, int p_282324_) {
        int i = p_283575_;

        int j;
        for(IntIterator intiterator = slices(p_281790_, p_281728_); intiterator.hasNext(); i += j) {
            j = intiterator.nextInt();
            int k = (p_281728_ - j) / 2;
            int l = p_283192_;

            int i1;
            for(IntIterator intiterator1 = slices(p_283642_, p_282324_); intiterator1.hasNext(); l += i1) {
                i1 = intiterator1.nextInt();
                int j1 = (p_282324_ - i1) / 2;
                this.blit(texture, i, l, p_282691_ + k, p_281912_ + j1, j, i1);
            }
        }

    }

    private static IntIterator slices(int p_282197_, int p_282161_) {
        int i = Mth.positiveCeilDiv(p_282197_, p_282161_);
        return new Divisor(p_282197_, i);
    }

}
