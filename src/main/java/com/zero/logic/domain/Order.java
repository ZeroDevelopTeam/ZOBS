package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;
import com.zero.logic.util.DateUtil;

import javax.jws.soap.SOAPBinding;
import javax.persistence.*;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 订单类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
@Entity
@Table(name = "sys_order")
public class Order extends BasicBean{
    //订单id
    @Id
    @Column(name = "ORDERID")
    private String orderId;
    //订单状态 //废止订单-1，交易成功0，已付款待配送1，3交易关闭，配送中2；未付款4，退款5，退款退货6
    @Column(name = "STATE")
    private int state;
    //订单地址
    @Column(name = "ADDRESS")
    private String address;
    //收货人
    @Column(name = "RECEIVER")
    private String receiver;
    //收货人电话
    @Column(name = "PHONE")
    private String phone;
    //送货人
    @Column(name = "DELIVERY")
    private String delivery;
    //送货时间
    @Column(name = "DELIVERYDATE")
    private Date deliveryDate;
    //收获时间
    @Column(name = "RECEIVERDATE")
    private Date receiverDate;


    //setter和getter方法
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getDeliveryDate() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,deliveryDate);
    }

    public void setDeliveryDate(Date deliveryDate) {

        this.deliveryDate = deliveryDate;
    }

    public String getReceiverDate() throws ParseException {
        return DateUtil.formatDate(DateUtil.FORMAT2,receiverDate);
    }

    public void setReceiverDate(Date receiverDate) {
        this.receiverDate = receiverDate;
    }

}
