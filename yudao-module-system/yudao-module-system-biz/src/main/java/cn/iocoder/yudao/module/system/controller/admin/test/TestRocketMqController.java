package cn.iocoder.yudao.module.system.controller.admin.test;

import cn.iocoder.yudao.module.system.mq.message.sms.SmsSendMessage;
import cn.iocoder.yudao.module.system.util.mock.RandomUtils1;

import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName TestRocketMq
 * @Description TODO
 * @Author zyc
 * @Date 2023/5/20 21:50
 * @Version 1.0
 */
@RestController
@RequestMapping("/system/test")
public class TestRocketMqController {

    @Resource
    private StreamBridge streamBridge;

    /** 消息过滤 **/
    @RequestMapping("/sendMsgFilter")
    public void sendMsgFilter(){



        for (String tag : new String[]{"yunai", "yutou", "tudou"}) {
            // 创建 Message
            SmsSendMessage smsSendMessage = RandomUtils1.randomPojo(SmsSendMessage.class);
            smsSendMessage.setMobile(tag);

            // 创建 Spring Message 对象
            Message<SmsSendMessage> springMessage = MessageBuilder.withPayload(smsSendMessage).setHeader(MessageConst.PROPERTY_TAGS, tag).build(); // <X> 设置 Tag
            // 发送消息
            streamBridge.send("testSend-out-0", springMessage);
        }
    }
}
