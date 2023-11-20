package net.creeperhost.polylib.fabric.client;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * Created by brandon3055 on 21/08/2023
 */
public class ResourceReloadListenerWrapper implements IdentifiableResourceReloadListener {
    private final ResourceLocation listenerId;
    private final Supplier<PreparableReloadListener> getWrapped;

    public ResourceReloadListenerWrapper(Supplier<PreparableReloadListener> getWrapped, ResourceLocation listenerId) {
        this.getWrapped = getWrapped;
        this.listenerId = listenerId;
    }

    @Override
    public ResourceLocation getFabricId() {
        return listenerId;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return getWrapped.get().reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
    }
}
