package com.hch.practice.snake;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.WindowConstants;


import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 绘制展示类
 */
public class ShowFrame extends JFrame {
    
    // 控制器类
    private GIControl control;
    // 地图信息接口
    private GMap map;
    // 定时重绘
    private ScheduledExecutorService executor;
    // 全局事件
    private GEvent event;
    private JPanel jp;

    // 方向操作校验map
    private Map<Integer, GAction> keyMap = new HashMap<>() {
        {
            put(KeyEvent.VK_LEFT,    GAction.LEFT);
            put(KeyEvent.VK_RIGHT,   GAction.RIGHT);
            put(KeyEvent.VK_UP,      GAction.TOP);
            put(KeyEvent.VK_DOWN,    GAction.BOTTOM);
        }
    };

    // 地图元素值绑定枚举map
    private Map<Integer, GMapEle> mapEle = 
                Arrays.stream(GMapEle.values())
                .collect(Collectors.toMap(GMapEle::getVal, Function.identity()));

    public ShowFrame() {
        super();
        map = new GMapImpl();
        System.out.println("map ok");
        control = new GControlImpl(map, true);
        event = GEvent.PREPARE;

        this.setTitle("贪吃蛇");
        this.setSize(560, 600);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setVisible(true);
        jp = initPanel();
        this.add(jp);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyListen(e);
            }
        });
    }

    public JPanel initPanel() {
        JPanel panel = new Plot();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(0, 60));
        return panel;
    }

    /**
     * 定时器重绘
     */
    public void scheduleRepaint() {
        executor =  Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(jp::repaint, 150, 150, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭定时器
     */
    public void clearSchedule() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * 键盘按键监听
     * @param e 键盘事件
     */
    public void keyListen(KeyEvent e) {
        // 空格暂停/重启定时器
        if (e.getKeyCode() == KeyEvent.VK_SPACE && event != GEvent.START) {
            this.event = GEvent.START;
            control.listen(GEvent.START);
            scheduleRepaint();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE && event != GEvent.PAUSE) {
            this.event = GEvent.PAUSE;
            control.listen(GEvent.PAUSE);
            clearSchedule();
        }

        // 暂停后不能操作方向
        if (this.event == GEvent.PAUSE) return;
        // 按下操作
        // 调用反方向处理
        keyMap.computeIfPresent(e.getKeyCode(), (k,v) -> {
            System.out.println("方向：" + v.getDesc());
            control.listenAction(v);
            return  v;
        });
    }



    /**
     * 绘制图像panel
     */
    public class Plot extends JPanel {

        public Plot() {
            super();
            
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            // 画笔设置，画笔宽度
            int strokeWidth = 2;
            BasicStroke bs = new BasicStroke(strokeWidth);
            Graphics2D graph2D = (Graphics2D) g;
            graph2D.setStroke(bs);
            // 绘制大框，框颜色红
            graph2D.setColor(Color.RED);
            int marginTop = 20, marginLeft = 20, blockWidth = 10;
            //TODO 该线程应该只读map，但这里没有限制，应该优化
            int[][] mapArr = map.getMap();
            int mapLen = mapArr.length;
            // 外框宽高取map长度方形
            graph2D.drawRect(marginTop, marginLeft, mapLen * blockWidth,  mapLen * blockWidth);
            // 框内刷黑，后续避免频繁画空处
            graph2D.setColor(Color.BLACK);
            graph2D.fillRect(marginTop + strokeWidth, marginLeft + strokeWidth, 
                (mapLen * blockWidth) - strokeWidth,  (mapLen * blockWidth) - strokeWidth);
            
            // 画每一个map中的元素，默认50*50，body,wall,food...
            for (int i = 0; i < mapLen; i++) {
                for (int j = 0; j < mapLen; j++) {
                    // 黑色不画
                    if (mapArr[i][j] == GMapEle.PASS.getVal()) {
                        continue;
                    }
                    final int x = i, y = j;
                    mapEle.computeIfPresent(mapArr[i][j], (k,v) -> {
                        graph2D.setColor(v.getColor());
                        // 矩形框绘制
                        // graph2D.drawRect(marginTop * (x+1), marginLeft * (y+1), blockWidth, blockWidth);
                        // 矩形填充
                        graph2D.fillRect(marginTop + (x*blockWidth), marginLeft + (y*blockWidth), blockWidth, blockWidth);
                        // 绘制矩形
                        return v;
                    });
                }
            }

            
        }

    }
}
