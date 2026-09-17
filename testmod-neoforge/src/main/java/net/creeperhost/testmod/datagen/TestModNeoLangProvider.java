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
        add("testmod.polylib.config_button", "Test Mod Settings");
        add("testmod.configuration.title", "Test Mod Settings");
        add("testmod.configuration.enabled", "Enabled");
        add("testmod.configuration.enabled.tooltip", "Enable the example feature.");
        add("testmod.configuration.count", "Count");
        add("testmod.configuration.count.tooltip", "An example integer setting.");
        add("testmod.configuration.largeCount", "Large Count");
        add("testmod.configuration.largeCount.tooltip", "An example setting for numbers larger than an integer can hold.");
        add("testmod.configuration.scale", "Scale");
        add("testmod.configuration.scale.tooltip", "An example decimal setting.");
        add("testmod.configuration.greeting", "Greeting");
        add("testmod.configuration.greeting.tooltip", "The example greeting text.");
        add("testmod.configuration.mode", "Mode");
        add("testmod.configuration.mode.tooltip", "Choose one of the example modes.");
        add("testmod.configuration.mode.first", "First");
        add("testmod.configuration.mode.second", "Second");
        add("testmod.configuration.mode.third", "Third");
        add("testmod.configuration.nested.opacity", "Opacity");
        add("testmod.configuration.nested.opacity.tooltip", "An example decimal value in a nested config object.");
        add("testmod.configuration.nested.offset", "Offset");
        add("testmod.configuration.nested.offset.tooltip", "An example position offset. Negative values are allowed.");
        add("testmod.configuration.flags.example.mod", "Example Mod Flag");
        add("testmod.configuration.flags.example.mod.tooltip", "Enable or disable the flag stored under the example.mod map key.");
        add("testmod.configuration.names", "Names");
        add("testmod.configuration.names.tooltip", "An example list of names. Edit this value in the config file.");
    }
}
