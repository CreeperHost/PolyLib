package net.creeperhost.polylib.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface ClientRenderEvents
{
    Event<ClientRenderEvents.LAST> LAST = EventFactory.createEventResult();

    interface LAST
    {
        void onRenderLastEvent(PoseStack poseStack);
    }
}
