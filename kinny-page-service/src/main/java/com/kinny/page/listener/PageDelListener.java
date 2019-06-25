package com.kinny.page.listener;

import com.kinny.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @author qgy
 * @create 2019/6/20 - 17:24
 */
public class PageDelListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        if(message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                String [] goodsIds = (String[]) objectMessage.getObject();
                this.itemPageService.deleteItemPage(goodsIds);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }



}
