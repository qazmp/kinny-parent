package com.kinny.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.kinny.pay.service.PayService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/7/12 - 16:51
 */
@Service
public class ZhiFuBaoPayServiceImpl implements PayService {

    private static Log log = LogFactory.getLog(ZhiFuBaoPayServiceImpl.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("properties/zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }

    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee) {
// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = out_trade_no;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "肯尼";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = (Double.parseDouble(total_fee) / 100) + "";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "共" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress);
                //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                //.setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                String body1 = response.getBody();
                Map map = JSON.parseObject(body1, Map.class);
                JSONObject alipay_trade_precreate_response = (JSONObject) map.get("alipay_trade_precreate_response");
                String qr_code = alipay_trade_precreate_response.getString("qr_code");
                Map<String, Object> map3 = new HashMap<>();
                map3.put("code_url", qr_code); // 支付链接
                map3.put("out_trade_no", out_trade_no);
                map3.put("total_fee", total_fee);
                return map3;
                // 需要修改为运行机器上的路径
                //String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",
                        //response.getOutTradeNo());
                //log.info("filePath:" + filePath);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return new HashMap<>();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                       response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }
}
