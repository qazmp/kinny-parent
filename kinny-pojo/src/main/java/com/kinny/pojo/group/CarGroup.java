package com.kinny.pojo.group;

import com.kinny.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  购物车对象 数据表结构 有购物车实例抽象的
 *  api接口设计 没有登录时 使用cookie存储
 *  登录后 购物车有了ID使用Redis存储
 * @author qgy
 * @create 2019/7/4 - 10:01
 */
public class CarGroup implements Serializable {

    private Long sellerId;

    private String sellerName;

    private List<TbOrderItem> itemList = new ArrayList<>(); // 空集合 不能是null

    @Override
    public String toString() {
        return "CarGroup{" +
                "sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", itemList=" + itemList +
                '}';
    }

    public CarGroup(Long sellerId, String sellerName, List<TbOrderItem> itemList) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.itemList = itemList;
    }

    public CarGroup() {
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbOrderItem> itemList) {
        this.itemList = itemList;
    }
}
