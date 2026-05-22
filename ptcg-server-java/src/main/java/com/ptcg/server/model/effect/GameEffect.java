package com.ptcg.server.model.effect;

public interface GameEffect {
    String getType();
    boolean isPreventDefault();
    void setPreventDefault(boolean preventDefault);
}
