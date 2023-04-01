package com.hch.practice.snake;

public enum GAction {
    LEFT("left", -1),
    RIGHT("right", 1),
    TOP("top", -2),
    BOTTOM("bottom", 2);
    private String desc;
    private int direction;

    GAction(String desc, int direction) {
        this.desc = desc;
        this.direction = direction;
    }

    public String getDesc() {
        return desc;
    }

    public int getDirection() {
        return direction;
    }
    
}
