package com.hch.practice.snake.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import com.hch.practice.alg.AStar;
import com.hch.practice.snake.GMap;
import com.hch.practice.snake.GSingleBody;
import com.hch.practice.snake.PathSelectAlgorithm;

public class PathSelectAlgAstarImpl implements PathSelectAlgorithm {

    @Override
    public List<GSingleBody> getPath(GMap map, LinkedList<GSingleBody> snakeBodys, GSingleBody end, BiFunction<GSingleBody, GSingleBody,  Boolean> checkBoundry) {
        AStar aStar = new AStar();
        int[][] copyMap = Arrays.stream(map.getMap())
            .map(int[]::clone).toArray(int[][]::new);
        return aStar.search(copyMap, snakeBodys, end, checkBoundry);
    }
    
}
