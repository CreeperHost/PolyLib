package net.creeperhost.testmod.datagen;

import net.creeperhost.polylib.datagen.PolyLibLangProvider;
import net.minecraft.data.PackOutput;

public class TestModNeoLangProvider extends PolyLibLangProvider
{
    public TestModNeoLangProvider(PackOutput output)
    {
        super(output, "testmod");
    }

    @Override
    protected void addModTranslations()
    {
        // All items, blocks, and creative tabs are contributed automatically.
        // Add any manual keys here (e.g. tooltips, chat messages).
    }
}
