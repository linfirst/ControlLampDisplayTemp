package com.linfirst.controllampdisplaytemp;

/**
 * @author linfirst
 */
public interface OnReceivesListener {
    /**
     *返回接收到的消息
     * @param message
     */
    void success(String message);
    void failure(String message);
}
