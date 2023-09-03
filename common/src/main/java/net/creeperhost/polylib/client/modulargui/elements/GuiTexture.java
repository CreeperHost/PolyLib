package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.BackgroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Borders;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 28/08/2023
 */
public class GuiTexture extends GuiElement<GuiTexture> implements BackgroundRender {
    private Supplier<Material> getMaterial;
    private Borders dynamicBorders = null;
    
    /**
     * @param parent parent {@link GuiParent}.
     */
    public GuiTexture(@NotNull GuiParent<?> parent) {
        super(parent);
    }

    public GuiTexture(@NotNull GuiParent<?> parent, Supplier<Material> supplier) {
        super(parent);
        setMaterial(supplier);
    }

    public GuiTexture(@NotNull GuiParent<?> parent, Material material) {
        super(parent);
        setMaterial(material);
    }

    public GuiTexture setMaterial(Supplier<Material> supplier) {
        this.getMaterial = supplier;
        return this;
    }

    public GuiTexture setMaterial(Material material) {
        this.getMaterial = () -> material;
        return this;
    }

    @Nullable
    public Material getMaterial() {
        return getMaterial == null ? null : getMaterial.get();
    }

    /**
     * Enables dynamic texture resizing though the use of cutting and tiling.
     * Only works with textures that can be cut up and tiled without issues, e.g. background textures or button textures.
     * This method uses the standard border with of 5 pixels on all sides.
     */
    public GuiTexture dynamicTexture() {
        return dynamicTexture(5);
    }

    /**
     * Enables dynamic texture resizing though the use of cutting and tiling.
     * Only works with textures that can be cut up and tiled without issues, e.g. background textures or button textures.
     * The border parameters indicate the width of border around the texture that must be maintained during the cutting and tiling process.
     * For standardisation purposes the border width should be >= 5
     */
    public GuiTexture dynamicTexture(int textureBorders) {
        return dynamicTexture(Borders.create(textureBorders));
    }

    /**
     * Enables dynamic texture resizing though the use of cutting and tiling.
     * Only works with textures that can be cut up and tiled without issues, e.g. background textures or button textures.
     * The border parameters indicate the width of border around the texture that must be maintained during the cutting and tiling process.
     * For standardisation purposes the border width should be >= 5
     */
    public GuiTexture dynamicTexture(Borders textureBorders) {
        dynamicBorders = textureBorders;
        return this;
    }

    @Override
    public void renderBehind(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        Material material = getMaterial();
        if (material == null) return;
        if (dynamicBorders != null) {
            render.dynamicTex(material, getRectangle(), dynamicBorders);
        } else {
            render.texRect(material, getRectangle());
        }
    }
}
