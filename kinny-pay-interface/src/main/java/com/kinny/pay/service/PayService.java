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
     * @return
     */
    public Map<String, Object> createNative(String out_trade_no, String total_fee);

}
