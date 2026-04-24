package net.creeperhost.polylib.register;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Cross-platform screen registration queue.
 *
 * <p>Call {@link #register} from client-only init code (e.g. inside an {@code isClient()} guard).
 * Platform entry points flush the queue at the appropriate time:
 * <ul>
 *   <li><b>NeoForge</b> — call {@code NeoPolyScreens.registerToBus(bus)} from your mod constructor;
 *       the flush happens during {@code RegisterMenuScreensEvent}.</li>
 *   <li><b>Fabric</b> — call {@link #flush} from your {@code ClientModInitializer.onInitializeClient()}.</li>
 * </ul>
 */
public class PolyScreens {

    private static final List<Runnable> PENDING = new ArrayList<>();

    public static <M extends AbstractContainerMenu, S extends AbstractContainerScreen<M>>
    void register(Supplier<MenuType<M>> menuType, MenuScreens.ScreenConstructor<M, S> factory) {
        PENDING.add(() -> MenuScreens.register(menuType.get(), factory));
    }

    public static void flush() {
        PENDING.forEach(Runnable::run);
        PENDING.clear();
    }
}
