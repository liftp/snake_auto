package com.hch.practice.snake;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;


public interface PathSelectAlgorithm {
    

    public List<GSingleBody> getPath(GMap map, LinkedList<GSingleBody> snakeBodys, GSingleBody end, BiFunction<GSingleBody, GSingleBody, Boolean> checkBoundry);
}
