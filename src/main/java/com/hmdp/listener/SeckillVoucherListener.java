package com.hmdp.listener;

import com.hmdp.entity.VoucherOrder;
import com.hmdp.service.impl.VoucherOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SeckillVoucherListener {

    private VoucherOrderServiceImpl voucherOrderService;

    public SeckillVoucherListener(VoucherOrderServiceImpl voucherOrderService) {
        this.voucherOrderService = voucherOrderService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1",
                    durable = "true",
                    arguments = @Argument(name = "x-queue-mode", value = "lazy")),// 设置消息队列默认为持久化和开启lazy队列模式（消息会直接存入磁盘，避免内存爆仓。）
            exchange = @Exchange(name = "seckill.direct", type = ExchangeTypes.DIRECT),//设置交换机名称和类型，默认为DIRECT类型与自动持久化
            key = {"seckill.order"} //设定RoutingKey
    ),
            concurrency = "1-10" // 启动动态线程池（最低1个，最多10个）并发消费
    )
    public void receiveMessage(VoucherOrder voucherOrder, Message message) {
        log.debug("接收到的消息 ID:{} ",message.getMessageProperties().getMessageId());
        log.debug("线程: {} - \n收到优惠券订单消息：{}",Thread.currentThread().getName(), voucherOrder);
        voucherOrderService.createVoucherOrder(voucherOrder);
    }

}
