package net.creeperhost.polylib.fabric.datagen;

import java.nio.file.Path;

public class PolyDataGen
{
    public static String COMMON = System.getProperty("polylib.datagen.output-dir-common");
    public static String FORGE = System.getProperty("polylib.datagen.output-dir-forge");
    public static String FABRIC = System.getProperty("polylib.datagen.output-dir-fabric");
    public static String QUILT = System.getProperty("polylib.datagen.output-dir-quilt");


    public static Path getPathFromModuleType(ModuleType moduleType)
    {
        switch (moduleType)
        {
            case FORGE : return Path.of(FORGE);
            case COMMON: return Path.of(COMMON);
            case FABRIC: return Path.of(FABRIC);
            case QUILT: return Path.of(QUILT);
        }
        try
        {
            throw new Exception("Unable to get path for "  + moduleType + " ensure vm args are added for polylib.datagen.output-dir-" + moduleType.toString().toLowerCase());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
