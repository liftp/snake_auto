package com.hch.practice.alg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.hch.practice.snake.GAction;
import com.hch.practice.snake.GMapEle;
import com.hch.practice.snake.GSingleBody;
import com.hch.practice.snake.PathCost;

public class AStar {
    

    /**
     * 
     * @param grid      方阵，需要长宽一致
     * @param start     起点
     * @param end       终点
     * @param checkBoundry    边界校验函数
     */
    public List<GSingleBody> search(int[][] grid, LinkedList<GSingleBody> snakeBods, GSingleBody end, BiFunction<GSingleBody, GSingleBody, Boolean> checkBoundry) {

        // 保存走过的路径
        Map<String, GSingleBody> closed = new HashMap<>();
        //excludes.forEach(e -> closed.put(e.getX() + "," + e.getY(), e));

        // 每一步路径选择
        List<GSingleBody> action = new LinkedList<>();

        GSingleBody start = snakeBods.get(0);
        int g = 0;
        // cost = g + heuristic
        int f = g + calcManhattanDistance(start, end);

        PriorityQueue<PathCost> call = new PriorityQueue<>();

        call.add(new PathCost(f, g, start.getX(), start.getY()));

        boolean found = false;
        while (!found) {
            PathCost nextCall = call.poll();
            int x = nextCall.getX();
            int y = nextCall.getY();
            action.add(new GSingleBody(x, y));
            // int cost = nextCall.getCost();

            if (x == end.getX() && y == end.getY()) {
                found = true;
            } else {
                for (GAction act : GAction.values()) {
                    int x2 = x, y2 = y;
                    switch(act) {
                        case LEFT:      x2 -= 1;break;
                        case RIGHT:     x2 += 1;break;
                        case TOP:       y2 -= 1;break;
                        case BOTTOM:    y2 += 1;break;
                    }
                    // 模拟步进后是否退回，确定选择路径后不再退回
                    boolean isBack = true;
                    
                    // 路径可行且在边界中里
                    GSingleBody temp = new GSingleBody(x2, y2);
                    if (checkBoundry.apply(new GSingleBody(x, y), new GSingleBody(x2, y2))) {
                        // 模拟前进，保证snakeBods处在当前位置
                        GSingleBody backBody = simulateToNextOrBack(grid, snakeBods, x2, y2, false);
                        // 未走过的路径
                        String pos = String.format("%s,%s", x2, y2);
                        // System.out.println(pos);
                        if (!closed.containsKey(pos)) {
                            int g2 = g + 1;
                            int f2 = g2 + calcManhattanDistance(temp, end);
                            call.add(new PathCost(f2, g2, x2, y2));
                            closed.put(pos, temp);
                            // action.add(temp);
                            isBack = false;
                        }
                        // 需要回退
                        if (isBack) {
                            simulateToNextOrBack(grid, snakeBods, backBody.getX(), backBody.getY(), true);
                        }
                    }
                    
                }
            }
        }

        action.remove(0); // 去掉起点

        if (!action.isEmpty()) {
            action.forEach(System.out::println);
        }

        return found ? action : new LinkedList<>();

    }

    /**
     * 模拟前进或后退
     * @param map           地图信息
     * @param snakeBodys    snake
     * @param x             toX
     * @param y             toY
     * @param isBack        是否回退
     * @return
     */
    private GSingleBody simulateToNextOrBack(int[][] map, LinkedList<GSingleBody> snakeBodys, int x, int y, boolean isBack) {
        // isBack:false, 将尾部放到头部，前进；将头部放到尾部，回退
        GSingleBody toRun = isBack ? snakeBodys.getFirst() : snakeBodys.getLast();
        GSingleBody back = toRun.copy();
        map[toRun.getX()][toRun.getY()] = GMapEle.PASS.getVal();
        map[x][y] = GMapEle.SNAKE.getVal();
        toRun.setX(x);
        toRun.setY(y);
        if (isBack) { 
            snakeBodys.add(toRun);
        } else {
            snakeBodys.addFirst(toRun);
        }
        return back;
    }

    // 曼哈顿距离启发函数计算
    private int calcManhattanDistance(GSingleBody src, GSingleBody dest) {
        return Math.abs(dest.getX() - src.getX()) + Math.abs(dest.getY() - src.getY());
    }

    public static void main(String[] args) {
        
        //path.forEach(e -> System.out.println(String.format("路径：%s,%s", e.getX(), e.getY())));
        test();
    }

    // 测试使用
    public static void test() {
        // GMap map = new GMapImpl();
        // int[][] mapArr = map.getMap();
        // List<GSingleBody> exclude = new ArrayList<>();
        // for (int i = 0; i < 3; i++) {
        //     mapArr[i][0] = GMapEle.SNAKE.getVal();
        //     exclude.add(new GSingleBody(i, 0));
        // }
        // mapArr[20][30] = GMapEle.FOOD.getVal();
        // AStar astar = new AStar();
        
        // List<GSingleBody> path = astar.search(mapArr, new GSingleBody(2, 0), new GSingleBody(20, 30), 
        //     (curr, body) -> astar.checkBound(mapArr, null, new GSingleBody(body.getX(), body.getY())));

        // Set<String> setStr = path.stream().map(e -> String.format("%s+%s", e.getX(), e.getY())).collect(Collectors.toSet());
        // // 基于map打印矩阵
        // for (int i = 0; i < mapArr.length; i++) {
        //     StringBuilder build = new StringBuilder();
        //     for (int j = 0; j < mapArr.length; j++) {
        //         String printStr = " ";
        //         if (mapArr[i][j] == 0) {
        //             if (setStr.contains(String.format("%s+%s", i, j))) {
        //                 printStr = "*";
        //             }
        //         } else {
        //             printStr = mapArr[i][j] + "";
        //         }
        //         build.append(printStr).append(" ");
        //     }

        //     System.out.println(build.toString());
        // }
    }

    // 边界校验，测试使用
    private boolean checkBound(int[][] map, GSingleBody curr, GSingleBody next) {
        int nextX = next.getX();
        int nextY = next.getY();
        // 跨越步数大于1
        boolean skip = (Math.abs(curr.getX() - nextX) > 1 || Math.abs(curr.getY() - nextY) > 1);
        // System.out.println("条件：" + (mapShow()[x][y]));
        return nextX >= 0 && nextY >= 0 && nextX < map.length && nextY < map.length && !skip &&
            (map[nextX][nextY] == GMapEle.PASS.getVal() || map[nextX][nextY] == GMapEle.FOOD.getVal());

    }
}
