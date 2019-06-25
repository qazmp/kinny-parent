package com.kinny.solr.util;

import com.alibaba.fastjson.JSON;
import com.kinny.mapper.TbItemMapper;
import com.kinny.pojo.TbItem;
import com.kinny.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/6/9 - 17:57
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;



    public void importData() {

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = this.itemMapper.selectByExample(example);

        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();
            Map specMap = JSON.parseObject(spec, Map.class);
            tbItem.setSpecMap(specMap);
        }

        this.solrTemplate.saveBeans(tbItems);
        this.solrTemplate.commit();
    }





}
