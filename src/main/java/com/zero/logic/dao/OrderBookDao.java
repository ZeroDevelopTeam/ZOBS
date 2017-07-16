package com.zero.logic.dao;

import com.zero.logic.domain.OrderBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 订单图书类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
public interface OrderBookDao extends CrudRepository<OrderBook,Integer>{

    /**
     * 根据图书ID获取订单图书信息
     * @param bookId
     * @return 订单图书信息
     */
    public OrderBook getOrderBookByBookId(String bookId);

    /**
     * 根据订单ID获取订单图书信息
     * @param orderId
     * @return list 订单图书信息
     */
    public List<OrderBook> getOrderBooksByOrderId(String orderId);

    /**
     * 根据订单ID分页获取订单图书信息
     * @param orderId
     * @param pageable
     * @return 订单图书信息
     */
    @Query("select t from OrderBook t where t.orderId=:orderId")
    public Page<OrderBook> findOrderBooksByOrderId(@Param("orderId")String orderId, Pageable pageable);

    /**
     * 根据订单ID分页获取订单图书信息记录
     * @param orderId
     * @return 记录数量
     */
    @Query("select count(*) from OrderBook t where t.orderId=:orderId")
    public long count(@Param("orderId")String orderId);


    /**
     * 根据已经完场的订单来统计货物销量排行榜
     * @param pagaNum
     * @param pageSize
     * @return
     */
    @Query(value = "select booknum,bookId from sys_order_book join sys_order on (sys_order.state=0) ORDER BY sys_order_book.booknum DESC limit ?1,?2",nativeQuery = true)
    public List<Object> getByBookNum(int pagaNum,int pageSize);


    @Query("select t from OrderBook t where t.orderId=:orderId and t.bookId=:bookId")
    public OrderBook getByOrderIdAndBookId(@Param("orderId") String orderId,@Param("bookId") String bookId);
}
