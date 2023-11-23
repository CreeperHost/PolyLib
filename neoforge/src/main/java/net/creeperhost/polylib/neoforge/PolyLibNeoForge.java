package net.creeperhost.polylib.neoforge;

import net.creeperhost.polylib.PolyLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;

@Mod(PolyLib.MOD_ID)
public class PolyLibNeoForge
{
    public PolyLibNeoForge()
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> NeoForgeClientEvents::init);

        PolyLib.init();
    }
}
