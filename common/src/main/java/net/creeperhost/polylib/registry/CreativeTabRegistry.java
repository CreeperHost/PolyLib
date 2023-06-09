//package net.creeperhost.polylib.registry;
//
//import net.creeperhost.polylib.PolyLib;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.CreativeModeTab;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.ItemLike;
//
//import java.util.function.Supplier;
//
//public class CreativeTabRegistry
//{
//    public static CreativeModeTab of(Component name, Supplier<ItemStack> itemStack)
//    {
//        PolyLib.LOGGER.info("Registering creative tab: " + name);
//        return dev.architectury.registry.CreativeTabRegistry.create(name, itemStack);
//    }
//
//    public static CreativeModeTab of(String modid, String name, Supplier<ItemStack> itemStack)
//    {
//        Component component = Component.literal(modid + ":" + name);
//        return of(component, itemStack);
//    }
//
//    @SafeVarargs
//    public static <I extends ItemLike, T extends Supplier<I>> void append(ResourceLocation creative, T... items)
//    {
//        try
//        {
//            var tab = dev.architectury.registry.CreativeTabRegistry.defer(creative);
//            dev.architectury.registry.CreativeTabRegistry.append(tab, items);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//}
