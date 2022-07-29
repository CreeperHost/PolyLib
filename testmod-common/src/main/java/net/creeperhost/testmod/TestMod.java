package net.creeperhost.testmod;

import dev.architectury.registry.CreativeTabRegistry;
import net.creeperhost.testmod.init.TestItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMod
{
    public static final String MOD_ID = "testmod";
    public static final Logger LOGGER = LogManager.getLogger();


    public static void init()
    {
        LOGGER.info("Hi I'm " + MOD_ID);
        TestItems.ITEMS.register();
    }
}
