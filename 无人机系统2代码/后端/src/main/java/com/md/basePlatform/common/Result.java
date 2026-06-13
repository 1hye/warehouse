package com.md.basePlatform.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 * 用于所有API接口的响应格式统一
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    
    /**
     * 状态码
     * 200-成功，201-创建成功，400-请求错误，404-未找到，500-服务器错误
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 创建成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    /**
     * 创建成功响应（自定义消息）
     * @param data 响应数据
     * @param message 自定义消息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success(T data, String message) {
        return Result.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 创建空数据成功响应
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 创建资源创建成功响应
     * @param data 创建的资源
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> created(T data) {
        return Result.<T>builder()
                .code(201)
                .message("创建成功")
                .data(data)
                .build();
    }
    
    /**
     * 创建错误响应
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> error(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
    
    /**
     * 创建400请求错误响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
    
    /**
     * 创建404未找到响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }
    
    /**
     * 创建500服务器错误响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result实例
     */
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }
}