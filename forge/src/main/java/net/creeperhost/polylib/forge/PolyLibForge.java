package net.creeperhost.polylib.forge;

import dev.architectury.platform.forge.EventBuses;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.CreativeTabEvents;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PolyLib.MOD_ID)
public class PolyLibForge
{
    public PolyLibForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(PolyLib.MOD_ID, eventBus);
        eventBus.addListener(this::registerCreativeTab);
        eventBus.addListener(this::creativeTabContentsEvent);

        PolyLib.init();
    }

    public void registerCreativeTab(CreativeModeTabEvent.Register event)
    {
        CreativeTabEvents.REGISTER.invoker().register();

//        event.registerCreativeModeTab(new ResourceLocation(MOD_ID, "creative_tab"), builder -> builder.icon(() -> new ItemStack(ShrinkItems.SHRINKING_DEVICE.get()))
//                .title(Component.translatable("key.shrink.category"))
//                .displayItems((features, output, hasPermissions) -> ShrinkItems.ITEMS.getEntries().forEach(itemRegistryObject -> output.accept(new ItemStack(itemRegistryObject.get())))));

    }

    public void creativeTabContentsEvent(CreativeModeTabEvent.BuildContents event)
    {
        CreativeTabEvents.BUILD_CONTENTS.invoker().build();
    }
}
