package net.creeperhost.testmod.forge;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.creeperhost.testmod.TestMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;


@Mod(TestMod.MOD_ID)
public class TestModNeoForge
{
    public TestModNeoForge(IEventBus bus)
    {
        TestMod.init();
//        NeoForge.EVENT_BUS.addListener(TestModNeoForge::onPlayerTick);

        if(Platform.getEnvironment() == Env.CLIENT)
        {
            NeoForgeClientEvents.init(bus);
        }
    }

    //Testing
//    public static void onPlayerTick(PlayerTickEvent.Pre event) {
//        Player player = event.getEntity();
//        if (!(player instanceof ServerPlayer)) return;
//
//        ItemStack stack = player.getMainHandItem();
//        EnergyHandler handler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forPlayerInteraction(player, InteractionHand.MAIN_HAND));
//        if (handler == null) return;
//
//        try (Transaction transaction = Transaction.open(null)){
//            handler.extract(1, transaction);
//            transaction.commit();
//        }
//    }
}
