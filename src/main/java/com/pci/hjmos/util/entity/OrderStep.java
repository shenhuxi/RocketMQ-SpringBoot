package com.hjmos.springbootrocketmq.entity;

import java.util.ArrayList;
import java.util.List;

public class OrderStep {

    private long orderId;
    private String desc;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "OrderStep{" +
                "orderId=" + orderId +
                ", desc='" + desc + '\'' +
                '}';
    }

    public static List<OrderStep> buildOrdes(){

        List<OrderStep> orderSteps = new ArrayList<OrderStep>();

        /**
         * 1342L  创建  推送  付款  完成
         * 1352L  创建  付款  完成
         * 132L  创建  付款  完成
         */
        OrderStep orderStep = new OrderStep();
        orderStep.setOrderId(1342L);
        orderStep.setDesc("创建");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1352L);
        orderStep.setDesc("创建");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1362L);
        orderStep.setDesc("创建");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1342L);
        orderStep.setDesc("推送");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1352L);
        orderStep.setDesc("付款");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1342L);
        orderStep.setDesc("付款");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1362L);
        orderStep.setDesc("付款");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1362L);
        orderStep.setDesc("完成");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1342L);
        orderStep.setDesc("完成");
        orderSteps.add(orderStep);

        orderStep = new OrderStep();
        orderStep.setOrderId(1352L);
        orderStep.setDesc("完成");
        orderSteps.add(orderStep);

        return orderSteps;
    }
}
