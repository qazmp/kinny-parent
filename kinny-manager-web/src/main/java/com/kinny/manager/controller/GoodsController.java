package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.kinny.common.exception.DbException;
import com.kinny.pojo.TbGoods;
import com.kinny.pojo.TbItem;
import com.kinny.pojo.TbSeller;
import com.kinny.sellergoods.service.GoodsService;
import com.kinny.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/21 - 11:07
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {



    @Reference
    private GoodsService goodsService;

//    @Reference
//    private ItemSearchService searchService;

    @Autowired
    private Destination solrAddQueue;

    @Autowired
    private Destination solrDelQueue;

    @Autowired
    private Destination pageAddTopic;

    @Autowired
    private Destination pageDelTopic;

    @Autowired
    private JmsTemplate jmsTemplate;

//    @Reference(timeout = 50000)
//    private ItemPageService pageService;


    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(@RequestBody(required = true) TbGoods goods,
                                                    @RequestParam(required = false, defaultValue = "1") String pageIndex,
                                                    @RequestParam(required = false, defaultValue = "10") String pageSize) {

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("pageIndex", pageIndex);
        objectMap.put("pageSize", pageSize);
        objectMap.put("goods", goods);

        Map<String, Object> page = null;

        try {
            page = this.goodsService.findPage(objectMap);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false , e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商品查询失败");
        }

        return new ResponseVo<>(true, page);
    }

    @RequestMapping("/updateStatus")
    public ResponseVo updateStatus(
            @RequestParam(value = "id", required = true) String idSplit,
            @RequestParam(value = "status", required = true) String status
    ) {
        String[] split = idSplit.split("-");

        try {
            for (String s : split) {
                this.goodsService.updateStatus(s, status);
                updateSolrIndex(status, s);
                genGoodsDetails(status, s);
            }
        }  catch (IllegalArgumentException e) {
            return new ResponseVo<>(false , e.getMessage());
        } catch (DbException e) {
            return new ResponseVo<>(false , e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商品状态修改异常");
        }

        return new ResponseVo<>(true, "商品状态修改正常");
    }

    /**
     *  创建商品详情
     * @param status
     * @param s
     */
    private void genGoodsDetails(@RequestParam(value = "status", required = true) String status, final String s) {
        if("1".equals(status)) {
            //this.pageService.genItemPage(s);
            this.jmsTemplate.send(this.pageAddTopic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(s);
                }
            });
        }
    }

    /**
     *  将审核通过的商品信息添加到索引库 同步方法是增量
     * @param status
     * @param s
     */
    private void updateSolrIndex(@RequestParam(value = "status", required = true) String status, String s) {
        /**
         *  审核通过导入索引库
         */
        if("1".equals(status)) {
            // 上架
            List<TbItem> items = this.goodsService.findSkuBySpuIdAndStatus(s, "1");
            //this.searchService.batchImportSku(items); todo 同步activeMq 实现异步调用
            final String itemsString = JSON.toJSONString(items);
            this.jmsTemplate.send(this.solrAddQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(itemsString);
                }
            });


        }
    }

    @RequestMapping("/delete")
    public ResponseVo delete(
            @RequestParam(value = "ids", required = true) String idSplit
    ) {
        final String[] split = idSplit.split("-");

        try {
            for (String s : split) {
                this.goodsService.batchDelete(split);
                /**
                 *  删除索引库中的数据
                 */
                //this.searchService.deleteBySpuId(s); todo
            }
            this.jmsTemplate.send(this.solrDelQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(split);
                }
            });

            // 删除对应的商品详细页
            this.jmsTemplate.send(this.pageDelTopic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(split);
                }
            });

        }  catch (IllegalArgumentException e) {
            return new ResponseVo<>(false , e.getMessage());
        } catch (DbException e) {
            return new ResponseVo<>(false , e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商品删除修改异常");
        }

        return new ResponseVo<>(true, "商品删除修改正常");
    }


    @RequestMapping("/genItemPage")
    public void genItemPage(String goodsId) {

        //this.pageService.genItemPage(goodsId);
    }






}
