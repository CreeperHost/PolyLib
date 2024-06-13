package net.creeperhost.testmod.client.gui;

import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.sprite.ModAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.creeperhost.testmod.TestMod.MOD_ID;

/**
 * This class contains Polylib's gui texture atlas and methods for retrieving gui materials.
 * <p>
 * Mods wishing to use their own textures in guis can pretty much just copy-paste this class.
 * Just make sure MOD_ID points to your mod's id.
 * <p>
 * Don't forget to register the atlas holder as a resource reload listener.
 * polylib does this in PolyLibFabric (fabric) and ForgeClientEvents (forge)
 * <p>
 * Then the last thing you need is an atlas json identical to polylib's in "assets/[mod-id]/atlasses/gui.json"
 *
 * <p>
 * Created by brandon3055 on 29/06/2023
 */
public class TestModTextures {

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
        return MATERIAL_CACHE.computeIfAbsent(MOD_ID + ":" + texture, e -> getUncached(texture));
    }

    public static Material get(Supplier<String> texture) {
        return get(texture.get());
    }

    public static Supplier<Material> getter(Supplier<String> texture) {
        return () -> get(texture.get());
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
        return new Material(ATLAS_HOLDER.atlasLocation(), ResourceLocation.fromNamespaceAndPath(MOD_ID, "gui/" + texture), ATLAS_HOLDER::getSprite);
    }
}
