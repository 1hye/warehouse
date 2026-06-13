package com.md.basePlatform.config.shiro;

import com.md.basePlatform.domain.User;
import com.md.basePlatform.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * 自定义 Shiro Realm — 用户认证的核心实现
 *
 * Realm 是 Shiro 中连接应用与安全数据的桥梁，相当于一个"数据源代理"。
 * Shiro 通过 Realm 获取用户信息并完成身份验证。
 *
 * 本 Realm 实现了以下流程：
 * 1. 接收用户输入的用户名和密码
 * 2. 从数据库（UserMapper）中查询用户
 * 3. 验证用户状态（是否被禁用）
 * 4. MD5 加密比对密码是否一致
 * 5. 返回认证信息给 Shiro 框架
 *
 * 密码加密规则：MD5("baseplatform" + 原始密码 + "md5salt")
 * 数据库中存储的也是此规则加密后的密文。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomRealm extends AuthorizingRealm {

    /**
     * 用户数据访问接口，用于从数据库查询用户信息
     */
    private final UserMapper userMapper;

    /**
     * 授权方法 — 获取用户的角色和权限信息
     *
     * 当前系统采用 URL 级过滤控制（Shiro 过滤器链），
     * 暂未启用细粒度的角色/权限注解控制，因此返回空授权信息。
     *
     * 如果后续需要实现"管理员只能管理某些无人机"之类的功能，
     * 可以在此方法中从数据库加载用户的角色和权限列表。
     *
     * @param principals 认证主体集合，包含用户身份信息
     * @return 授权信息（当前为空）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }

    /**
     * 认证方法 — 验证用户登录身份
     *
     * 处理流程（逐步骤说明）：
     *
     * 步骤 1 — 获取用户输入
     *   从 AuthenticationToken 中提取用户名和密码。
     *   UsernamePasswordToken 是 Shiro 内置的实现，对应登录表单提交的数据。
     *
     * 步骤 2 — 查询用户
     *   通过 UserMapper.selectByUsername() 从数据库查询用户信息。
     *   如果用户不存在，抛出 UnknownAccountException（统一由 AuthController 捕获处理）。
     *
     * 步骤 3 — 检查用户状态
     *   验证 status 字段是否为 1（启用状态）。
     *   如果为 0 或 null，抛出 DisabledAccountException。
     *
     * 步骤 4 — 验证密码
     *   使用相同的加密规则（MD5 + 盐值）对用户输入的密码进行加密，
     *   然后与数据库中的密文进行比对。
     *   不一致则抛出 IncorrectCredentialsException。
     *
     * 步骤 5 — 返回认证信息
     *   验证通过后，将用户对象和密码返回给 Shiro 框架，
     *   Shiro 会将此信息存入 Subject 中，供后续使用。
     *
     * @param token 认证令牌，由 Shiro 框架在调用 Subject.login() 时传入
     * @return 认证通过后返回的用户信息
     * @throws AuthenticationException 认证失败时抛出对应的异常子类
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // ── 步骤 1：解析令牌 ──
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());
        log.info("Shiro 认证: username={}", username);

        // ── 步骤 2：查询用户 ──
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("用户名或密码错误");
        }

        // ── 步骤 3：检查状态 ──
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new DisabledAccountException("账号已被禁用");
        }

        // ── 步骤 4：密码加密比对 ──
        String encryptedPassword = encryptPassword(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new IncorrectCredentialsException("用户名或密码错误");
        }

        // ── 步骤 5：认证成功 ──
        log.info("Shiro 认证成功: id={}, username={}", user.getId(), user.getUsername());

        /*
         * SimpleAuthenticationInfo 的参数说明：
         *   第 1 个参数 — principal（主体）：存入 Session，后续可通过
         *                 SecurityUtils.getSubject().getPrincipal() 获取
         *   第 2 个参数 — credentials（凭证）：用户输入的原始密码
         *   第 3 个参数 — realmName：当前 Realm 的名称，用于区分多个 Realm
         */
        return new SimpleAuthenticationInfo(user, password, getName());
    }

    /**
     * 密码加密方法
     *
     * 加密规则：MD5("baseplatform" + password + "md5salt")
     *
     * 盐值说明：
     *   "baseplatform"   — 项目前缀，防止与其他系统使用相同哈希
     *   password         — 用户输入的原始密码
     *   "md5salt"        — 固定盐值字符串，增加破解难度
     *
     * 此规则必须与 data.sql 中初始化管理员密码的加密规则完全一致。
     *
     * @param password 用户输入的原始明文密码
     * @return 32 位小写 MD5 密文字符串
     * @throws AuthenticationException 如果加密过程中发生异常
     */
    private String encryptPassword(String password) {
        try {
            return DigestUtils.md5DigestAsHex(("baseplatform" + password + "md5salt").getBytes("UTF-8"));
        } catch (Exception e) {
            throw new AuthenticationException("密码加密失败", e);
        }
    }
}