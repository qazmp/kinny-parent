package com.kinny.search.listener;

import com.kinny.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author qgy
 * @create 2019/6/20 - 11:15
 */
public class SolrDelListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                String [] ids = (String[]) objectMessage.getObject();
                for (String id : ids) {
                    this.itemSearchService.deleteBySpuId(id);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
