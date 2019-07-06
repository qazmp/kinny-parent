package com.kinny.page.service.impl;

import com.kinny.mapper.TbGoodsDescMapper;
import com.kinny.mapper.TbGoodsMapper;
import com.kinny.mapper.TbItemCatMapper;
import com.kinny.mapper.TbItemMapper;
import com.kinny.page.service.ItemPageService;
import com.kinny.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/6/16 - 16:16
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {


    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Value("${pageDir}")
    private String pageDir;


    @Override
    public boolean genItemPage(String goodsIdS) {

        if (goodsIdS == null) {
            throw new IllegalArgumentException("商品id不能为空");
        }

        Long goodsId = null;

        try {
            goodsId = Long.parseLong(goodsIdS);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("商品id必须是数字");
        }


        TbGoods tbGoods = this.goodsMapper.selectByPrimaryKey(goodsId);

        TbGoodsDesc tbGoodsDesc = this.goodsDescMapper.selectByPrimaryKey(goodsId);

        Configuration configuration = this.configurer.getConfiguration();
        Writer out = null;
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map<String, Object> dataModel = new HashMap<>();

            dataModel.put("goods", tbGoods);
            dataModel.put("goodsDesc", tbGoodsDesc);

            String oneLevelCategory = this.itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String towLevelCategory = this.itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String threeLevelCategory = this.itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            dataModel.put("oneLevelCategory", oneLevelCategory);
            dataModel.put("towLevelCategory", towLevelCategory);
            dataModel.put("threeLevelCategory", threeLevelCategory);

            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1"); // 上架
            example.setOrderByClause("is_default DESC"); // 默认
            List<TbItem> tbItems = this.itemMapper.selectByExample(example);

            dataModel.put("itemList", tbItems);

            out = new FileWriter(this.pageDir + goodsId + ".html");
            template.process(dataModel, out);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }

        return true;
    }

    @Override
    public boolean deleteItemPage(String[] ids) {

        for (String id : ids) {
            try {
                return new File(this.pageDir + id + ".html").delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
