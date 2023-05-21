package cn.iocoder.yudao.module.system.mq.consumer.test;

import cn.iocoder.yudao.module.system.mq.message.sms.SmsSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @ClassName TestSendConsumer
 * @Description TODO
 * @Author zyc
 * @Date 2023/5/20 21:32
 * @Version 1.0
 */
@Component
@Slf4j
public class TestSendConsumer implements Consumer<Message<SmsSendMessage>> {
    @Override
    public void accept(Message<SmsSendMessage> smsSendMessage) {
        log.info("ROCKEMQ [accept][消息内容({})]", smsSendMessage);
    }
}
