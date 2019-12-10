package com.hjmos.springbootrocketmq.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

/**
 * 执行本地事务(转账业务)，由客户端回调
 */
@Component
@Slf4j
public class TransactionExecuterImpl implements LocalTransactionExecuter {
    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
        if (msg.getTags().equals("Transaction_rollback")) {
            //这里有一个分阶段提交的概念
            log.info("这里是处理业务逻辑，失败情况下进行ROLLBACK。"+"msg=" + new String(msg.getBody())+" arg = " + arg);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.COMMIT_MESSAGE;
        //return LocalTransactionState.UNKNOW;
    }
}
