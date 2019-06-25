package com.kinny.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.GoodsEnum;
import com.kinny.mapper.*;
import com.kinny.pojo.*;
import com.kinny.pojo.group.GoodsGroup;
import com.kinny.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author qgy
 * @create 2019/5/15 - 8:46
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     *  在业务层完成数据校验工作 体现了软件架构的思想
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> findPage(Map<String, Object> map) {

        // 获取当前页 一页数据条数
        String pageIndexStr = (String) map.get("pageIndex");
        String pageSizeStr = (String) map.get("pageSize");
        // 封装查询条件的实体类
        TbGoods goods = (TbGoods) map.get("goods");
        // 校验参数
        if (pageIndexStr == null) {
            throw new IllegalArgumentException("当前页数不能为空");
        }
        if (pageSizeStr == null) {
            throw new IllegalArgumentException("每页数据条数不能为空");
        }
        // 校验数据的格式
        Integer pageIndex = null;
        Integer pageSize = null;

        try {
            pageIndex = Integer.parseInt(pageIndexStr);
            pageSize = Integer.parseInt(pageSizeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("当前页数或者每页数据条数必须是数字");
        }

        // mybatis pageHelper
        PageHelper.startPage(pageIndex, pageSize);

        // 查询全部数据 mybatis会通过插件机制对sql 修改
        TbGoodsExample example = new TbGoodsExample();

        TbGoodsExample.Criteria criteria = example.createCriteria();

        if (goods != null) {

            if (goods.getAuditStatus() != null && !("".equals(goods.getAuditStatus())))
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());

            if (goods.getGoodsName() != null && !("".equals(goods.getGoodsName())))
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");

            // 商家过滤条件 商家管理后台只能显示当前商家的商品信息 横向越权 同级别非法查看其他商家的商品信息
            if (goods.getSellerId() != null && !("".equals(goods.getSellerId())))
                criteria.andSellerIdEqualTo(goods.getSellerId());

        }

        // 默认的过滤
        criteria.andIsDeleteIsNull(); // null

        List<TbGoods> tbGoods = this.goodsMapper.selectByExample(example);

        PageInfo<TbGoods> pageInfo = new PageInfo<>(tbGoods);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("totalSize", pageInfo.getTotal());
        objectMap.put("list", pageInfo.getList());


        return objectMap;
    }

    @Override
    public List<TbItem> findSkuBySpuIdAndStatus(String idS, String status) {

        if (idS == null) {
            throw new IllegalArgumentException("商品id不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idS);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("商品id必须是数字");
        }

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);

        criteria.andStatusEqualTo(status);

        List<TbItem> items = this.itemMapper.selectByExample(example);


        return items;
    }

    @Override
    public GoodsGroup findOne(String idStr) {

        if (idStr == null) {
            throw new IllegalArgumentException("商品id不能为空");
        }

        Long id = null;

        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("商品id必须是数字");
        }

        TbGoods goods = this.goodsMapper.selectByPrimaryKey(id);

        TbGoodsDesc goodsDesc = this.goodsDescMapper.selectByPrimaryKey(id);

        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(id);

        List<TbItem> items = this.itemMapper.selectByExample(tbItemExample);

        GoodsGroup goodsGroup = new GoodsGroup();
        goodsGroup.setGoods(goods);
        goodsGroup.setGoodsDesc(goodsDesc);
        goodsGroup.setItems(items);

        return goodsGroup;
    }

    /**
     *  控制器调用该方法是 注意将商家实体信息注入
     * @param goodsGroup
     */
    @Override
    public void save(GoodsGroup goodsGroup) {

        if (goodsGroup == null) {
            throw new IllegalArgumentException("组合实体类不能为空");
        }

        TbGoods goods = goodsGroup.getGoods();
        if (goodsGroup == null) {
            throw new IllegalArgumentException("基本信息不能为空");
        }
        TbGoodsDesc goodsDesc = goodsGroup.getGoodsDesc();
        if (goodsGroup == null) {
            throw new IllegalArgumentException("描述不能为空");
        }



        if (goods.getId() == null) {
            add(goodsGroup, goods, goodsDesc);
        }else {

            update(goodsGroup, goods, goodsDesc);
        }

    }

    @Override
    public void updateStatus(String idStr, String s) {

        if (idStr == null || s == null) {
            throw new IllegalArgumentException("商品主键或者状态不能为空");
        }

        Long id = null;
        Integer st = null;

        try {
            id = Long.parseLong(idStr);
            st = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("商品主键或者状态必须是数值");
        }

        Set<Integer> integers = new HashSet<>();
        integers.addAll(Arrays.asList(new Integer []{0, 1, 2, 3}));

        if (!integers.contains(st)) {
            throw new IllegalArgumentException("状态范围错误");
        }

        TbGoods tbGoods = new TbGoods();
        tbGoods.setId(id);
        tbGoods.setAuditStatus(st + "");
        int update = this.goodsMapper.updateByPrimaryKeySelective(tbGoods);

        if(update != 1) {
            throw new DbException("修改商品的状态失败");
        }


    }

    @Override
    public void batchDelete(String[] ids) {
        if (ids == null || ids.length == 0) {
            throw new IllegalArgumentException("商品主键或者状态不能为空");
        }

        List<Long>  list = new ArrayList();

        for (String id : ids) {
            try {
                list.add(Long.parseLong(id));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("商品主键必须是数值");
            }
        }

        for (Long o : list) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(o);
            tbGoods.setIsDelete("1");
            int update = this.goodsMapper.updateByPrimaryKeySelective(tbGoods);
            if(update != 1) {
                throw new DbException("删除商品失败");
            }
        }


    }

    private void update(GoodsGroup goodsGroup, TbGoods goods, TbGoodsDesc goodsDesc) {
        int update = this.goodsMapper.updateByPrimaryKeySelective(goods);
        if (update != 1)
            throw new DbException("商品的基本信息修改失败");
        goodsDesc.setGoodsId(goods.getId());
        int update1 = this.goodsDescMapper.updateByPrimaryKeySelective(goodsDesc);
        if (update1 != 1)
            throw new DbException("商品的描述信息修改失败");

        if (GoodsEnum.ENABLE_SPECIFICATION.getCode().equals(goods.getIsEnableSpec())) {

            List<TbItem> items = goodsGroup.getItems();

            if (items == null || items.size() == 0) {
                throw new IllegalArgumentException("已经启用规格 sku不能为空");
            }

            // 删除原来的sku
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goods.getId());
            this.itemMapper.deleteByExample(example);
            for (TbItem item : items) {
                assembleItem(goods, goodsDesc, item);
            }

            for (TbItem item : items) {
                System.out.println("-------------------" + item.getStatus() + "-----------------------");
                int insert2 = this.itemMapper.insert(item);
                if (insert2 != 1)
                    throw new DbException("商品的sku新增失败");
            }
        }else {
            // 删除原来的sku
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goods.getId());
            this.itemMapper.deleteByExample(example);
            // 禁用
            TbItem tbItem = defaultItem(goods, goodsDesc);
            int insert2 = this.itemMapper.insert(tbItem);
            if (insert2 != 1)
                throw new DbException("商品的sku新增失败");
        }
    }

    private void add(GoodsGroup goodsGroup, TbGoods goods, TbGoodsDesc goodsDesc) {
        // 商品状态默认是未审核
        goods.setAuditStatus(GoodsEnum.NOT_CHECK.getCode());
        int insert = this.goodsMapper.insert(goods);
        if (insert != 1)
            throw new DbException("商品的基本信息新增失败");
        goodsDesc.setGoodsId(goods.getId());
        int insert1 = this.goodsDescMapper.insert(goodsDesc);

        if (insert1 != 1)
            throw new DbException("商品的描述信息新增失败");

        if (GoodsEnum.ENABLE_SPECIFICATION.getCode().equals(goods.getIsEnableSpec())) {

            List<TbItem> items = goodsGroup.getItems();

            if (items == null || items.size() == 0) {
                throw new IllegalArgumentException("已经启用规格 sku不能为空");
            }

            for (TbItem item : items) {
                assembleItem(goods, goodsDesc, item);
            }

            for (TbItem item : items) {
                int insert2 = this.itemMapper.insert(item);
                if (insert2 != 1)
                    throw new DbException("商品的sku新增失败");
            }


        }else {
            TbItem tbItem = defaultItem(goods, goodsDesc);
            int insert2 = this.itemMapper.insert(tbItem);
            if (insert2 != 1)
                throw new DbException("商品的sku新增失败");
        }
    }

    private TbItem defaultItem(TbGoods goods, TbGoodsDesc goodsDesc) {
        // 禁用
        TbItem tbItem = new TbItem();
        tbItem.setTitle(goods.getGoodsName());
        tbItem.setSpec("{}");
        tbItem.setPrice(goods.getPrice());
        tbItem.setNum(99999);
        tbItem.setIsDefault(GoodsEnum.DEFAULT.getCode());
        tbItem.setStatus(GoodsEnum.NOT_CHECK.getCode());



        // spu id
        tbItem.setGoodsId(goods.getId());
        // seller
        tbItem.setSellerId(goods.getSellerId());
        TbSeller tbSeller = this.sellerMapper.selectByPrimaryKey(goods.getSellerId());
        tbItem.setSeller(tbSeller.getName());
        // 分类
        TbItemCat tbItemCat = this.itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        tbItem.setCategory(tbItemCat.getName());
        tbItem.setCategoryid(goods.getCategory3Id());
        // 品牌
        TbBrand tbBrand = this.brandMapper.selectByPrimaryKey(goods.getBrandId());
        tbItem.setBrand(tbBrand.getName());

        // 商品图片默认是第一张图片
        String itemImages = goodsDesc.getItemImages();
        List<Map> list = JSON.parseArray(itemImages, Map.class);
        String url = (String) list.get(0).get("url");
        tbItem.setImage(url);


        // 状态
        tbItem.setStatus(GoodsEnum.NOT_CHECK.getCode());

        // 创建修改
        tbItem.setCreateTime(new Date());
        tbItem.setUpdateTime(new Date());

        return tbItem;
    }

    /**
     *  商品基本信息 商品描述信息 填充到sku
     * @param goods
     * @param goodsDesc
     * @param item
     */
    private void assembleItem(TbGoods goods, TbGoodsDesc goodsDesc, TbItem item) {
        // spu id
        item.setGoodsId(goods.getId());
        // seller
        item.setSellerId(goods.getSellerId());
        TbSeller tbSeller = this.sellerMapper.selectByPrimaryKey(goods.getSellerId());
        item.setSeller(tbSeller.getName());
        // 分类
        TbItemCat tbItemCat = this.itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        item.setCategory(tbItemCat.getName());
        item.setCategoryid(goods.getCategory3Id());
        // 品牌
        TbBrand tbBrand = this.brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(tbBrand.getName());
        // 标题
        String spec = item.getSpec(); // {"机身内存":"16G","网络":"联通3G"}
        Map<String, Object> jsonObject = JSON.parseObject(spec);
        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        String goodsName = goods.getGoodsName();
        for (Map.Entry<String, Object> entry : entries) {
            goodsName += " " + entry.getValue();
        }
        item.setTitle(goodsName);

        // 商品图片默认是第一张图片
        String itemImages = goodsDesc.getItemImages();
        List<Map> list = JSON.parseArray(itemImages, Map.class);
        String url = (String) list.get(0).get("url");
        item.setImage(url);

        //todo 状态 字段对吗 商品状态，1-正常，2-下架，3-删除 前端传过来了 不要和goods 审核状态混淆
        //item.setStatus(GoodsEnum.NOT_CHECK.getCode());

        // 创建修改
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
    }
}
