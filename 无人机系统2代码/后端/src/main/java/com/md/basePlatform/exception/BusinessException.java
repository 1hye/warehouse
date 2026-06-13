package com.md.basePlatform.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;
    
    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造函数，默认错误码400
     * @param message 错误消息
     */
    public BusinessException(String message) {
        this(400, message);
    }
    
    /**
     * 创建404未找到异常
     * @param message 错误消息
     * @return BusinessException实例
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    /**
     * 创建401未授权异常
     * @param message 错误消息
     * @return BusinessException实例
     */
    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, message);
    }

    /**
     * 创建400请求错误异常
     * @param message 错误消息
     * @return BusinessException实例
     */
    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }
}