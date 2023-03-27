package com.hch.practice.snake;

public class GMapImpl implements GMap {

    private final int[][] map = new int[50][50];

    public GMapImpl() {
        // for(int i = 0; i < 100; i++) {

        // }
    }

    @Override
    public int[][] getMap() {
        
        return map;
    }

    // @Override
    // public int[][] setMap(int[][] update) {
    //     // TODO Auto-generated method stub
    //     return null;
    // }
    
}
