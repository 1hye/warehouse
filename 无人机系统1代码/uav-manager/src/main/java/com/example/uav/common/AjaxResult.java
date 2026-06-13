package com.example.uav.common;

import java.util.HashMap;

/**
 * 统一响应结果
 */
public class AjaxResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";
    /** 返回内容 */
    public static final String MSG_TAG = "msg";
    /** 数据对象 */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 AjaxResult 对象
     */
    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     */
    public static AjaxResult success() {
        return success("操作成功");
    }

    /**
     * 返回成功数据
     */
    public static AjaxResult success(Object data) {
        return success("操作成功", data);
    }

    /**
     * 返回成功消息
     */
    public static AjaxResult success(String msg) {
        return success(msg, null);
    }

    /**
     * 返回成功消息
     */
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(0, msg, data);
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult error() {
        return error("操作失败");
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult error(String msg) {
        return AjaxResult.error(500, msg);
    }

    /**
     * 返回错误消息
     */
    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg, null);
    }

    @Override
    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
