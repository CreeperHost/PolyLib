package net.creeperhost.testmod;

import com.mojang.serialization.Codec;
import net.creeperhost.polylib.init.DataComps;
import net.creeperhost.polylib.platform.Services;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataRegistry;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.BroadcastScope;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsRegistry;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.creeperhost.testmod.init.*;
import net.creeperhost.testmod.network.TestOptionalPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class TestModCommon
{
    public static final String MOD_ID = "testmod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    //TODO move these out of the main class
    /** Demo: client-side "reduce screen shake" preference synced to nearby players. */
    public static final PlayerClientSettingsType<Boolean> REDUCE_SCREENSHAKE =
        PlayerClientSettingsRegistry.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "reduce_screenshake"),
            StreamCodec.<RegistryFriendlyByteBuf, Boolean>of(
                (buf, v) -> buf.writeBoolean(v),
                buf -> buf.readBoolean()
            ),
            () -> false,
            BroadcastScope.TRACKING_RANGE,
            /*copyOnDeath*/ true,
            "testmod.setting.reduce_screenshake",
            "Reduce Screen Shake"
        );

    /** Demo: server-authoritative ticks-played counter, synced to owner client. */
    public static final PlayerServerDataType<Integer> TICKS_PLAYED =
        PlayerServerDataRegistry.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "ticks_played"),
            Codec.INT,
            StreamCodec.<RegistryFriendlyByteBuf, Integer>of(
                (buf, v) -> buf.writeVarInt(v),
                buf -> buf.readVarInt()
            ),
            () -> 0,
            /*copyOnDeath*/ true
        );

    public static void init()
    {
        DataComps.registerData();
        TestPlayerData.init();
        TestItems.init();
        TestBlocks.init();
        TestCreativeTabs.init();
        TestContainers.init();
        TestEvents.init();
        TestCommands.init();
        TestOptionalPackets.init();

        if (Services.PLATFORM.isClient()) {
            TestModClientCommon.init();
        }
    }
}
