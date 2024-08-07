package com.zhanyd.app.service;

import com.zhanyd.app.common.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zhanyd.app.mapper.UserInfoMapper;
import com.zhanyd.app.model.UserInfo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserInfoMapper userInfoMapper;
	
	public int insertSelective(UserInfo record){
		return userInfoMapper.insertSelective(record);
	}
	
	public UserInfo selectByPrimaryKey(Integer id){
		return userInfoMapper.selectByPrimaryKey(id);
	}
	
	public UserInfo selectByUnionid(String unionid){
		return userInfoMapper.selectByUnionid(unionid);
	}

	/**
	 * 根据用户名查找用户信息
	 * @param userName
	 * @return
	 */
	public UserInfo selectByUserName(String userName) {
		return userInfoMapper.selectByUserName(userName);
	}

	/**
	 * 生成token
	 * @return
	 */
	public Map<String, Object> generateToken(Integer userId, String userName) {
		Map<String, Object> resultMap = new HashMap<>();
		String token = JwtUtils.signJWT(userId);
		if (token == null) {
			resultMap.put("msg", "生成token失败");
			return resultMap;
		} else {
			logger.info("{} 登录成功", userName);
			resultMap.put("username", userName);
			resultMap.put("roles", new String[]{"admin"});
			resultMap.put("accessToken", token);
			resultMap.put("refreshToken", "");
			LocalDateTime currentTime = LocalDateTime.now();
			resultMap.put("expires", currentTime.plusDays(7));
			return resultMap;
		}
	}


}
