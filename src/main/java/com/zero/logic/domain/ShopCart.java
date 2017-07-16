package com.zero.logic.domain;

import com.zero.basic.domain.BasicBean;

import javax.persistence.*;

/**
 *
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
@Entity
@IdClass(ShopCartPK.class)
@Table(name = "sys_shop_cart")
public class ShopCart extends BasicBean{
    //
    @Id
    @Column(name = "SHOPCARTID")
    private String shopCartId;

    @Id
    @Column(name = "BOOKID")
    private String bookId;

    @Column(name = "BOOKNUM")
    private int bookNum;



    public String getShopCartId() {
        return shopCartId;
    }

    public void setShopCartId(String shopCartId) {
        this.shopCartId = shopCartId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getBookNum() {
        return bookNum;
    }

    public void setBookNum(int bookNum) {
        this.bookNum = bookNum;
    }
}
