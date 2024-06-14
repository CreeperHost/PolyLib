package net.creeperhost.testmod.forge;

import dev.architectury.registry.menu.MenuRegistry;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.blocks.creativepower.PowerScreen;
import net.creeperhost.testmod.blocks.inventorytestblock.ScreenInventoryTestBlock;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockGui;
import net.creeperhost.testmod.init.TestContainers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistration
{
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        System.out.println("Registering screens");
        event.register(TestContainers.TEST_INVENTORY_CONTAINER.get(), ScreenInventoryTestBlock::create);
        event.register(TestContainers.MGUI_TEST_BLOCK_CONTAINER.get(), MGuiTestBlockGui::create);
        event.register(TestContainers.POWER_CONTAINEr.get(), PowerScreen::new);
    }
}
