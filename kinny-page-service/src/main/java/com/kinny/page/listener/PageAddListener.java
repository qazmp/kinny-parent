package com.kinny.page.listener;

import com.kinny.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author qgy
 * @create 2019/6/20 - 16:46
 */
public class PageAddListener implements MessageListener {


    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                this.itemPageService.genItemPage(textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
