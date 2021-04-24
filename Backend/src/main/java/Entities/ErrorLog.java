package Entities;

import io.netty.handler.logging.LogLevel;

import java.io.Serializable;

public class ErrorLog implements Serializable {

    public LogLevel level;
    public String msg;

    public ErrorLog(LogLevel level, String msg) {
        this.level = level;
        this.msg = msg;
    }
}
