package com.hch.practice.snake;

public class PathCost implements Comparable<PathCost> {
    
    // 累计成本
    private int cost;

    // 目标坐标
    private int x;
    private int y;
    private int pathG; // 累计路径成本

    public int getPathG() {
        return pathG;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PathCost(int cost, int g, int x, int y) {
        this.cost = cost;
        this.pathG = g;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PathCost) {
            PathCost path = (PathCost) obj;
            return this.getX() == path.getX() && this.getY() == path.getY();
        }
        return false;
    }

    @Override
    public int compareTo(PathCost o) {
        return this.cost - o.cost;
    }

    @Override
    public String toString() {
        return String.format("cost:%s,x=%s,y=%s  ", cost, x, y);
    }



}
