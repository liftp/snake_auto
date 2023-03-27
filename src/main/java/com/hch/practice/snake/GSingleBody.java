package com.hch.practice.snake;

public class GSingleBody {

    
    public GSingleBody(int x, int y) {
        this.x = x;
        this.y = y;
    }
    private int x;
    private int y;
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

    public GSingleBody copy() {
        return new GSingleBody(x, y);
    }

    public boolean posEquals(GSingleBody other) {
        if (other.x == x && other.y == y) {
            return true;
        } 
        return false;
    }

    @Override
    public String toString() {
        return String.format("坐标: x=%s,y=%s", x, y);
    }

    
}
