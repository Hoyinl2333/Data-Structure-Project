package com.codex.taxitrajectory.utils;

/**
 * 自定义异常类，用于处理出租车数据加载过程中的异常
 */
public class TaxiDataLoadException extends RuntimeException {
    public TaxiDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}