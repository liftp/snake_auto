package com.hch.practice.snake;

public class GSingleBody {

    
    public GSingleBody(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public GSingleBody(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
    private int x;
    private int y;
    private int direction;  // 运动方向：-1 左 1右 -2 上 2 下
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public GSingleBody copy() {
        return new GSingleBody(x, y, direction);
    }

    public boolean posEquals(GSingleBody other) {
        if (other.x == x && other.y == y) {
            return true;
        } 
        return false;
    }

    @Override
    public String toString() {
        String d = "";
        switch(direction) {
            case -1: d = "<"; break;
            case  1: d = ">"; break;
            case -2: d = "^"; break;
            case  2: d = "v"; break;
            default: d = "";
        }
        return String.format("坐标: x=%s,y=%s,d=%s", x, y, d);
    }

    
}
