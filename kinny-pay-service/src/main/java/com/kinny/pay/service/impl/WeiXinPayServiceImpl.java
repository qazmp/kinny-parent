package com.kinny.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.kinny.pay.service.PayService;
import com.kinny.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/7/10 - 17:12
 */
@Service
public class WeiXinPayServiceImpl implements PayService {


    @Value("${pay.appid}")
    private String appId; // 公众账号ID

    @Value("${pay.partner}")
    private String mchId; // 商户号

    @Value("${pay.notifyurl}")
    private String notifyurl; // 通知地址

    @Value("${pay.partnerkey}")
    private String partnerkey; // 秘钥



    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee, String notifyurl) {
        System.err.println("---------------1-----------------------");
        // 1 封装参数
        Map<String, String> mapParam = new HashMap<>();
        mapParam.put("appid", this.appId); // 公众账号id
        mapParam.put("mch_id", this.mchId); // 商户号
        mapParam.put("nonce_str", WXPayUtil.generateNonceStr()); // 随机字符串
        //mapParam.put("sign", "") // 签名 SDK会自动生成
        mapParam.put("body", "肯尼"); // 商品的描述
        mapParam.put("out_trade_no", out_trade_no); // 外部订单号
        mapParam.put("total_fee", total_fee); // 标价金额 （分）
        mapParam.put("spbill_create_ip", "127.0.0.1"); // 终端IP
        mapParam.put("notify_url", this.notifyurl); // 通知地址
        mapParam.put("trade_type", "NATIVE"); // 交易类型
        System.err.println("---------------2-----------------------");
        // 将map集合装换为XML字符串
        try {
            String xmlParam = WXPayUtil.generateSignedXml(mapParam, this.partnerkey);
            System.err.println(this.partnerkey);
            // 2 请求访问
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setXmlParam(xmlParam); // 请求参数
            httpClient.post(); // post请求
            httpClient.setHttps(true); // HTTPS
            System.err.println("---------------3-----------------------");
            // 3 获取结果
            String xmlResult = httpClient.getContent();
            System.err.println("---------------4-----------------------");
            // 装换为map
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.err.println(mapResult);
            Map<String, Object> map = new HashMap<>();
            map.put("code_url", mapResult.get("code_url")); // 支付链接
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("---------------5-----------------------");
            return new HashMap<>();
        }
    }

    @Override
    public boolean validatePayInformation(String out_trade_no, String total_fee, String trade_status) {
        return false;
    }


    @Override
    public boolean pollTrandeIsPayment(String outTradeNo) {
        return false;
    }

    @Override
    public Map<String, String> cancelPayOrder(String out_trade_no) {
        return null;
    }
}
