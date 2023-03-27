package com.hch.practice.snake;

public enum GAction {
    LEFT("left"),
    RIGHT("right"),
    TOP("top"),
    BOTTOM("bottom");
    private String desc;

    GAction(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
