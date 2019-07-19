package com.kinny.seckill.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.kinny.common.exception.TradeException;
import com.kinny.pay.service.PayService;
import com.kinny.pojo.tbSeckillOrder;
import com.kinny.seckill.service.SeckillOrderService;
import com.kinny.vo.ResponseVo;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author qgy
 * @create 2019/7/18 - 10:30
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 10000)
    private PayService zhiFuBaoPayServiceImpl;

    @Reference
    private SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${notifyUrl}")
    private String notifyUrl;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("properties/zfbinfo.properties");
    }

    /**
     *  调用支付平台预下单接口 返回二维码支付链接
     * @return
     */
    @RequestMapping("/createNative")
    public ResponseVo<Map<String, Object>> createNative() {
        System.out.println(this.notifyUrl);
        // 1 获取当前主体
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        // 2 从redis中获取秒杀订单 包含外部订单号及支付金额
        tbSeckillOrder seckillOrder = (tbSeckillOrder) this.redisTemplate.boundHashOps("seckillOrderList").get(principal);
        // 3 调用支付品牌预下单
        try {
            Map<String, Object> aNative = this.zhiFuBaoPayServiceImpl.createNative(seckillOrder.getId() + "", ((long) seckillOrder.getMoney().longValue() * 100) + "", this.notifyUrl);
            return new ResponseVo<>(true, aNative);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "下单失败");
        }
    }

    @RequestMapping("/alipayCallBack")
    public Object alipayCallBack(HttpServletRequest request) {
        // 获取当前实体
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
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

        boolean b = this.zhiFuBaoPayServiceImpl.validatePayInformation(params.get("out_trade_no"), total_amount, params.get("trade_status"));
        if (b) {
            // 流水号放入redis 该方法无法获得principal
            this.redisTemplate.boundValueOps(params.get("out_trade_no")).set(params.get("trade_no"));
            return "success";
        }else {
            return "failure";
        }

    }

    @RequestMapping("/pollOutTradeIsPayment")
    public ResponseVo pollOutTradeIsPayment(String outTradeNo) {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        int i = 0;
        while(true) {
            boolean b = false;
            try {
                b = this.zhiFuBaoPayServiceImpl.pollTrandeIsPayment(outTradeNo);
            } catch (TradeException e) {
                return new ResponseVo(false, e.getMessage());
            }
            if (b) {
                String trade_no = (String) this.redisTemplate.boundValueOps(outTradeNo).get();
                this.seckillOrderService.updateSeckillOrderDuarbilityCacheToDatabase(principal, trade_no);
                return new ResponseVo(true, "用户已支付");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;

            if(i >= 6) {
                // 秒杀频道 订单超时 删除订单 并恢复库存 取消支付平台订单
                Map<String, String> map = this.zhiFuBaoPayServiceImpl.cancelPayOrder(outTradeNo);
                if("Success".equals(map.get("msg"))) {
                    this.seckillOrderService.deleteOrderAndResetStock(principal, outTradeNo);
                    return new ResponseVo(false, "超时");
                }else {
                    String trade_no = (String) this.redisTemplate.boundValueOps(outTradeNo).get();
                    this.seckillOrderService.updateSeckillOrderDuarbilityCacheToDatabase(principal, trade_no);
                    return new ResponseVo(true, "用户已支付");
                }

            }

        }
    }


}
