package xyz.vergoclient.event.impl;

import xyz.vergoclient.event.Event;

public class StepEvent extends Event {
    private double stepHeight;
    private double realHeight;
    private boolean pre;

    public StepEvent(boolean state, double stepHeight, double realHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
        this.realHeight = realHeight;
    }

    public StepEvent(boolean state, double stepHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
        this.realHeight = this.realHeight;
    }

    public boolean isPre() {
        return this.pre;
    }

    public double getStepHeight() {
        return this.stepHeight;
    }

    public void setStepHeight(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getRealHeight() {
        return this.realHeight;
    }

    public void setRealHeight(double realHeight) {
        this.realHeight = realHeight;
    }
}