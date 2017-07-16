package com.zero.logic.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.LogDao;
import com.zero.logic.dao.ShopCartDao;
import com.zero.logic.domain.Book;
import com.zero.logic.domain.Log;
import com.zero.logic.domain.ShopCart;
import com.zero.logic.util.CodeGeneratorUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/7/7
 */
@RestController
@RequestMapping("shopCart")
public class ShopCartController {
    @Autowired
    private ShopCartDao shopCartDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private LogDao logDao;

    @RequestMapping(value = "/addToShopCart",method = RequestMethod.POST)
    @ApiOperation(value = "添加货物到购物车",notes = "添加货物到购物车")
    public String addToShopCart(@RequestBody ShopCart shopCart, HttpServletRequest req){
        try {
            String user_id = req.getHeader("user_id");
            List<ShopCart> carts = shopCartDao.getShopCartsByCreateUser(user_id);
            ShopCart cart=null;
            if (carts.size()>0){
                cart =carts.get(0);//取第一条就行
            }
            if(null==cart){//用户未拥有车
                shopCart.setShopCartId(CodeGeneratorUtil.getShopCartCode());
                shopCart.setCreateUser(user_id);
                shopCart.setCreateDate(new Date());
                shopCart.setUpdateDate(new Date());
                shopCartDao.save(shopCart);
            } else {//向车里添加货物
                ShopCart oldShooCart = shopCartDao.getByShopCartIdAndCreateUser(user_id,shopCart.getBookId());
                if (null!=oldShooCart){//车里已经添加过该种货物，改变货物数量即可
                    oldShooCart.setBookNum(oldShooCart.getBookNum()+shopCart.getBookNum());
                    shopCartDao.save(oldShooCart);
                }else {
                    shopCart.setShopCartId(cart.getShopCartId());
                    shopCart.setCreateUser(cart.getCreateUser());
                    shopCart.setCreateDate(new Date());
                    shopCart.setUpdateDate(new Date());
                    shopCartDao.save(shopCart);
                }
            }
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"购物车"+shopCart.getBookId()+"添加"+shopCart.getBookId()+"货物成功",0,req.getHeader("user_id")));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"添加货物成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"添加货物失败");
        }
    }

    @RequestMapping(value = "/editShopCart",method = RequestMethod.POST)
    @ApiOperation(value = "修改购物车信息",notes = "修改购物车信息")
    public String editShopCart(@RequestParam String shopCartId,@RequestParam String bookId,@RequestParam int bookNum,HttpServletRequest req){
        try {
            ShopCart oldShopCart = shopCartDao.getByShopCartIdAndBookId(shopCartId,bookId);
            oldShopCart.setBookNum(bookNum);
            shopCartDao.save(oldShopCart);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"修改购物车："+shopCartId+"的"+oldShopCart.getBookId()+"信息成功",0,req.getHeader("user_id")));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改失败");
        }
    }
    @RequestMapping(value = "/deleteShopCart",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除物车信息",notes = "删除物车信息")
    public String deleteShopCart(@RequestParam String shopCartId,@RequestParam String bookId,HttpServletRequest req){
        try {
            shopCartDao.deleteByShopCartIdAndAndBookId(shopCartId,bookId);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"删除购"+shopCartId+"物车的"+bookId+"书籍成功",0,req.getHeader("user_id")));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除购物车的货物信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除购物车的货物信息失败");
        }
    }
    @RequestMapping(value = "/emptyShopCart",method = RequestMethod.DELETE)
    @ApiOperation(value = "清空购物车",notes = "清空购物车")
    public String emptyShopCart(@RequestParam String shopCartId,HttpServletRequest req){
        try {
            shopCartDao.deleteByShopCartId(shopCartId);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"清空购物车"+shopCartId+"成功",0,req.getHeader("user_id")));
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"清空购物车成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"清空购物车失败");
        }
    }
    @RequestMapping(value = "/getCartByUserCode",method = RequestMethod.GET)
    @ApiOperation(value = "根据userCode获取购物车信息",notes = "根据userCode分页获取购物车信息")
    public String getCartByUserCode(
            @RequestParam String createUser,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize,HttpServletRequest req){
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "updateDate");
            Pageable pageable = new PageRequest(pageNum-1 , pageSize, sort);
            Page<ShopCart> shopCarts = shopCartDao.getShopCartsByCreateUser(createUser,pageable);
            List<String> list = new ArrayList<>();
            for (ShopCart shopCart:shopCarts){
                Book oldBook = bookDao.getBookByBookId(shopCart.getBookId());
                Map map = new HashMap();
                map.put("bookName",oldBook.getBookName());
                map.put("price",oldBook.getPrice());
                map.put("discount",oldBook.getDiscount());
                map.put("author",oldBook.getAuthor());
                String mapStr = JsonUtil.makeJsonBeanByKey(shopCart,map);
                list.add(mapStr);
            }
            long total = shopCartDao.countByCreateUser(createUser);
            long totalPage = total%pageSize==0?total/pageSize:total/pageSize+1;
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取购物车信息失败");
        }
        }
}
