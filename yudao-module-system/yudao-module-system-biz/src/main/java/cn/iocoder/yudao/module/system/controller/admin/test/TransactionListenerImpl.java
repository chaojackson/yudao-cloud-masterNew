package cn.iocoder.yudao.module.system.controller.admin.test;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;

import org.apache.rocketmq.common.message.MessageExt;

import org.springframework.stereotype.Component;

/**
 * @ClassName TransactionListenerImpl
 * @Description  事务的实现类
 * @Author zyc
 * @Date 2023/5/21 18:38
 * @Version 1.0
 */
@Component("myTransactionListener")
public class TransactionListenerImpl implements TransactionListener {
    /**
     * 实现 #executeLocalTransaction(...) 方法，实现执行本地事务。
     * 注意，这是一个模板方法。在调用这个方法之前，Spring Cloud Alibaba Stream RocketMQ 已经使用 Producer 发送了一条事务消息。然后根据该方法执行的返回的 RocketMQLocalTransactionState 结果，提交还是回滚该事务消息
     * @param msg messages
     * @param arg message args
     * @return Transaction state
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

        // 从消息 Header 中解析到 args 参数，并使用 JSON 反序列化
        Args args = JSON.parseObject(msg.getProperty("args"), Args.class);

        if ("1".equals(args.getArgs2())) {
            System.out.println("executer: " + args + " unknown");
            return LocalTransactionState.UNKNOW;
        }
        else if ("2".equals(args.getArgs2())) {
            System.out.println("executer: " + args + " rollback");
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        System.out.println("executer: " + args+ " commit");
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    /**
     * 实现 #checkLocalTransaction(...) 方法，检查本地事务。
     * 在事务消息长事件未被提交或回滚时，RocketMQ 会回查事务消息对应的生产者分组下的 Producer ，获得事务消息的状态。此时，该方法就会被调用。
     * 一般来说，有两种方式实现本地事务回查时，返回事务消息的状态：
     * 第一种，通过 msg 消息，获得某个业务上的标识或者编号，然后去数据库中查询业务记录，从而判断该事务消息的状态是提交还是回滚。
     * 第二种，记录 msg 的事务编号，与事务状态到数据库中。
     *    第一步，在 #executeLocalTransaction(...) 方法中，先存储一条 id 为 msg 的事务编号，状态为 RocketMQLocalTransactionState.UNKNOWN 的记录。
     *    第二步，调用带有事务的业务 Service 的方法。在该 Service 方法中，在逻辑都执行成功的情况下，更新 id 为 msg 的事务编号，状态变更为 RocketMQLocalTransactionState.COMMIT
     *    第三步，要以 try-catch 的方式，调用业务 Service 的方法。如此，如果发生异常，回滚事务的时候，可以在 catch 中，更新 id 为 msg 的事务编号的记录的状态为 RocketMQLocalTransactionState.ROLLBACK 。
     *    极端情况下，可能更新失败，则打印 error 日志，告警知道，人工介入
     *    如此三步之后，我们在 #executeLocalTransaction(...) 方法中，就可以通过查找数据库，id 为 msg 的事务编号的记录的状态，然后返回。
     * @param msg messages
     * @return Transaction state
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        System.out.println("check: " + new String(msg.getBody()));
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
