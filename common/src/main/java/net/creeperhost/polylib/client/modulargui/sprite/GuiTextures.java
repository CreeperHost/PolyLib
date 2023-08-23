package net.creeperhost.polylib.client.modulargui.sprite;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static net.creeperhost.polylib.PolyLib.MOD_ID;

/**
 * This class contains Polylib's gui texture atlas and methods for retrieving gui materials.
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public class GuiTextures {

    private static final ModAtlasHolder ATLAS_HOLDER = new ModAtlasHolder(MOD_ID, "textures/atlas/gui.png", "gui");
    private static final Map<String, Material> MATERIAL_CACHE = new HashMap<>();

    public static ModAtlasHolder getAtlasHolder() {
        return ATLAS_HOLDER;
    }

    /**
     * Returns a cached Material for the specified gui texture.
     * Warning: Do not use this if you intend to use the material with multiple render types.
     * The material will cache the first render type it is used with.
     * Instead use {@link #getUncached(String)}
     *
     * @param texture The texture path relative to "modid:gui/"
     */
    public static Material get(String texture) {
        return MATERIAL_CACHE.computeIfAbsent(MOD_ID + ":" + texture, GuiTextures::getUncached);
    }

    /**
     * Use this to retrieve a new uncached material for the specified gui texture.
     * Feel free to hold onto the returned material.
     * Storing it somewhere is more efficient than recreating it every render frame.
     *
     * @param texture The texture path relative to "modid:gui/"
     * @return A new Material for the specified gui texture.
     */
    public static Material getUncached(String texture) {
        return new Material(ATLAS_HOLDER.atlasLocation(), new ResourceLocation(MOD_ID, "gui/" + texture), ATLAS_HOLDER::getSprite);
    }
}
