package com.hch.practice.snake;

public enum GEvent {
    PREPARE("prepare"),
    START("start"),
    END("end"),
    PAUSE("pause");

    private String content;

    GEvent(String content) {
        this.content = content;
    }

    private String getContent() {
        return this.content;
    }
}
