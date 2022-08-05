package net.creeperhost.polylib.generators;

import dev.architectury.platform.Mod;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Function;

public class PolyDataGenerator extends DataGenerator
{
    private final Mod mod;
    private final boolean strictValidation;

    public PolyDataGenerator(Path output, Mod mod, boolean strictValidation)
    {
        super(output, Collections.emptyList(), SharedConstants.getCurrentVersion(), true);
        this.mod = mod;
        this.strictValidation = strictValidation;
    }

    public <P extends DataProvider> P addProvider(Function<PolyDataGenerator, P> provider)
    {
        return addProvider(true, provider);
    }

    public <P extends DataProvider> P addProvider(boolean include, Function<PolyDataGenerator, P> provider)
    {
        P p = provider.apply(this);
        addProvider(include, p);
        return p;
    }

    public void addProvider(DataProvider dataProvider)
    {
        super.addProvider(true, dataProvider);
    }

    public Mod getModContainer()
    {
        return mod;
    }

    public String getModId()
    {
        return getModContainer().getModId();
    }

    public boolean isStrictValidationEnabled()
    {
        return strictValidation;
    }
}
