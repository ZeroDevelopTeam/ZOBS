package com.zero.logic.dao;

import com.zero.logic.domain.OrderBook;
import org.springframework.data.repository.CrudRepository;

/**
 * 订单图书类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
public interface OrderBookDao extends CrudRepository<OrderBook,Integer>{

}
