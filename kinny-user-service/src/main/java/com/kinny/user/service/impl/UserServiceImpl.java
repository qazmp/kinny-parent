package com.kinny.user.service.impl;



import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.kinny.common.exception.DbException;
import com.kinny.common.exception.SMSException;
import com.kinny.mapper.TbUserMapper;
import com.kinny.pojo.TbUser;
import com.kinny.user.service.UserService;
import com.kinny.util.PhoneFormatCheckUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author qgy
 * @create 2019/6/22 - 17:14
 */
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private Destination activeMQQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${sms.signName}")
    private String signName;

    @Value("${sms.templateCode}")
    private String templateCode;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void registry(final TbUser user, String smsCode) {

        // 控制器使用jsr303对手机号进行了校验 可以在校验
        // 密码MD5
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        // 创建时间
        user.setCreated(new Date());
        // 修改时间
        user.setUpdated(new Date());
        //检验验证码
        matchSmsCode(user, smsCode);
        int i = this.userMapper.insert(user);
        if (i != 1)
            throw new DbException("用户注册失败");

    }

    /**
     *  将redis缓存的验证码与用户输入的验证码匹配如果不通过抛出异常
     * @param user
     * @param smsCode
     */
    private void matchSmsCode(TbUser user, String smsCode) {
        String systemSmsCode = (String) this.redisTemplate.boundHashOps("smsCode").get(user.getPhone());
        if (systemSmsCode == null || !systemSmsCode.equals(smsCode)) {
            throw new SMSException("验证码错误");
        }
    }


    public void sendSmsCode(String phone) {
        boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
        if (!phoneLegal)
            throw new IllegalArgumentException("手机格式不正确");
        this.sendSmsAndCacheSmscode(phone, this.signName, this.templateCode);
    }

    /**
     *  发送消息到activemq 短信微服务调用alidayu api 发送验证码到用户手机 并把系统生成的验证码放入共有的redis
     *  可以使用expire 过期时间 string
     * @param phone
     * @param signName
     * @param templateCode
     */
    private void sendSmsAndCacheSmscode(final String phone,
                                        final String signName,
                                        final String templateCode)  {
        Random random = new Random();
        final String ran = (int)((Math.random()*9+1)*100000) + "";
        System.out.println("验证码 = " + ran);
        // 缓存手机号对应的验证码 放入共有的redis缓存
        this.redisTemplate.boundHashOps("smsCode").put(phone, ran);
        this.jmsTemplate.send(this.activeMQQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone", phone);
                mapMessage.setString("signName", signName);
                mapMessage.setString("templateCode", templateCode);
                Map<String, String> param = new HashMap<>();
                param.put("code", ran);
                mapMessage.setString("templateParam", JSON.toJSONString(param));
                return mapMessage;
            }
        });
    }
}
