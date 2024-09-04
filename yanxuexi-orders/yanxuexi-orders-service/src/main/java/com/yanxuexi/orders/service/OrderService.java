package com.yanxuexi.orders.service;

import com.yanxuexi.messagesdk.model.po.MqMessage;
import com.yanxuexi.orders.model.dto.AddOrderDto;
import com.yanxuexi.orders.model.dto.PayRecordDto;
import com.yanxuexi.orders.model.dto.PayStatusDto;
import com.yanxuexi.orders.model.po.XcPayRecord;

/**
 * @author hyx
 * @version 1.0
 * @description
 * @date 2024-09-04 11:09
 **/
public interface OrderService {
    /**
     * @param userId 用户Id
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付记录(包括二维码)
     * @description 创建商品订单
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @description 查询支付记录
     * @param payNo  交易记录号
     * @return com.xuecheng.orders.model.po.XcPayRecord
     */
    XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * @description: 保存支付宝支付结果
     * @param payStatusDto 支付结果信息
     */
    void saveAliPayStatus(PayStatusDto payStatusDto);

    /**
     * @description: 发送通知结果
     * @param message 消息
     */
    void notifyPayResult(MqMessage message);
}
