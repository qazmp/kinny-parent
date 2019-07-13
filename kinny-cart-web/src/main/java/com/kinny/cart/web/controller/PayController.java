package com.kinny.cart.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.kinny.common.exception.TradeException;
import com.kinny.pay.service.PayService;
import com.kinny.util.IdWorker;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author qgy
 * @create 2019/7/11 - 9:37
 */
@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference(timeout = 6000)
    private PayService zhiFuBaoPayServiceImpl;

    @Value("${alipay_public_key}")
    private String alipayPublicKey;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("properties/zfbinfo.properties");
    }

    @RequestMapping("/createNative")
    public Map<String, Object> createNative() {
        // 获取当前实体
        String principal = getPrincipal();
        IdWorker idWorker = new IdWorker();
        Map<String, Object> aNative = null;
        try {
            aNative = this.zhiFuBaoPayServiceImpl.createNative(idWorker.nextId() + "", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aNative;
    }

    @RequestMapping("/alipayCallBack")
    public Object alipayCallBack(HttpServletRequest request) {
        // 获取当前实体
        String principal = getPrincipal();
        Map<String, String> params = new HashMap<>();
        // 获取参数 多参数 用,分割
        Map<String, String []> parameterMap = request.getParameterMap();
        Set<String> keySet = parameterMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String val = new String();
            String param = iterator.next();
            String[] values = parameterMap.get(param);
            for (int i = 0; i < values.length; i++) {
               val = i == values.length - 1? val + values[i]: val + values[i] + ",";
            }
            params.put(param, val);
        }

        params.remove("sign_type");
        System.out.println("params = " + params);

        try {
            boolean b = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "UTF-8", Configs.getSignType());
            System.out.println("b = " + b);
            if (!b)
                return new ResponseVo<>(false, "非法请求， 不予受理");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "failure";
        }
        String total_amount = params.get("total_amount");
        total_amount = (Double.parseDouble(total_amount) * 100) + "";

        boolean b = this.zhiFuBaoPayServiceImpl.validatePayInformation(params.get("out_trade_no"), total_amount);
        System.out.println("b = " + b);
        if (b) {
            return "success";
        }else {
            return "failure";
        }

    }

    private String getPrincipal() {
        return (String) SecurityUtils.getSubject().getPrincipal();
    }

    @RequestMapping("/pollOutTradeIsPayment")
    public ResponseVo pollOutTradeIsPayment(String outTradeNo) {
        int i = 0;
        while(true) {
            boolean b = false;
            try {
                b = this.zhiFuBaoPayServiceImpl.pollTrandeIsPayment(outTradeNo);
            } catch (TradeException e) {
                return new ResponseVo(false, e.getMessage());
            }
            if (b)
                return new ResponseVo(true, "用户已支付");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;

            if(i >= 100) {
                // 将redis中 订单删除

                return new ResponseVo(true, "用户未支付");
            }

        }
    }




}
