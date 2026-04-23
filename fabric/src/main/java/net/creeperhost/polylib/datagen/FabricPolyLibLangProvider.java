package net.creeperhost.polylib.datagen;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract {@link FabricLanguageProvider} that automatically includes every translation entry
 * contributed to PolyLib's lang datagen system via any PolyLib registration API.
 *
 * <h3>Usage</h3>
 * Extend this class in your mod's Fabric datagen module instead of extending
 * {@link FabricLanguageProvider} directly:
 * <pre>{@code
 * public class ModLangProvider extends FabricPolyLibLangProvider {
 *     public ModLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
 *         super(output, registries);
 *     }
 *
 *     @Override
 *     protected void addModTranslations(HolderLookup.Provider registries, TranslationBuilder builder) {
 *         // Only entries NOT managed by PolyLib registration APIs.
 *     }
 * }
 * }</pre>
 */
public abstract class FabricPolyLibLangProvider extends FabricLanguageProvider
{
    protected FabricPolyLibLangProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup)
    {
        super(output, registryLookup);
    }

    protected FabricPolyLibLangProvider(FabricPackOutput output, String locale, CompletableFuture<HolderLookup.Provider> registryLookup)
    {
        super(output, locale, registryLookup);
    }

    /**
     * Final — do not override. Drains all PolyLib-tracked contributions first, then
     * calls {@link #addModTranslations} for mod-specific manual entries.
     */
    @Override
    public final void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder)
    {
        PolyLangContributions.drainTo(builder::add);
        addModTranslations(registryLookup, builder);
    }

    /**
     * Override to add translation entries NOT automatically contributed via PolyLib APIs.
     * Items, blocks, and tabs registered with English-name overloads are included automatically.
     */
    protected abstract void addModTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder);
}
