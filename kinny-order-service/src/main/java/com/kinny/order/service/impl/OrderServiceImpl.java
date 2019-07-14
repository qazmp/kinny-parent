package com.kinny.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.CacheEnum;
import com.kinny.common.pojo.em.OrderEnum;
import com.kinny.mapper.TbOrderItemMapper;
import com.kinny.mapper.TbOrderMapper;
import com.kinny.mapper.TbPayLogMapper;
import com.kinny.order.service.OrderService;
import com.kinny.pojo.TbOrder;
import com.kinny.pojo.TbOrderItem;
import com.kinny.pojo.TbPayLog;
import com.kinny.pojo.group.CarGroup;
import com.kinny.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

/**
 * @author qgy
 * @create 2019/7/9 - 11:10
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void addOrderAndClear(TbOrder order) {
        // 参数非空校验
        if(order == null) {
            throw new IllegalArgumentException("用户的订单列表为空");
        }

        // 获取远程购物车
        String principal = order.getUserId();
        List<CarGroup> cartList = (List<CarGroup>) this.redisTemplate.boundHashOps(CacheEnum.CART_REMOTE_HASH.code).get(principal);
        // 新增订单
        Map<String, Object> map = this.populateOderListAndDuarbility(order, cartList, principal);

        // 创建持久化支付日志
        TbPayLog payLog = populateDuarbilityPaylog(principal, map);

        // 缓存支付日志
        this.redisTemplate.boundHashOps("payLog").put(principal, payLog);

        // 清空购物车
        this.redisTemplate.boundHashOps(CacheEnum.CART_REMOTE_HASH.code).delete(principal);

    }

    @Override
    public void paySuccessUpdatePayLogAndOrder(String out_trade_no, String trade_no) {
        TbPayLog payLog = this.payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTransactionId(trade_no); // 支付平台交易流水号
        payLog.setPayTime(new Date()); // 支付时间
        payLog.setTradeState("2"); // 已支付
        String orderList = payLog.getOrderList();
        String[] orderIds = orderList.split(",");
        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            order.setStatus("2"); // 已支付
            order.setUpdateTime(new Date()); // 订单更新
            order.setPaymentTime(new Date()); // 支付
            this.orderMapper.updateByPrimaryKey(order);
        }
        this.payLogMapper.updateByPrimaryKey(payLog);
    }

    private TbPayLog populateDuarbilityPaylog(String principal, Map<String, Object> map) {
        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(idWorker.nextId() + ""); // 支付订单号
        //payLog.setTransactionId(""); // 支付平台交易流水号
        payLog.setUserId(principal); // 购买商品登录用户
        payLog.setTotalFee((long)((double)map.get("cartListFee") * 100)); // 支付金额 分
        payLog.setCreateTime(new Date()); // 支付日志创建 生成订单
        //payLog.setPayTime(); // 支付
        payLog.setPayType("2"); // 1 微信 2 支付宝
        payLog.setPayType("1"); // 未支付
        List<String> orderIds = (List<String>) map.get("orderIds");
        payLog.setOrderList(orderIds.toString().replace("[", "").replace("]", "")); // 订单id
        int insert = this.payLogMapper.insert(payLog);
        if (insert != 1)
            throw new DbException("持久化支付日志实例失败");
        return payLog;
    }

    /**
     *  创建用户购买的所有商家订单列表 一个商家一份
     * @param order 前端传递过来的数据
     * @param cartList 购物车 redis
     * @param principal shiro上下文
        @return 购物车列表总金额
     */
    private Map<String, Object> populateOderListAndDuarbility(TbOrder order, List<CarGroup> cartList, String principal) {
        List<TbOrder> orderList = new ArrayList<>();
        double cartListTotalFee = 0;
        List<String> orderIds = new ArrayList<>();
        Map<String, Object> map = new HashMap();
        for (CarGroup cart : cartList) {
            TbOrder tbOrder = new TbOrder();
            Long orderId = this.idWorker.nextId();
            orderIds.add(orderId + "");
            tbOrder.setOrderId(orderId); // 订单ID
            tbOrder.setSellerId(cart.getSellerId()); // 商家ID
            tbOrder.setPaymentType(order.getPaymentType()); // 支付的方式
            tbOrder.setStatus(OrderEnum.NOT_PATMENT.getCode()); // 订单状态
            tbOrder.setCreateTime(new Date()); // 订单的创建时间
            tbOrder.setUpdateTime(new Date()); // 订单的更新时间
            tbOrder.setUserId(principal); // 认证的用户
            tbOrder.setReceiver(order.getReceiver()); // 收货人
            tbOrder.setReceiverMobile(order.getReceiverMobile()); // 收货人手机号码
            tbOrder.setReceiverAreaName(order.getReceiverAreaName()); // 收货人地址
            tbOrder.setSourceType(order.getSourceType()); // 订单来源
            /*订单明细*/
            List<TbOrderItem> itemList = cart.getItemList();
            double payment = 0; // 商家订单总金额
            List<TbOrderItem> orderItemList = new ArrayList<>();
            for (TbOrderItem shipping : itemList) {
                TbOrderItem orderItem = shipping;
                orderItem.setId(this.idWorker.nextId()); // 订单明细id
                orderItem.setOrderId(orderId); // 订单id
                orderItem.setSellerId(cart.getSellerId()); // 商家id
                payment += orderItem.getTotalFee().doubleValue();
                orderItemList.add(orderItem);
            }
            tbOrder.setPayment(new BigDecimal(payment)); // 购物选项总金额
            cartListTotalFee += payment;
            orderList.add(tbOrder);
            // 现持久化订单 在订单明细 防止出现一段id不存在
            int i = this.orderMapper.insert(tbOrder);
            if (i != 1)
                throw new DbException("订单增加失败");
            for (TbOrderItem orderItem : orderItemList) {
                int i1 = this.orderItemMapper.insert(orderItem);
                if (i1 != 1)
                    throw new DbException("订单明细增加失败");
            }

        }

        map.put("cartListFee", cartListTotalFee);
        map.put("orderIds", orderIds);
        return map;
    }
}
