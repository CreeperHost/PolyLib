package net.creeperhost.polylib.registry;

import net.creeperhost.polylib.PolyLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CreativeTabRegistry
{
    public static CreativeModeTab of(ResourceLocation name, Supplier<ItemStack> itemStack)
    {
        PolyLib.LOGGER.info("Registering creative tab " + name);
        return dev.architectury.registry.CreativeTabRegistry.create(name, itemStack);
    }
}
