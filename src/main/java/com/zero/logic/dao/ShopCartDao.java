package com.zero.logic.dao;

import com.zero.logic.domain.ShopCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
public interface ShopCartDao extends CrudRepository<ShopCart,Integer>{
    /**
     * 根据 shopCartId和bookId获取购物车信息
     * @param shopCartId
     * @param bookId
     * @return 购物车信息
     */
    @Query("select t  from ShopCart t where t.shopCartId=:shopCartId and t.bookId=:bookId")
    public ShopCart getByShopCartIdAndBookId(@Param("shopCartId") String shopCartId,@Param("bookId") String bookId);


    /**
     * 根据 createUser和bookId获取购物车信息
     * @param createUser
     * @param bookId
     * @return 购物车信息
     */
    @Query("select t  from ShopCart t where t.createUser=:createUser and t.bookId=:bookId")
    public ShopCart getByShopCartIdAndCreateUser(@Param("createUser") String createUser,@Param("bookId") String bookId);


    /**
     * 根据createUser分页获取购物车信息（createUser对应用户表的userCode）
     * @param createUser
     * @param pageable
     * @return
     */
    @Query("select t from ShopCart t where t.createUser=:createUser")
    public Page<ShopCart> getShopCartsByCreateUser(@Param("createUser") String createUser, Pageable pageable);

    @Query("select count(*) from ShopCart t where t.createUser=:createUser")
    public long countByCreateUser(@Param("createUser") String createUser);

    /**
     * 根据shopCartId清空购物车里的货物信息
     * @param shopCartId
     * @return
     */
    @Transactional
    @Modifying
    @Query("delete from ShopCart t where t.shopCartId=:shopCartId")
    public void deleteByShopCartId(@Param("shopCartId") String shopCartId);

    /**
     * 根据shopCartId和bookId删除购物车的货物信息
     * @param shopCartId
     * @param bookId
     */
    @Transactional
    @Modifying
    @Query("delete from ShopCart t where t.shopCartId=:shopCartId and t.bookId=:bookId")
    public void deleteByShopCartIdAndAndBookId(@Param("shopCartId") String shopCartId,@Param("bookId") String bookId);

    /**
     * 根据用户获取购物车的信息
     * @param createUser
     * @return 购物车信息
     */
    public List<ShopCart> getShopCartsByCreateUser(String createUser);
}
