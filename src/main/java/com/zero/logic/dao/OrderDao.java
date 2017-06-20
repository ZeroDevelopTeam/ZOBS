package com.zero.logic.dao;

import com.zero.logic.domain.Order;
import org.springframework.data.repository.CrudRepository;

/**
 * 订单类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
public interface OrderDao extends CrudRepository<Order,Integer>{

}
