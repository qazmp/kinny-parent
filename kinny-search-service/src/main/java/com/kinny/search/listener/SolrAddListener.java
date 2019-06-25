package com.kinny.search.listener;

import com.alibaba.fastjson.JSON;
import com.kinny.pojo.TbItem;
import com.kinny.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @author qgy
 * @create 2019/6/20 - 10:59
 */
public class SolrAddListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String text = textMessage.getText();
                List<TbItem> tbItems = JSON.parseArray(text, TbItem.class);
                this.itemSearchService.batchImportSku(tbItems);
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }

    }
}
