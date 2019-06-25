package com.kinny.solr.util;

import com.kinny.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author qgy
 * @create 2019/6/9 - 18:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class SolrUtilTest {

    @Autowired
    private SolrUtil solrUtil;

    @Autowired
    private SolrTemplate solrTemplate;

    //@Test
    public void testImport() {

        this.solrUtil.importData();

    }

    @Test
    public void testBatchDelete() {
        SolrDataQuery query = new SimpleQuery("*:*");
        this.solrTemplate.delete(query);
        this.solrTemplate.commit();
    }



}
