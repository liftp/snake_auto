package com.hch.practice.snake;

import java.awt.Color;

public enum GMapEle {
    SNAKE(2, "snake", Color.green),
    WALL(1, "wall", Color.white),
    FOOD(3, "food", Color.yellow),
    PASS(0, "pass", Color.black);

    private int val;
    private String name;
    private Color color;
    
    GMapEle(int val, String name, Color color) {
        this.val = val;
        this.name = name;
        this.color = color;
    }

    public int getVal() {
        return val;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

}
