package com.zero.logic.controller;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.LogDao;
import com.zero.logic.dao.OrderBookDao;
import com.zero.logic.dao.OrderDao;
import com.zero.logic.domain.*;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import java.util.*;

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
    @Autowired
    private BookDao bookDao;
    @Autowired
    private LogDao logDao;

    @RequestMapping(value = "/addOrder",method = RequestMethod.POST)
    @ApiOperation(value = "生成订单",notes = "生成订单信息")
    @Transactional
    public String addOrder(@RequestBody Object obj){
        try {
            String orderId = String.valueOf(System.currentTimeMillis());
            int state = Integer.parseInt(JsonUtil.getString("state",obj));//废止订单-1，已经完成0，已付款待待配送1，配送中2，取消订单3，未付款4；
            String address = JsonUtil.getString("address",obj);
            String receiver = JsonUtil.getString("receiver",obj);
            String phone = JsonUtil.getString("phone",obj);
            String delivery = JsonUtil.getString("delivery",obj);
            String deliveryDate = JsonUtil.getString("deliveryDate",obj);
            String receiverDate = JsonUtil.getString("receiverDate",obj);
            Date createDate = new Date();
            String createUser = "";
            //保存订单图书
            String orderBooks = JsonUtil.getString("orderBookList",obj);
            List<Object> orderBookList =JsonUtil.getList(orderBooks);
            List<OrderBook> list = new ArrayList<>();
            for (Object object:orderBookList){
                OrderBook newOrderBook = new OrderBook();
                String bookId = JsonUtil.getString("bookId",object);
                String bookNum = JsonUtil.getString("bookNum",object);
                newOrderBook.setOrderId(orderId);
                newOrderBook.setBookId(bookId);
                newOrderBook.setBookNum(Integer.parseInt(bookNum));
                newOrderBook.setCreateDate(new Date());
                newOrderBook.setUpdateDate(new Date());
                list.add(newOrderBook);
            }
            if (list.size()<1){
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"请选择你要购买的东西，不能提交空订单");
            }
            orderBookDao.save(list);
            //保存订单
            Order order = new Order();
            order.setOrderId(orderId);
            order.setState(state);
            order.setAddress(address);
            order.setReceiver(receiver);
            order.setPhone(phone);
            order.setDelivery(delivery);
            order.setDeliveryDate(DateUtil.parse(DateUtil.FORMAT2,deliveryDate));
            order.setReceiverDate(DateUtil.parse(DateUtil.FORMAT2,receiverDate));
            order.setCreateDate(createDate);
            order.setCreateUser(createUser);
            orderDao.save(order);

            //记录日志
            logDao.save(new Log(new Date(),new Date(),"新增订单"+orderId+"成功",0,""));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增订单成功");
        }catch (Exception e){
            e.printStackTrace();
            //因为sping 默认trycatch是不进行事务回滚的，可以在手动设置事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"新增订单失败");
        }
    }

    @RequestMapping(value = "/editOrder",method = RequestMethod.POST)
    @ApiOperation(value = "修改订单",notes = "修改订单信息")
    public String editOrder(@RequestBody Order order){
        try {

            //修改订单
            Order oldOrder = orderDao.findOrderByOrderId(order.getOrderId());
            if (null!=oldOrder){
                order.setUpdateDate(new Date());
                order.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldOrder.getCreateDate()));
                orderDao.save(order);
            }
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"修改订单"+oldOrder.getOrderId()+"成功",0,""));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改订单成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改订单失败");
        }
    }
    @RequestMapping(value = "/deleteOrder",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除订单",notes = "根据订单ID删除订单信息")
    public String deleteOrder(@RequestParam String [] orderIds){
        try {
            String unDeletes="";
            String deletes="";
            for (int i=0;i<orderIds.length;i++){
                Order oldOrder = orderDao.findOrderByOrderId(orderIds[i]);
                if (null!=oldOrder && oldOrder.getState()==-1){//只能删除状态为废止的订单
                    orderDao.delete(oldOrder);
                    deletes +=oldOrder.getOrderId()+",";
                }else {
                    unDeletes +=orderIds[i]+",";
                }
            }
            if ("".equals(unDeletes)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除订单成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"除了订单"+unDeletes+"未能删除，其余订单删除成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除订单失败");
        }
    }

    @RequestMapping(value = "getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取订单",notes = "分页获取订单信息")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "orderId");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Order> orders = orderDao.getByOrderId(keyWord,pageable);
            List<Object> list = new ArrayList<>();
            for (Order order:orders){
                List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                double price =0;//订单金额
                for (OrderBook orderBook:orderBooks){
                    Book book = bookDao.getBookByBookId(orderBook.getBookId());
                    int orderNum = orderBook.getBookNum();
                    if (null!=book) {
                        double bookPrice =book.getPrice();
                        price += orderNum*bookPrice;
                    }
                }

                Map map = new HashMap();
                String orderStr = JsonUtil.fromObject(order);
                map.put("price",price);
                String orderMap = JsonUtil.makeJsonBeanByKey(orderStr,map);

                //String orderBooksstr = JsonUtil.fromArray(orderBooks);
                //map.put("detail",orderBooksstr);
                //String str = JsonUtil.makeJsonBeanByKey(orderMap,map);
                list.add(orderMap);
            }
            long total = orderDao.count(keyWord);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"查询订单失败");
        }
    }

    @RequestMapping(value = "/getOrderByOrderId",method = RequestMethod.GET)
    @ApiOperation(value = "根据订单ID获取订单信息",notes = "根据订单ID获取订单信息")
    public String getOrderByOrderId(@RequestParam String orderId){
        try {
            Order oldOrder = orderDao.findOrderByOrderId(orderId);
            List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(orderId);
            double price =0;//订单金额
            for (OrderBook orderBook:orderBooks){
                String bookId = orderBook.getBookId();
                Book book = bookDao.getBookByBookId(bookId);
                if (null!=book){
                    price += orderBook.getBookNum()*book.getPrice();
                }
            }

            Map map = new HashMap();
            String orderStr = JsonUtil.fromObject(oldOrder);
            map.put("price",price);
            String orderMap = JsonUtil.makeJsonBeanByKey(orderStr,map);
            return  orderMap;
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单信息失败");
        }
    }

    @RequestMapping(value = "getDetailByOrderId",method = RequestMethod.GET)
    @ApiOperation(value = "根据订单ID获取订单详情",notes = "根据订单ID获取订单详情信息")
    public String getDetailByOrderId(
            @RequestParam("orderId")String orderId,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "orderId");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<OrderBook> orderBooks = orderBookDao.findOrderBooksByOrderId(orderId,pageable);
            List<Object> orderList = new ArrayList<>();
            for (OrderBook orderBook:orderBooks){
                Book book = bookDao.getBookByBookId(orderBook.getBookId());
                Map map = new HashMap<>();
                map.put("bookName",book.getBookName());
                map.put("storeHouse",book.getStorehouse());
                map.put("price",book.getPrice());
                String strList = JsonUtil.fromObject(orderBook);
                strList = JsonUtil.makeJsonBeanByKey(strList,map);
                orderList.add(strList);
            }
            long total = orderBookDao.count(orderId);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(orderList,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单详情失败");
        }
    }

    @RequestMapping(value = "changeOrderState",method = RequestMethod.POST)
    @ApiOperation(value = "根据订单ID改变订单状态",notes = "根据订单ID改变订单状态情信息")
    public String changeOrderState(@RequestBody Object object){
        //订单状态 //废止订单-1，交易完成0，已付款待配送1，3交易关闭，配送中2；未付款4，退款5，退款退货6
        try {
            String orderId = JsonUtil.getString("orderId",object);
            Order oldOrder = orderDao.findOrderByOrderId(orderId);
            int state = Integer.parseInt(JsonUtil.getString("state",object));

            String bookId = JsonUtil.getString("bookId",object);//如果在改变订单状态时传了bookId就是对订单的其中货物进行操作（退货操作）
            if (!"".equals(bookId)){
               OrderBook orderBook = orderBookDao.getByOrderIdAndBookId(orderId,bookId);
               orderBook.setState(state);
               orderBookDao.save(orderBook);
               oldOrder.setState(state);
                oldOrder.setUpdateDate(new Date());
               orderDao.save(oldOrder);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"退货申请成功，请耐心等待");
            }
           if (state==0){
                oldOrder.setState(state);
                oldOrder.setReceiverDate(DateUtil.parse(DateUtil.FORMAT2,JsonUtil.getString("receiverDate",object)));
                oldOrder.setUpdateDate(new Date());
            }else if (state==2){
                oldOrder.setState(state);
                oldOrder.setReceiver(JsonUtil.getString("receiver",object));
                oldOrder.setDelivery(JsonUtil.getString("delivery",object));
                oldOrder.setPhone(JsonUtil.getString("phone",object));
                oldOrder.setUpdateDate(new Date());
            }else{
                oldOrder.setState(state);
                oldOrder.setUpdateDate(new Date());
            }
            orderDao.save(oldOrder);
            //记录日志

            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改订单状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改订单状态失败");
        }
    }
    /*@RequestMapping(value = "getAllByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode获取所有订单",notes = "根据用户ID获取所有订单信息")
    public String getAllByUserCode(
            @RequestParam("userCode")String userCode,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "updateDate");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Order> orders = orderDao.getByCreateUser(userCode,pageable);
            List<Object> list = new ArrayList<>();
            for (Order order:orders){
                double price =0;//订单金额
                List<Book> books = new ArrayList<>();
                List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                for (OrderBook orderBook:orderBooks){
                    Book book = bookDao.getBookByBookId(orderBook.getBookId());
                    int orderNum = orderBook.getBookNum();
                    if (null!=book) {
                        double bookPrice =book.getPrice()*book.getDiscount();//价格*折扣;
                        book.setBookNum(orderNum);
                        books.add(book);
                        price += orderNum*bookPrice;
                    }
                }
                Map map = new HashMap();
                String orderStr = JsonUtil.fromObject(order);
                map.put("books",books);
                map.put("price",price);
                String orderMap = JsonUtil.makeJsonBeanByKey(orderStr,map);
                list.add(orderMap);
            }
            long total = orderDao.countByCreateUser(userCode);//获取查询总数
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return  JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单信息失败");
        }
    }*/

    @RequestMapping(value = "getAllByUserCodeAndState",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode和state获取所有订单",notes = "根根据userCode和state获取所有订单信息")
    public String getAllByUserCodeAndState(
            @RequestParam("userCode")String userCode,
            @RequestParam("state") int state,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "updateDate");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<Order> orders = null;
            long total=0;//获取查询总数
            if (state==-2){//如果state==-2，就根据userCode查询全部
                 orders = orderDao.getByCreateUser(userCode,pageable);
                 total = orderDao.countByCreateUser(userCode);//获取查询总数
            }else {
                orders = orderDao.getByCreateUseraAndState(userCode,state,pageable);
                total = orderDao.countByCreateUserAndState(userCode,state);//获取查询总数
            }
            List<Object> list = new ArrayList<>();
            for (Order order:orders){
                double price =0;//订单金额
                List<Book> books = new ArrayList<>();
                List<OrderBook> orderBooks = orderBookDao.getOrderBooksByOrderId(order.getOrderId());
                for (OrderBook orderBook:orderBooks){
                    int orderNum = orderBook.getBookNum();
                    Book book = bookDao.getBookByBookId(orderBook.getBookId());
                    //根据orderId和bookId拿到货物在订单中的状态（因为订单中可能有的货物进行退货了）
                   OrderBook obook = orderBookDao.getByOrderIdAndBookId(orderBook.getOrderId(),book.getBookId());
                   book.setState(obook.getState());
                    if (null!=book) {
                        double bookPrice =book.getPrice()*book.getDiscount();//价格*折扣;
                        price += orderNum*bookPrice;
                        book.setBookNum(orderNum);
                        books.add(book);
                    }
                }
                Map map = new HashMap();
                String orderStr = JsonUtil.fromObject(order);
                map.put("price",price);
                map.put("books",books);
                list.add(JsonUtil.makeJsonBeanByKey(orderStr,map));
            }

            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return  JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取订单信息失败");
        }
    }

    @RequestMapping(value = "getTopSell",method = RequestMethod.GET)
    @ApiOperation(value = "获取热销",notes = "获取热销")
    public String getTopSell(
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
              try {
                  List<Object> orderBooks = orderBookDao.getByBookNum(pageNum-1,pageSize);
                  List<Object> list = new ArrayList<>();
                  for (Object object:orderBooks){
                      JSONArray array = null;
                      array = JSONArray.fromObject(object);
                      Map map = new HashMap();
                      for (int i=0;i<array.size();i++){
                          map.put("bookNum",array.get(i));
                          i++;
                          String bookId = array.get(i).toString();
                          Book oldBook = bookDao.getBookByBookId(bookId);
                          map.put("bookId",bookId);
                          map.put("bookName",oldBook.getBookName());
                      }
                      list.add(map);
                  }
                  return JsonUtil.fromArray(list);
              }catch (Exception e){
                  e.printStackTrace();
                  return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取热销书失败");
              }
    }
}
