package com.kinny.pojo.group;

import com.kinny.pojo.TbGoods;
import com.kinny.pojo.TbGoodsDesc;
import com.kinny.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author qgy
 * @create 2019/5/15 - 9:46
 */
public class GoodsGroup implements Serializable {

    private TbGoods goods;

    private TbGoodsDesc goodsDesc;

    private List<TbItem> items;

    public GoodsGroup() {
    }

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItems() {
        return items;
    }

    public void setItems(List<TbItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "GoodsGroup{" +
                "goods=" + goods +
                ", goodsDesc=" + goodsDesc +
                ", items=" + items +
                '}';
    }
}
