package net.creeperhost.polylib.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public interface CreativeTabEvents
{
    Event<CreativeTabEvents.REGISTER> REGISTER = EventFactory.createEventResult();
    Event<CreativeTabEvents.BUILD_CONTENTS> BUILD_CONTENTS = EventFactory.createEventResult();

    interface REGISTER
    {
        void register();
    }

    interface BUILD_CONTENTS
    {
        void build();
    }
}
