package com.hch.practice.snake;

public interface GIControl {
    // 初始化，暂时未调用，在子类的实例化里调用
    void init();

    // 整体启动停止等状态调用
    void listen(GEvent event);

    // 运动方向动作调用
    void listenAction(GAction action);

    // 地图获取
    int[][] mapShow();

}
