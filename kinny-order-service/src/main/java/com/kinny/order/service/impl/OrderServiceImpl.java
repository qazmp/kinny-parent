package com.kinny.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.CacheEnum;
import com.kinny.common.pojo.em.OrderEnum;
import com.kinny.mapper.TbOrderItemMapper;
import com.kinny.mapper.TbOrderMapper;
import com.kinny.order.service.OrderService;
import com.kinny.pojo.TbOrder;
import com.kinny.pojo.TbOrderItem;
import com.kinny.pojo.group.CarGroup;
import com.kinny.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        this.populateOderListAndDuarbility(order, cartList, principal);
        // 清空购物车
        this.redisTemplate.boundHashOps(CacheEnum.CART_REMOTE_HASH.code).delete(principal);

    }

    /**
     *  创建用户购买的所有商家订单列表 一个商家一份
     * @param order 前端传递过来的数据
     * @param cartList 购物车 redis
     * @param principal shiro上下文
     */
    private void populateOderListAndDuarbility(TbOrder order, List<CarGroup> cartList, String principal) {
        List<TbOrder> orderList = new ArrayList<>();
        for (CarGroup cart : cartList) {
            TbOrder tbOrder = new TbOrder();
            Long orderId = this.idWorker.nextId();
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
    }
}
