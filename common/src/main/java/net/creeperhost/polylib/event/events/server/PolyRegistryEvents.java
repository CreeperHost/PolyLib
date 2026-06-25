package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;

/**
 * Events related to registry operations.
 *
 * <p>Tier 15 — {@code REGISTRY_ID_REMAP} added in Tier 15.
 */
public final class PolyRegistryEvents
{

    /**
     * Fires when registry integer IDs are remapped during world load
     * (e.g. when mods are added/removed and the block/item ID table needs updating).
     * <p>
     * Fabric-primary; NeoForge handles ID remapping internally with no accessible event hook.
     * {@code remapState} is a Fabric {@code RegistryIdRemapCallback.RemapState<?>} on Fabric;
     * on NeoForge this event is never fired (no-op).
     * <p>
     * Fabric: {@code RegistryIdRemapCallback.event(registry)}
     */
    public static final PolyEvent<RegistryIdRemap> REGISTRY_ID_REMAP = PolyEvent.create(
            handlers -> remapState -> handlers.forEach(h -> h.onRegistryIdRemap(remapState)));

    private PolyRegistryEvents() {}

    @FunctionalInterface
    public interface RegistryIdRemap
    {
        /**
         * @param remapState On Fabric: {@code RegistryIdRemapCallback.RemapState<?>};
         *                   on NeoForge: always null (event never fires).
         */
        void onRegistryIdRemap(Object remapState);
    }


    /**
     * Fires when a dimension's world generation attributes are being modified.
     * Handlers receive loader-specific objects as {@code Object} references to avoid
     * coupling common code to Fabric API.
     * <p>
     * Fabric-only: {@code DimensionEvents.MODIFY_ATTRIBUTES}<br>
     * NeoForge: no equivalent (event never fires — no-op).
     * <p>
     * On Fabric the three arguments are, in order:
     * <ol>
     *   <li>{@code Holder<DimensionType>} — the dimension type holder</li>
     *   <li>{@code EnvironmentAttributeMap.Builder} — the attribute builder to modify</li>
     *   <li>{@code HolderLookup.Provider} — the registry lookup provider</li>
     * </ol>
     * On NeoForge all three are always {@code null}.
     */
    public static final PolyEvent<ModifyDimensionAttributes> MODIFY_DIMENSION_ATTRIBUTES = PolyEvent.create(
            handlers -> (dimType, builder, provider) ->
                    handlers.forEach(h -> h.onModifyDimensionAttributes(dimType, builder, provider)));

    @FunctionalInterface
    public interface ModifyDimensionAttributes
    {
        /**
         * @param dimType  On Fabric: {@code Holder<DimensionType>}; on NeoForge: null.
         * @param builder  On Fabric: {@code EnvironmentAttributeMap.Builder}; on NeoForge: null.
         * @param provider On Fabric: {@code HolderLookup.Provider}; on NeoForge: null.
         */
        void onModifyDimensionAttributes(Object dimType, Object builder, Object provider);
    }
}
