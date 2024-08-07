package com.zhanyd.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhanyd.app.common.ApiResult;
import com.zhanyd.app.common.util.Argon2Helper;
import com.zhanyd.app.common.util.JwtUtils;
import com.zhanyd.app.model.UserInfo;
import com.zhanyd.app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;


import java.util.*;


/**
 * Created by zhanyd@sina.com on 2018/2/13 0013.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        ApiResult<Map<String, Object>> apiResult = new ApiResult<>();
        Map<String, Object> resultMap;

        // UserInfo userInfoSelected = userService.selectByUserName(userInfo.getUserName());
        UserInfo userInfoSelected = new UserInfo();
        // 判断该用户是否已经存在
        if (userInfoSelected != null) {
            // 验证密码
           /* if (Argon2Helper.verifyPassword(userInfo.getPassword(), userInfoSelected.getPassword())) {

            }*/
            if (userInfo.getPassword().equals("123456")) {
                // 生成token
                resultMap = userService.generateToken(userInfoSelected.getId(), userInfoSelected.getUserName());
                return apiResult.success(resultMap);
            } else {
                // 密码错误，拒绝登录
                logger.info("{} 用户名或密码错误", userInfo.getUserName());
                return new ApiResult(500, "用户名或密码错误", null);
            }
        } else {
            return new ApiResult(500, "该用户不存在", null);
        }
    }

    /**
     * 返回动态路由
     * @return
     */
    @GetMapping("/getAsyncRoutes")
    public ApiResult<JSONArray> getAsyncRoutes() {
        ApiResult<JSONArray> apiResult = new ApiResult<JSONArray>();
        String pagesTextBlock =
                """
                        [{
                          path: "/permission",
                          meta: {
                            title: "权限管理",
                            icon: "ep:lollipop",
                            rank: 10
                          },
                          children: [
                            {
                              path: "/permission/page/index",
                              name: "PermissionPage",
                              meta: {
                                title: "页面权限",
                                roles: ["admin", "common"]
                              }
                            },
                            {
                              path: "/permission/button/index",
                              name: "PermissionButton",
                              meta: {
                                title: "按钮权限",
                                roles: ["admin", "common"],
                                auths: [
                                  "permission:btn:add",
                                  "permission:btn:edit",
                                  "permission:btn:delete"
                                ]
                              }
                            }
                          ]
                        }]
                        """;
        // 解析JSON字符串
        JSONArray jsonArray = JSON.parseArray(pagesTextBlock);
        return apiResult.success(jsonArray);
    }


}
