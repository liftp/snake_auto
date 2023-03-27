package com.hch.practice;

import javax.swing.JFrame;

import com.hch.practice.snake.ShowFrame;

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
