package com.hch.practice.snake;

public interface GIControl {
    
    void init();

    void listen(GEvent event);

    void listenAction(GAction action);

    int[][] mapShow();

    int bodyLen();

}
