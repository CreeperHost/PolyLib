package net.creeperhost.polylib.sentry;

import dev.architectury.platform.Platform;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import io.sentry.protocol.SentryException;
import net.fabricmc.api.EnvType;

public class SentryRegistry
{
    public static Sentry.OptionsConfiguration<SentryOptions> registerSentryHandler(String dsn, String packagePath)
    {
        return options ->
        {
            options.setDsn(dsn);
            options.setTracesSampleRate(Platform.isDevelopmentEnvironment() ? 1.0 : 0.025);
            options.setEnvironment(Platform.getMinecraftVersion());
            options.setTag("modloader", Platform.isMinecraftForge() ? "Forge" : "Fabric");
            options.setTag("ram", String.valueOf(((Runtime.getRuntime().maxMemory() / 1024) / 1024)));
            options.setDist(System.getProperty("os.arch"));
            options.setServerName(Platform.getEnv() == EnvType.CLIENT ? "integrated" : "dedicated");
            options.setDebug(Platform.isDevelopmentEnvironment());
            options.addInAppInclude(packagePath);
        };
    }

    public static void registerSentryHandler(String dsn, String release, String packagePath)
    {
        Sentry.init(options ->
        {
            options.setDsn(dsn);
            options.setTracesSampleRate(Platform.isDevelopmentEnvironment() ? 1.0 : 0.025);
            options.setRelease(release);
            options.setEnvironment(Platform.getMinecraftVersion());
            options.setTag("modloader", Platform.isMinecraftForge() ? "Forge" : "Fabric");
            options.setTag("ram", String.valueOf(((Runtime.getRuntime().maxMemory() / 1024) / 1024)));
            options.setDist(System.getProperty("os.arch"));
            options.setServerName(Platform.getEnv() == EnvType.CLIENT ? "integrated" : "dedicated");
            options.setDebug(Platform.isDevelopmentEnvironment());
            options.addInAppInclude(packagePath);
            options.setBeforeSend((event, hint) ->
            {
                if(event.getExceptions() == null) return null;
                for (SentryException exception : event.getExceptions())
                {
                    if(exception.getStacktrace() == null) continue;
                    if(exception.getStacktrace().toString().contains("net.creeperhost")) return event;
                }
                return null;
            });
        });
    }
}