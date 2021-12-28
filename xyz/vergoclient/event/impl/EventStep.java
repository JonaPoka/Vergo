package xyz.vergoclient.event.impl;

import net.minecraft.entity.Entity;
import xyz.vergoclient.event.Event;

public class EventStep extends Event {

    public float stepHeight;
    public Entity entity;

    public EventStep(float stepHeight, Entity entity) {
        this.stepHeight = stepHeight;
        this.entity = entity;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

}