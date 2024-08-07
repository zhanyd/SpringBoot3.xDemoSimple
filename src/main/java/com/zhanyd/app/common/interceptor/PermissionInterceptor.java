package com.zhanyd.app.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.zhanyd.app.common.ApiResult;
import com.zhanyd.app.common.util.JwtUtils;
import com.zhanyd.app.common.util.StringHelp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by zhanyd@sina.com on 2018/2/16 0016.
 */
public class PermissionInterceptor implements HandlerInterceptor {
    /**
     * 判断是否登录
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        System.out.println("in preHandle");
        // 从请求头中提取Token
        String token = extractToken(request);
        System.out.println(request.getRequestURI());
        if(StringHelp.isEmpty(token)){
            responseMessage(response,"token不能为空");
            return false;
        }

        Integer result = JwtUtils.verifyJWT(token);
        if(result == null){
            responseMessage(response,"token无效或已过期");
            return false;
        }
        return true;
    }

    /**
     * 从请求头中提取Token去掉最前面的Bearer
     * @param request
     * @return
     */
    public String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if(authorization != null) {
            if(authorization.startsWith("Bearer ")) {
                return authorization.substring(7);
            } else {
                return authorization;
            }
        } else {
            return null;
        }
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


    /**
     * 发送消息给客户端
     * @param response
     * @param msg
     * @throws IOException
     */
    private void responseMessage(HttpServletResponse response,String msg) throws IOException
    {
        ApiResult<String> apiResult = new ApiResult<String>();
        String message = JSON.toJSONString(apiResult.tokenExpired(msg));
        response.setHeader("Content-type", "application/json");
        OutputStream stream = response.getOutputStream();
        stream.write(message.getBytes("UTF-8"));
        stream.flush();
        stream.close();
    }
}
