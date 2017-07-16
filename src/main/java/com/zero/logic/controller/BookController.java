package com.zero.logic.controller;

import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.LogDao;
import com.zero.logic.domain.Book;
import com.zero.logic.domain.Log;
import com.zero.logic.util.CodeGeneratorUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import com.zero.logic.util.UploadFilePathUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private LogDao logDao;
    @RequestMapping(value = "/getByPage",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取图书",notes = "分页获取图书")
    public String getByPage(
            @RequestParam("keyWord")String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"createDate");//按照最新时间进行排序
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Book> books = bookDao.findByBookName(keyWord,pageable);
            List<Object> list = new ArrayList<>();
            for (Book book:books){
                list.add(book);
            }
            long total = bookDao.count(keyWord);
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取图书失败");
        }
    }

    @RequestMapping(value = "/getBookByBookId",method = RequestMethod.GET)
    @ApiOperation(value = "获取图书",notes = "根据图书ID获取图书")
    public String getBookByBookId(@RequestParam("bookId")String bookId){
        try {
            Book book = bookDao.getBookByBookId(bookId);
            return JsonUtil.fromObject(book);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取图书失败");
        }
    }

    @RequestMapping(value = "/getBooksByTypeId",method = RequestMethod.GET)
    @ApiOperation(value = "根据分类ID获取图书",notes = "根据分类ID获取图书")
    public String getBooksByTypeId(
           @RequestParam("typeId")String typeId,
           @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
           @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            Sort sort = new Sort(Sort.Direction.DESC,"typeId");
            Pageable pageable = new PageRequest(pageNum-1,pageSize,sort);
            Page<Book> books =bookDao.findBookByTypeId(typeId,pageable);
            List<Object> list = new ArrayList<>();
            for (Book book:books){
                list.add(book);
            }
            long total = bookDao.countAllByTypeId(typeId);
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(list,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"根据分类ID获取图书失败");
        }
    }

    @RequestMapping(value = "/addBook",method = RequestMethod.POST)
    @ApiOperation(value = "新增图书",notes = "新增图书")
    public String addBook(@RequestBody Book book,String [] imgs){
        try {
            book.setCreateDate(new Date());
            book.setUpdateDate(new Date());
            book.setBookId(CodeGeneratorUtil.getBookCode());
            book.setImage_s(imgs);
            bookDao.save(book);
            //记录日志
            logDao.save(new Log(new Date(),new Date(),"新增"+book.getBookId()+"图书成功",0,""));
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
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"修改"+book.getBookId()+"图书成功",0,""));
            }
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改图书成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改图书失败");
        }
    }

    @RequestMapping(value = "/deleteBooks",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除图书",notes = "根据图书编号删除图书")
    public String deleteBook(@RequestParam("bookId") String []bookIds){
        try {
            String filePath = UploadFilePathUtil.uploadFilePath;//图书保存文件夹路径
            String books = "";
            String noBookId="";
            for (int i=0;i<bookIds.length;i++){
                String bookId = bookIds[i];
                Book oldBook = bookDao.getBookByBookId(bookId);
                if (null!=oldBook){
                    bookDao.delete(oldBook);
                    books +=bookId+",";
                    String image_l = oldBook.getImage_l();//图书大图路径
                    String [] image_s = oldBook.getImage_s();//详情图路径
                    if(!"".equals(image_l)){new File(filePath+image_l).delete();}
                    if (image_s.length>0){new File(filePath+image_s).delete();}
                }else {
                    noBookId +=bookId+"、";
                }
            }
            if ("".equals(noBookId)){
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除"+books+"图书成功",0,""));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除图书成功");
            }else {
                //记录日志
                logDao.save(new Log(new Date(),new Date(),"删除"+books+"图书成功",0,""));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"图书"+noBookId+"不存在，其余的图书删除成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除图书失败");
        }
    }
    @RequestMapping(value = "/changeState",method = RequestMethod.POST)
    @ApiOperation(value = "修改图书状态",notes = "修改图书状态")
    public String changeState(@RequestBody Object object){
        try {
            String bookId = JsonUtil.getString("bookId",object);
            int state = Integer.parseInt(JsonUtil.getString("state",object));
            Book oldBook = bookDao.getBookByBookId(bookId);
            oldBook.setState(state);
            bookDao.save(oldBook);
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改状态成功");
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改状态失败");
        }
    }
    @RequestMapping(value = "/getDiscount",method = RequestMethod.GET)
    @ApiOperation(value = "分页获取特价货物",notes = "分页获取特价货物")
    public String getDiscount(
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize){
        try {
            List<Book> books = bookDao.getBooksBy(pageNum-1,pageSize);
            long total = bookDao.countByDiscount();
            long totalPage = total%pageSize==0? total/pageSize:total/pageSize+1;//总页数
            return TableUtil.createTableDate(books,total,pageNum,totalPage,pageSize);
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取打折货物失败");
        }
    }
}
