package com.hch.practice.snake.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.hch.practice.snake.GAction;
import com.hch.practice.snake.GEvent;
import com.hch.practice.snake.GIControl;
import com.hch.practice.snake.GMap;
import com.hch.practice.snake.GMapEle;
import com.hch.practice.snake.GSingleBody;
import com.hch.practice.snake.PathSelectAlgorithm;

public class GControlImpl implements GIControl {
    
    // 运动方向处理
    private GAction action;

    // 运动处理定时任务
    private ScheduledExecutorService executor;

    private GSingleBody foodPos;        // 食物位置

    private int snakeLen = 3;           // 默认长度

    private GEvent event;               // 当前状态

    private LinkedList<GSingleBody> snakeBodys;

    private int velicity = 300;         // 每个位置移动的毫秒数

    private long lastTime;              // 定时任务使用计时

    private long lastActionTime;        // 动作使用计时

    private PathSelectAlgorithm algorithm;  // 自动寻路算法
    
    private GMap map;                   // 地图信息接口

    private boolean isAuto;             // 自动寻迹开关

    private List<GSingleBody> autoPath; // 自动寻路结果

    // 初始化反方向对象
    private Map<GAction, GAction> reverse = new HashMap<>(4) {{
        put(GAction.LEFT, GAction.RIGHT);
        put(GAction.RIGHT, GAction.LEFT);
        put(GAction.TOP, GAction.BOTTOM);
        put(GAction.BOTTOM, GAction.TOP);
    }};
    
        

    public GControlImpl(boolean isAuto) {
        this.map = new GMapImpl();
        this.isAuto = isAuto;
        if (isAuto) velicity = 100; // 自动寻路速度较快
        init();
        algorithm = new PathSelectAlgAstarImpl();
        System.out.println("control ok");
    }

    @Override
    public void init() {
        // 获取地图
        int[][] mapArr = mapShow();
        
        // 准备snake数据
        snakeBodys = new LinkedList<>();
        for (int i = 0; i < snakeLen; i++) {
            // snake列表
            snakeBodys.addFirst(new GSingleBody(i, 0, 2));
            // 设置snake占用数组
            mapArr[i][0] = GMapEle.SNAKE.getVal(); 
        }
        System.out.println("snake prepare ok ");
        // map.setMap(mapArr);
        // 随机食物位置
        makeFood();
        // 准备事件
        listen(GEvent.PREPARE);
        // 开始方向
        action = GAction.RIGHT;
        
    }

    @Override
    public void listen(GEvent event) {
        // 防止重复操作
        if (this.event == event) {
            return;
        }
        // 事件的修改可能多线程操作，被control的定时任务与ShowFrame的JPanel#repain
        synchronized(this) {
            this.event = event;
            // 事件监听处理 启动，结束，暂停，寻迹
            if (event == GEvent.START) {
                resetSchedual();
            } else if (event == GEvent.END || event == GEvent.PAUSE) {
                clearSchedule();
            }
        }
        

    }

