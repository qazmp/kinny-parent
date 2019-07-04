package com.kinny.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.cart.service.CartService;
import com.kinny.common.exception.DbException;
import com.kinny.mapper.TbItemMapper;
import com.kinny.pojo.TbItem;
import com.kinny.pojo.TbOrderItem;
import com.kinny.pojo.group.CarGroup;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qgy
 * @create 2019/7/4 - 11:35
 */
@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private TbItemMapper itemMapper;



    /**
     *  总结 复杂代码要写注释 避免一心二用 增大出bug的几率
     * @param carGroupList 原来的购物车列表
     * @param itemId sku 可以获取商家信息
     * @param num 购买
     * @return
     */
    @Override
    public List<CarGroup> addGoodsToCartList(List<CarGroup> carGroupList, Long itemId, Integer num) {

        TbItem item = this.itemMapper.selectByPrimaryKey(itemId);

        if(item == null || !(item.getStatus().equals("1"))) {
            throw new DbException("商品不存在或者已下架");
        }

        String sellerId = item.getSellerId();

        CarGroup cart = this.getSellerCartFromCartList(carGroupList, Long.parseLong(sellerId));

        // 1 判断原来购物车列表中是否含有该商家的购物车
        if(cart != null) { // 2 含有该商家的购物车
            // 2.1 获取该商家的购物车对象 判断购物明细列表中 是否含有该商品明细列表
            TbOrderItem orderItem = this.getGoodsOrderItemFromCartOrderItemList(cart.getItemList(), itemId);

            if(orderItem != null) { // 2.1.1 该商家购物车购物明细列表中含有该商品
                // 2.1.1.1 获取商品明细对象 并购买数量新增 计算总价 注意 num 可能为负数
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                if(orderItem.getNum() < 1) {
                    cart.getItemList().remove(orderItem);
                    if(cart.getItemList().size() < 1) {
                        carGroupList.remove(cart);
                    }
                }
            }else { // 2.1.2 该商家购物车购物明细列表中不含有该商品
                // 2.1.2.1 创建商品明细对象 并添加到商品明细列表
                orderItem = this.createOrderItem(item, num);
                cart.getItemList().add(orderItem);
            }
        }else {  // 3 不含有该商家购物车

            // 3.1 创建该商家的购物车
            cart = this.createCart(item);

            // 3.2 创建该商品的购物车明细并添加到购物车明细列表
            TbOrderItem orderItem = this.createOrderItem(item, num);
            cart.getItemList().add(orderItem);
        }

        carGroupList.add(cart);

        return carGroupList;
    }

    /**
     *  根据商家ID判断购物车列表中是否含有该商家的购物车
     * @param carGroupList 购物车列表
     * @param sellerId 商家ID
     * @return 有 返回该商家的购物车 没有 返回空
     */
    private CarGroup getSellerCartFromCartList(List<CarGroup> carGroupList, Long sellerId) {
        for (CarGroup carGroup : carGroupList) {
            if(carGroup.getSellerId().equals(sellerId)) {
                return carGroup;
            }
        }
        return null;
    }

    /**
     *  判断购物车明细列表中是否含有该商品购物明细对象
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem getGoodsOrderItemFromCartOrderItemList(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     *  创建购物细节对象
     * @param item sku商品
     * @param num 购买数量 不需要对其进行范围校验 新加入购物车
     * @return 细节对象
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     *  创建购物车 不包含购物细节列表空集合
     * @param item sku商品
     * @return
     */
    private CarGroup createCart(TbItem item) {
        CarGroup cartGroup = new CarGroup();
        cartGroup.setSellerId(Long.parseLong(item.getSellerId()));
        cartGroup.setSellerName(item.getSeller());
        return cartGroup;
    }

}
