package com.md.basePlatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户数据传输对象（DTO）
 * 用于用户注册时接收前端提交的信息，以及登录成功后返回用户信息给前端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /** 用户 ID（新增时为空） */
    private Long id;

    /** 用户名，3-50字符 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度3-50字符")
    private String username;

    /** 密码，6-50字符（注册时必填，返回时忽略） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度6-50字符")
    private String password;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private String updateTime;
}