    /**
     * 开启定时任务进行步进处理
     */
    public void resetSchedual() {
        executor =  Executors.newSingleThreadScheduledExecutor();
        lastTime = System.currentTimeMillis();
        // 实际上定时任务的延时和velocity同时控制移动速度
        executor.scheduleAtFixedRate(this::runAction, 100, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭定时任务
     */
    public void clearSchedule() {
        // 停止运动清除定时器
        if (executor != null ) {
            executor.shutdown();
            executor = null;
        }
    }

    /**
     * 实际上运动动作调用，被定时器直接调用
     */
    public void runAction() {

        long thisTime = System.currentTimeMillis();
        if (GEvent.PAUSE == event) {
            // 暂停跳过定时任务处理
            lastTime = thisTime;
            System.out.println("暂停");
        } else if ((thisTime - lastTime) >= velicity) {
            // 时间修改
            lastTime = thisTime;
            // 运动一个位置
            moveOrAuto();
        }
    }

    // 运动边界校验 
    public boolean checkBoundary(int mapLen, GSingleBody curr, GSingleBody next) {
        int nextX = next.getX();
        int nextY = next.getY();
        // 跨越步数大于1，这个主要是对自动寻迹校验
        boolean skip = (Math.abs(curr.getX() - nextX) > 1 || Math.abs(curr.getY() - nextY) > 1);
        // System.out.println("条件：" + (mapShow()[x][y]));
        return nextX >= 0 && nextY >= 0 && nextX < mapLen && nextY < mapLen && !skip &&
            (mapShow()[nextX][nextY] == GMapEle.PASS.getVal() || mapShow()[nextX][nextY] == GMapEle.FOOD.getVal() || mapShow()[nextX][nextY] > 3);
    }

    @Override
    public void listenAction(GAction act) {
        // 非正常状态不操作
        if (event != GEvent.START) {
            System.out.println("跳走");
            return;
        }
        
        // 不能反方向运动 触发则跳过操作
        System.out.println(String.format("方向将要改变: %s", action.getDesc()));
        if (this.action == reverse.get(act)) return;
        // 不能频繁改变方向,150ms改变时长
        long thisTime = System.currentTimeMillis();
        if ((thisTime - lastActionTime) >= 150) {
            synchronized(this) {
                this.action = act;
            }
            lastActionTime = thisTime;
        }
    }

    public void pathSelect() {
        // 执行计划路径
        // copy用于模拟行走路线
        LinkedList<GSingleBody> copy = snakeBodys.stream().map(GSingleBody::copy).collect(Collectors.toCollection(LinkedList::new));
        autoPath = algorithm.getPath(map, copy, foodPos, 
            (curr, next) -> checkBoundary(map.getMap().length, curr, next));
        // 在map上画一次踪迹路径方向，6是设置方向位置基数 +1 为right，-1为left，-2为top,+2为bottom
        autoPath.stream().limit(autoPath.size() - 1).forEach(e -> mapShow()[e.getX()][e.getY()] = e.getDirection() + 6);
        if (autoPath.isEmpty()) {
            isAuto = false;
            System.out.println("关闭自动寻迹");
        }
    }

    // 添加自动寻迹逻辑
    public void moveOrAuto() {
        if (isAuto) {
            // 一次寻路执行完成，重新调用
            if (autoPath == null || autoPath.isEmpty()) {
                pathSelect();
            }
            GSingleBody next = autoPath.remove(0);
            moveToDest(snakeBodys.getFirst(), next.getX(), next.getY());
            if (autoPath.size() == 0) {
                //多向目标方向移动一个
            }
        } else {
            move();
        }

    }

    // 按照方向运行一个单位
    public void move() {
        int x,y;
        GSingleBody head = snakeBodys.getFirst();
        x = head.getX();
        y = head.getY();
        switch(action) {
            case LEFT:      x -= 1;break;
            case RIGHT:     x += 1;break;
            case TOP:       y -= 1;break;
            case BOTTOM:    y += 1;break;
        }
        System.out.println("move direction:" + action.getDesc());
        moveToDest(head, x, y);
    }

    private void moveToDest(GSingleBody head, int x, int y) {
        int[][] map = mapShow();
        // 方向计算
        GAction act = direction(head, new GSingleBody(x, y));
        // 运行前校验，通过后继续运行，否则结束
        if (checkBoundary(map.length, head, new GSingleBody(x, y))) {
            // 如果有食物，将食物放到最前面，否则取最后一个放到头部
            if (foodPos.getX() == x && foodPos.getY() == y) {
                map[x][y] = GMapEle.SNAKE.getVal();
                foodPos.setDirection(act.getDirection());
                snakeBodys.addFirst(foodPos);
                // 食物转body
                map[foodPos.getX()][foodPos.getY()] = GMapEle.SNAKE.getVal();
                // 继续随机产生食物
                makeFood();
                System.out.println("吃到食物");
            } else {
                GSingleBody last = snakeBodys.getLast();
                // 旧body画布清除,只清理最后一格
                map[last.getX()][last.getY()] = GMapEle.PASS.getVal();
                // 弹出最后一个放到第一个位置，减少移动计算及对象产生，即蛇的移动处理
                last.setX(x);
                last.setY(y);
                last.setDirection(act.getDirection());
                map[x][y] = GMapEle.SNAKE.getVal();
                snakeBodys.addFirst(snakeBodys.pollLast());
            }
            System.out.println(String.format("步进, snake长度 %s, x:%s, y:%s", snakeBodys.size(), x,y));
        } else {
            System.out.println("停止");
            listen(GEvent.END);
        }
    }
    // 添加食物
    public void makeFood() {
        int[][] map = mapShow();
        int x = 0,y = 0;
        while (true) {
            Random rand = new Random();
            x = (int) (rand.nextFloat() * map.length);
            y = (int) (rand.nextFloat() * map.length);
            if (map[x][y] == GMapEle.PASS.getVal()) {
                foodPos = new GSingleBody(x, y);
                map[x][y] = GMapEle.FOOD.getVal();
                break;
            }
        }
        System.out.println(String.format("makeFood x: %s, y: %s", x, y));
        
    }

    /**
     * 根据下一步位置和上一步位置算出方向
     * @param last      上一步
     * @param next      下一步
     * @return          方向枚举
     */
    private GAction direction(GSingleBody last, GSingleBody next) {
        if ((next.getX() - last.getX()) == 1) {
            return GAction.BOTTOM;
        } else if ((next.getX() - last.getX()) == -1) { 
            return GAction.TOP;
        } else if ((next.getY() - last.getY()) == 1) { 
            return GAction.RIGHT;
        } else {
            return GAction.LEFT;
        }
    }

    @Override
    public int[][] mapShow() {
        return map.getMap();
    }

}
