package com.zero.logic.controller;

import com.zero.logic.dao.BookDao;
import com.zero.logic.domain.Book;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
@RestController
@RequestMapping("book")
public class BookController {
    @Autowired
    private BookDao bookDao;

    @RequestMapping(value = "/getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取图书",notes = "分页获取图书")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        String result =null;
        Sort sort = new Sort(Sort.Direction.DESC,"bookId");
        Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
        Page<Book> books = bookDao.findByBookName(keyWord,pageable);
        List<Object> list = new ArrayList<>();
        for (Book book:books){
            list.add(book);
        }
        long total = bookDao.count(keyWord);
        long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
        result = TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        return result;
    }


    @RequestMapping(value = "/addBook",method = RequestMethod.POST)
    @ApiOperation(value = "新增图书",notes = "新增图书")
    public String addBook(@RequestBody Book book){
        try {
            bookDao.save(book);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增图书成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"新增图书失败");
        }
    }
    @RequestMapping(value = "/editBook",method = RequestMethod.POST)
    @ApiOperation(value = "修改图书",notes = "修改图书")
    public String editBook(@RequestBody Book book){
        try {
            Book oldBook = bookDao.getBookByBookId(book.getBookId());
            if(null!=oldBook){
                bookDao.save(book);
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改图书成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改图书失败");
        }
    }

    @RequestMapping(value = "/deleteBook",method = RequestMethod.POST)
    @ApiOperation(value = "删除图书",notes = "删除图书")
    public String deleteBook(@RequestBody Book book){
        try {
            Book oldBook = bookDao.getBookByBookId(book.getBookId());
            if(null!=oldBook){
                bookDao.delete(book);
                //删除对应图片
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除图书成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除图书失败");
        }
    }
}
