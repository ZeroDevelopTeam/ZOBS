package com.zero.logic.controller;

import com.zero.logic.dao.OrderBookDao;
import com.zero.logic.dao.OrderDao;
import com.zero.logic.domain.Order;
import com.zero.logic.domain.OrderBook;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 订控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/19
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderBookDao orderBookDao;

    @RequestMapping(value = "/addOrder",method = RequestMethod.POST)
    @ApiOperation(value = "生成订单",notes = "生成订单信息")
    public String addOrder(Order order,@RequestBody List<OrderBook> orderBooks){
        try {
            for (OrderBook orderBook:orderBooks){
                orderBook.setCreateDate(new Date());
                orderBook.setUpdateDate(new Date());
                orderBookDao.save(orderBook);
            }
            order.setCreateDate(new Date());
            order.setUpdateDate(new Date());
            orderDao.save(order);
        }catch (Exception e){

        }

        return "";
    }
}
