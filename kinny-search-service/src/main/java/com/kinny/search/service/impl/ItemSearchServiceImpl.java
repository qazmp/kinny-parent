package com.kinny.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.kinny.common.pojo.em.CacheEnum;
import com.kinny.common.pojo.em.ItemSearchEnum;
import com.kinny.pojo.TbItem;
import com.kinny.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import sun.misc.Cache;

import java.util.*;

/**
 * @author qgy
 * @create 2019/6/10 - 10:26
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public Map<String, Object> search(Map<String, Object> parameterMap) {

        Map<String, Object> objectMap = new HashMap<>();

        // 1 关键字高亮查询
        objectMap.putAll(this.highLightQuery(parameterMap));

        // 2 对查询结果进行统计分析 获取所有的品牌和规格数据 缓存中
        Map<String, Object> map = this.groupQuery(parameterMap);
        List<String> categorys = (List<String>) map.get("categoryList");

        objectMap.putAll(map);

        // 获取分类下的 brand speci
        String category = (String) parameterMap.get("category");
        if (!("".equals(category))) {
            objectMap.putAll(this.getBrandAndSpecificationFromCache(category));
        }else {
            // 没有使用分类过滤查询默认第一个分类下的
            if (categorys.size() > 0) {
                objectMap.putAll(this.getBrandAndSpecificationFromCache(categorys.get(0)));
            }
        }

        return objectMap;
    }

    @Override
    public void batchImportSku(List<TbItem> items) {
        System.out.println("items = [" + items + "]");
        // todo 有可能要审核的spu下没有 sku 应该是业务逻辑错误 应该有默认的sku
        try {
            this.solrTemplate.saveBeans(items);
        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("即将提交");
        this.solrTemplate.commit();

    }



    /**
     *  获取关键字 并对参数进行非空校验
     * @param parameterMap
     * @return
     */
    private String getKeywordsAndValidate(Map<String, Object> parameterMap) {
        String keywords = (String) parameterMap.get(ItemSearchEnum.PARAMETER_KYEWORDS.getCode());

        if (keywords == null || "".equals(keywords)) {
            throw new IllegalArgumentException("关键字不能为空");
        }
        return keywords;
    }
    /**
     *  关键字高亮查询
     * @param parameterMap
     * @return
     */
    private Map<String, Object> highLightQuery(Map<String, Object> parameterMap) {
        String keywords = getKeywordsAndValidate(parameterMap);
        keywords = keywords.replace(" ", ""); // 去掉空格 防止破坏分词效果

        String category = (String) parameterMap.get("category");
        String brand = (String) parameterMap.get("brand");
        Map<String, String> specMap = (Map<String, String>) parameterMap.get("spec");
        String price = (String) parameterMap.get("price");
        Integer pageIndex = getPageIndexDefalt1(parameterMap);
        Integer pageSize = getPageSizeDefalt20(parameterMap);
        String sort = (String) parameterMap.get("sort");
        String sortField = (String) parameterMap.get("sortField");



        HighlightQuery query = new SimpleHighlightQuery();

        // 设置高亮选项
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        // 1.1 关键字
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);



        // 1.2 过滤
        FilterQuery filterQuery = new SimpleFilterQuery();

        // 1.2.1 商品分类查询
        if (category != null && !("".equals(category))) {

            Criteria criteria1 = new Criteria("item_category").is(category);
            filterQuery.addCriteria(criteria1);

        }

        // 1.2.2 商品品牌查询
        if (brand != null && !("".equals(brand))) {

            Criteria criteria1 = new Criteria("item_brand").is(brand);
            filterQuery.addCriteria(criteria1);

        }

        // 1.2.3 商品规格查询
        if(specMap != null && specMap.size() > 0) {

            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                Criteria criteria1 = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                filterQuery.addCriteria(criteria1);
            }
        }


        // 1.2.4 商品价格区间查询
        if(price != null && !("".equals(price))) {
            String[] priceRange = price.split("-");
            if(!priceRange[0].equals("0")) {
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(priceRange[0]);
                filterQuery.addCriteria(criteria1);
            }
            if(!priceRange[1].equals("*")) {
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(priceRange[1]);
                filterQuery.addCriteria(criteria1);
            }
        }




        query.addFilterQuery(filterQuery);

        // 1.2.5 分页查询
        query.setOffset((pageIndex - 1) * pageSize);
        query.setRows(pageSize);

        // 1.2.6 排序
        if (sortField != null && !("".equals(sortField))) {
            if("ASC".equals(sort)) {
                Sort sort1 = new Sort(Sort.Direction.ASC, sortField);
                query.addSort(sort1);
            }
            if("DESC".equals(sort)) {
                Sort sort1 = new Sort(Sort.Direction.DESC, sortField);
                query.addSort(sort1);
            }

        }


        HighlightPage<TbItem> highlightPage = this.solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlightEntryList = highlightPage.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlightEntryList) {
            List<HighlightEntry.Highlight> highlightList = highlightEntry.getHighlights();
            if (highlightList.size() > 0) {
                HighlightEntry.Highlight highlight = highlightList.get(0);
                List<String> snipplets = highlight.getSnipplets();
                if(snipplets.size() > 0) {
                    TbItem entity = highlightEntry.getEntity();
                    System.out.println(snipplets.get(0));
                    entity.setTitle(snipplets.get(0));
                }
            }
        }

        Map<String, Object> map = new HashMap<>();

        map.put(ItemSearchEnum.RESULT_LIST.getCode(), highlightPage.getContent());

        map.put("totalSize", highlightPage.getTotalElements());
        map.put("totalCount", highlightPage.getTotalPages());

        return map;
    }

    private Integer getPageIndexDefalt1(Map<String, Object> parameterMap) {
        Integer pageIndex = (Integer) parameterMap.get("pageIndex"); // 注意前端js对象 该属性为 int
        if (pageIndex == null) {
            return 1;
        }

       return pageIndex;
    }
    private Integer getPageSizeDefalt20(Map<String, Object> parameterMap) {
        Integer pageSize = (Integer) parameterMap.get("pageSize");
        if (pageSize == null) {
            return 20;
        }
       return pageSize;
    }


    /**
     *  对关键字查询结果进行统计分析 获取商品分类列表
     * @return
     */
    private Map<String, Object> groupQuery(Map<String, Object> parameterMap) {

        String keywords = this.getKeywordsAndValidate(parameterMap);

        Query query = new SimpleQuery("*:*");

        Criteria critetia = new Criteria("item_keywords").is(keywords);
        query.addCriteria(critetia);
        // 设置分组域
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");

        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> page = this.solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntriesPage = groupResult.getGroupEntries(); // 获取分组路口集合
        List<GroupEntry<TbItem>> groupEntryList = groupEntriesPage.getContent();
        List<String> list = new ArrayList<>();
        for (GroupEntry<TbItem> groupEntry : groupEntryList) {
            String groupValue = groupEntry.getGroupValue(); // 获取分组值
            list.add(groupValue);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", list);
        return map;
    }

    /**
     *  查询缓存中品牌和规格及选项
     * @param category
     * @return
     */
    private Map<String, Object> getBrandAndSpecificationFromCache(String category) {

        Long typeTemplateId = (Long) this.redisTemplate.boundHashOps(CacheEnum.CACHE_CATEGORY_ID.code).get(category);

        Map<String, Object> map = new HashMap<>();

        if (typeTemplateId != null) {
            List<Map<String, Object>> brands = (List<Map<String, Object>>) this.redisTemplate.boundHashOps(CacheEnum.CACHE_ID_BRAND.code).get(typeTemplateId + "");
            List<Map<String, Object>> specifications = (List<Map<String, Object>>) this.redisTemplate.boundHashOps(CacheEnum.CACHE_ID_SPECIFICATION.code).get(typeTemplateId + "");
            map.put("brands", brands);
            map.put("specifications", specifications);

        }


        return map;
    }


    @Override
    public void deleteBySpuId(String id) {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsId").is(id);
        query.addCriteria(criteria);
        this.solrTemplate.delete(query);
        this.solrTemplate.commit();
    }


}
