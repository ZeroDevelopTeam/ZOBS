package com.zero.logic.domain;

import java.io.Serializable;

/**
 * 购物车复合主键
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
public class ShopCartPK implements Serializable {
    //购物车编号
    private String shopCartId;
    //书籍编号
    private String bookId;

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
}
