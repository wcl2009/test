package cn.wcl.test.netty.service;

import java.util.Map;

import cn.wcl.test.netty.constants.RtCode;

/**
 * @author L.M
 */
public interface LoginService {
    
    
    /**
     * redis 记录 login info
     * 
     * @param parms
     * @return
     */
    RtCode login(Map<String, Object> parms);
    
    
    /**
     * @param key
     * @return
     */
    boolean isLogin(String key);
    
}
