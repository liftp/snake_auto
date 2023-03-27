package com.hch.practice.snake;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import com.hch.practice.alg.AStar;

public class PathSelectAlgAstarImpl implements PathSelectAlgorithm {

    @Override
    public List<GSingleBody> getPath(GMap map, LinkedList<GSingleBody> snakeBodys, GSingleBody end, BiFunction<GSingleBody, GSingleBody,  Boolean> checkBoundry) {
        AStar aStar = new AStar();
        int[][] copyMap = Arrays.stream(map.getMap())
            .map(int[]::clone).toArray(int[][]::new);
        return aStar.search(copyMap, snakeBodys, end, checkBoundry);
    }
    
}
