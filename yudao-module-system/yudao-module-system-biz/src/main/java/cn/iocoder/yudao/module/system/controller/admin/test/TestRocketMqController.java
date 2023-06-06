package cn.iocoder.yudao.module.system.controller.admin.test;

import cn.iocoder.yudao.module.system.mq.message.sms.SmsSendMessage;
import cn.iocoder.yudao.module.system.util.mock.RandomUtils1;

import com.alibaba.fastjson.JSON;
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

    /**
      *@description 事务消息
     *            因为 Spring Cloud Stream 在设计时，并没有考虑事务消息，所以我们只好在 <X> 处，通过 Header 传递参数。又因为 Header 后续会被转换成 String 类型，
     *            导致我们无法获得正确的真实的原始参数，所以这里我们先使用 JSON 将 args 参数序列化成字符串，这样后续我们可以使用 JSON 反序列化回来。
     *            https://github.com/alibaba/spring-cloud-alibaba/blob/2022.x/spring-cloud-alibaba-examples/rocketmq-example/rocketmq-tx-example/src/main/java/com/alibaba/cloud/examples/tx/RocketMQTxApplication.java
      *@author zyc
      *@date 2023/5/21 18:33
     * @param
      *@return void
     **/
    @RequestMapping("/sendMsTransactional")
    public void sendMsTransactional(){
        // 创建 Message
        SmsSendMessage smsSendMessage = RandomUtils1.randomPojo(SmsSendMessage.class);
        // 创建 Spring Message 对象
        Args args = new Args().setArgs1(1).setArgs2("2");
        Message<SmsSendMessage> springMessage = MessageBuilder.withPayload(smsSendMessage)
                .setHeader("args", JSON.toJSONString(args)) // <X>
                .build();

        streamBridge.send("testSend-out-0", springMessage);
    }
}
