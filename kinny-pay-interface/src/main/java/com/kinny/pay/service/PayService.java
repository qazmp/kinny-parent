package com.kinny.pay.service;

import java.util.Map;

/**
 * @author qgy
 * @create 2019/7/10 - 17:08
 */
public interface PayService {

    /**
     *  通过httpClient 远程访问微信支付平台API接口
     * @param out_trade_no 相对于微信支付平台 外部的订单号
     * @param total_fee 总金额
     * @param notifyUrl 支付宝回调地址
     * @return
     */
    public Map<String, Object> createNative(String out_trade_no, String total_fee, String notifyUrl);

    /**
     *  支付宝异步通知 验证参数 同时更新订单状态 redis 查询订单
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public boolean validatePayInformation(String out_trade_no, String total_fee, String trade_status);

    /**
     *  轮询订单 前端调用 减少与后台交互次数 减轻访问压力
     * @param outTradeNo
     * @return
     */
    public boolean pollTrandeIsPayment(String outTradeNo);

    /**
     *  取消支付平台订单
     * @param out_trade_no
     * @return
     */
    public Map<String, String> cancelPayOrder(String out_trade_no);

}
