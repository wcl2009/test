package cn.wcl.test.netty.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.wcl.test.netty.constants.RtCode;
import cn.wcl.test.netty.service.LoginService;

/**
 * @author L.M
 */
@Service
public class LoginServiceImpl implements LoginService {
    
    
    @Resource
    private RedisTemplate<String, Map<String, Object>> redisTemplate;
    
    @Override
    public RtCode login(Map<String, Object> parms) {
        redisTemplate.opsForValue().set(parms.get("ip").toString(), parms);
        return RtCode.SUCCESS;
    }
    
    @Override
    public boolean isLogin(String key) {
        Map<String, Object> map = redisTemplate.opsForValue().get(key);
        return !CollectionUtils.isEmpty(map);
    }
    
}
