package xmu.oomall.controller;

import common.oomall.api.CommonResult;
import common.oomall.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import xmu.oomall.domain.MallAdmin;
import xmu.oomall.domain.MallRole;
import xmu.oomall.domain.MallUser;
import xmu.oomall.service.AdminService;
import xmu.oomall.service.RoleService;
import xmu.oomall.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liznsalt
 */
@RestController
@RequestMapping("/userInfoService")
public class UserControllerImpl {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;

    private Map<String, String> getTokenMap(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            return null;
        }
        return JwtTokenUtil.getMapFromToken(token);
    }

    // 内部接口

    /**
     * 修改用户返点
     * @param rebate 返点
     * @return 修改结果
     */
    @PutMapping("/user/rebate")
    public Object updateUserRebate(@RequestParam Integer userId,
                                   @RequestParam Integer rebate,
                                   HttpServletRequest request) {
        if (userId == null) {
            return CommonResult.badArgument("没有传入userId");
        }
        return userService.updateRebate(userId, rebate);
    }

    // 管理员

    /**
     * 获取管理员列表
     * @return 管理员列表
     */
    @GetMapping("/admin")
    public Object adminList() {
        return CommonResult.success(adminService.list());
    }

    /**
     * 创建一个管理员
     *
     * @param admin 管理员信息
     * @return 新增的admin对象
     */
    @PostMapping("/admin")
    public Object createAdmin(@RequestBody MallAdmin admin) {
        MallAdmin newAdmin = adminService.add(admin);
        if (newAdmin == null) {
            return CommonResult.failed("已经存在此管理员名字");
        } else {
            newAdmin.setPassword(null);
            return CommonResult.success(newAdmin);
        }
    }

    /**
     * 查看管理员信息
     *
     * @param id 管理员的id
     * @return admin对象
     */
    @GetMapping("/admin/{id}")
    public Object getAdminInfo(@PathVariable Integer id) {
        MallAdmin admin = adminService.findById(id);
        admin.setPassword(null);
        return CommonResult.success(admin);
    }

    /**
     * 修改管理员信息
     *
     * @param id 管理员id
     * @param admin 新的管理员信息
     * @return 修改后的admin对象
     */
    @PutMapping("/admin/{id}")
    public Object updateAdmin(@PathVariable Integer id, @RequestBody MallAdmin admin) {
        MallAdmin newAdmin = adminService.update(id, admin);
        newAdmin.setPassword(null);
        return CommonResult.success(newAdmin);
    }

    /**
     * 删除某个管理员
     *
     * @param id 管理员id
     * @return 删除结果
     */
    @DeleteMapping("/admin/{id}")
    public Object delete(@PathVariable Integer id) {
        return CommonResult.success(adminService.delete(id));
    }

    /**
     * 管理员查看自己的信息
     * @return admin对象
     */
    @GetMapping("/admins/info")
    public Object adminInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            return CommonResult.unauthorized(null);
        }
        // 解析token，得到用户的id和role
        Map<String, String> tokenMap = JwtTokenUtil.getMapFromToken(token);
        if (tokenMap == null) {
            return CommonResult.unauthorized(null);
        }
        Integer adminId = Integer.valueOf(tokenMap.get(JwtTokenUtil.CLAIM_KEY_USERID));

        MallAdmin admin = adminService.findById(adminId);
        admin.setPassword(null);
        return CommonResult.success(admin);
    }

    /**
     * 管理员根据请求信息登陆
     * @return admin对象
     */
    @PostMapping("/admins/login")
    public Object adminLogin(@RequestParam String username,
                             @RequestParam String password) {
        String token = adminService.login(username, password);
        if (token == null) {
            return CommonResult.failed("账号或密码不正确");
        }
        Map<String, Object> map = new HashMap<>(5);
        map.put("token", token);
        map.put("tokenHead", tokenHead);
        MallAdmin admin = adminService.findByName(username);
        admin.setPassword(null);
        map.put("data", admin);
        return CommonResult.success(map);
    }

    /**
     * 管理员注销
     * @return admin对象
     */
    @PostMapping("/admins/logout")
    public Object adminLogOut() {
        return CommonResult.success(null);
    }

    /**
     * 管理员修改密码
     * @param admin admin对象
     * @return admin对象
     */
    @PutMapping("/admin/password")
    public Object updateAdminPassword(@RequestBody MallAdmin admin) {
        // FIXME
        MallAdmin newAdmin = adminService.update(admin.getId(), admin);
        newAdmin.setPassword(null);
        return CommonResult.success(newAdmin);
    }


    /**
     * 管理员查看所有角色
     * @return role的列表
     */
    @GetMapping("/roles")
    public Object roleList() {
        return CommonResult.success(roleService.getAllRoles());
    }

    /**
     * 管理员新建角色
     * @param role role实例
     * @return role的实例
     */
    @PostMapping("/roles")
    public Object addRole(@RequestBody MallRole role) {
        return CommonResult.success(roleService.insert(role));
    }

    /**
     * 查看单个角色的详细信息
     * @param id role的id
     * @return role实例
     */
    @GetMapping("/roles/{id}")
    public Object getRole(@PathVariable("id") Integer id) {
        return CommonResult.success(roleService.findById(id));
    }

    /**
     * 修改某个role的信息
     * @param id role的id
     * @param role role实例
     * @return 修改后的role
     */
    @PutMapping("/roles/{id}")
    public Object updateRole(@PathVariable("id") Integer id, @RequestBody MallRole role) {
        role.setId(id);
        return CommonResult.success(roleService.update(role));
    }

    /**
     * 删除某个role
     * @param id role的id
     * @return 删除结果
     */
    @DeleteMapping("/roles/{id}")
    public Object deleteRole(@PathVariable("id") Integer id) {
        return CommonResult.success(roleService.deleteById(id));
    }

    /**
     * 修改role权限
     * @param id role的id
     * @return 没有permission表，暂时返回role
     */
    @PutMapping("/roles/{id}/permission")
    public Object updateRolePermission(@PathVariable("id") Integer id) {
        // FIXME
        return CommonResult.success(roleService.getPrivilegesByRoleId(id));
    }

    /**
     * 返回一个权限对应的管理员
     * @param id 权限的id
     * @return 管理员的列表
     */
    @GetMapping("/roles/{id}/admins")
    public Object getAdmin(@PathVariable("id") Integer id) {
        return CommonResult.success(roleService.findAdmins(id));
    }


    // 用户

    /**
     * 请求验证码
     *
     * 这里需要一定机制防止短信验证码被滥用
     * @return 验证码captcha
     */
    @PostMapping("/captcha")
    public Object captcha(@RequestParam String telephone) {
        return userService.generateAuthCode(telephone);
    }

    /**
     * 用户账号登录
     * @return 数据是userInfoVo
     */
    @PostMapping("/login")
    public Object userLogin(@RequestParam String username,
                            @RequestParam String password) {
        String token = userService.login(username, password);
        if (token == null) {
            return CommonResult.badArgumentValue("用户名或密码错误");
        }
        Map<String, Object> tokenMap = new HashMap<>(5);
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        MallUser user = userService.findByName(username);
        tokenMap.put("data", user);
        return CommonResult.success(tokenMap);
    }

    /**
     * 账号密码重置
     *
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PutMapping("/password")
    public Object reset(@RequestParam String telephone,
                        @RequestParam String password,
                        @RequestParam String authCode) {
        return userService.updatePassword(telephone, password, authCode);
    }

    /**
     * 账号手机号码重置
     *
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PutMapping("/phone")
    public Object resetPhone(@RequestParam String password,
                             @RequestParam String telephone,
                             @RequestParam String code) {
        return userService.updateTelephone(telephone, password, code);
    }

    /**
     * 请求注册验证码
     *
     * 这里需要一定机制防止短信验证码被滥用
     * param body 包括phoneNumber,captchaType
     * @return 验证码
     */
    @PostMapping("/regCaptcha")
    public Object registerCaptcha(@RequestParam String telephone) {
        return userService.generateAuthCode(telephone);
    }

    /**
     * 用户账号注册
     *
     *     请求内容
     *                {
     *                username: xxx,
     *                password: xxx,
     *                mobile: xxx
     *                wxCode: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 注册结果
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * token: xxx,
     * tokenExpire: xxx,
     * userInfo: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("/register")
    public Object register(@RequestBody MallUser user,
                           @RequestParam String code,
                           HttpServletRequest request) {
        return userService.register(user, code);
    }


    /**
     * 用户个人页面数据
     *
     * 目前是用户订单统计信息
     * 具体的userId去解析token获得
     * @return 用户个人页面数据userInfoVo
     */
    @GetMapping("/user")
    public Object userInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null) {
            return CommonResult.unauthorized(null);
        }
        // 解析token，得到用户的id和role
        Map<String, String> tokenMap = JwtTokenUtil.getMapFromToken(token);
        if (tokenMap == null) {
            return CommonResult.unauthorized(null);
        }
        Integer userId = Integer.valueOf(tokenMap.get(JwtTokenUtil.CLAIM_KEY_USERID));

        MallUser user = userService.findById(userId);
        return CommonResult.success(user);
    }
}
