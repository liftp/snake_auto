package com.hch.practice.snake;

import javax.swing.JFrame;
import java.awt.EventQueue;

public class App {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            initFrame();
        });
    }


    public static JFrame initFrame() {
        ShowFrame frame = new ShowFrame();
        
        return frame;
    }
}
