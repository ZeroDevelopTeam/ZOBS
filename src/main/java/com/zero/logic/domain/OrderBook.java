package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;

import javax.persistence.*;

/**
 * 订单图书实体类
 * @auther Deram Zhao
 * @creatTime 2017/6/19
 */
@Entity
@IdClass(OrderBookPK.class)
@Table(name = "sys_order_book")
public class OrderBook extends BasicBean{

    //订单编号
    @Id
    @Column(name="ORDERID")
    private String orderId;
    //图书编号
    @Id
    @Column(name = "BOOKID")
    private String bookId;
    //订购数量
    @Column(name = "ORDERNUM")
    private int orderNum;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

}
