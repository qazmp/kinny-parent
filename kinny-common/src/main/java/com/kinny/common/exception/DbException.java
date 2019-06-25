package com.kinny.common.exception;

/**
 * @author qgy
 * @create 2019/5/13 - 10:47
 */
public class DbException extends RuntimeException {

    public DbException(String message) {
        super(message);
    }
}
