package com.zero.logic.controller;

import com.zero.logic.dao.BookTypeDao;
import com.zero.logic.domain.BookType;
import com.zero.logic.util.JsonUtil;
import io.swagger.annotations.ApiOperation;
import org.hibernate.annotations.Cascade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 图书分类控制类
 * @autherAdmin Deram Zhao
 * @creat 2017/6/15
 */
@RestController
@RequestMapping("bookType")
public class BookTypeController {
    @Autowired
    private BookTypeDao bookTypeDao;

    @RequestMapping(value = "/addBookType",method = RequestMethod.POST)
    @ApiOperation(value = "BookType",notes = "新增图书分类")
    public String addCategory(@RequestBody BookType bookType){
        try {
            if (null!= bookType){
                Date date = new Date();
                bookType.setCreateDate(date);
                bookType.setUpdateDate(date);
                bookTypeDao.save(bookType);
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"新增图书分类失败");
        }
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"新增图书分类成功");
    }

    @RequestMapping(value = "/editBookType",method = RequestMethod.POST)
    @ApiOperation(value = "BookType",notes = "修改图书分类")
    public String editCategory(@RequestBody BookType bookType){
        try {
            if (null!= bookType){
                bookType.setUpdateDate(new Date());//修改时间
                bookTypeDao.save(bookType);
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改图书分类成功");
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"修改图书分类失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"修改图书分类失败");
        }
    }

    @RequestMapping(value = "/getTree",method = RequestMethod.GET)
    @ApiOperation(value = "获取分类树",notes = "获取分类树")
    public String getCategoryTree(){
        List<BookType> list = new ArrayList<>();
        try {
            BookType rootNote = bookTypeDao.getBookTypeByTypeId("00");//获取根目录节点
            String strList = null;
            Iterable<BookType> allBookType = bookTypeDao.findAll();
            Iterator<BookType> iter = allBookType.iterator();
            while (iter.hasNext()){
                BookType bookType = iter.next();
                if (!"".equals(bookType.getParent())){
                    list.add(bookType);
                }
            }
            String tree = recursionBookType(list,rootNote,strList);
            return tree;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取子节点列表
     * @param list
     * @param node
     * @return
     */
    public List<BookType> getChildList(List<BookType>list,BookType node){

        List<BookType> nodeList = new ArrayList<>();
        Iterator<BookType> it =list.iterator();
        while (it.hasNext()){
            BookType bookType = it.next();
            if (bookType.getParent().equals(node.getTypeId())){
                nodeList.add(bookType);
            }
        }
        return nodeList;
    }

    public String recursionBookType(List<BookType> list, BookType node,String strList) throws Exception {
        List<BookType> childList = getChildList(list, node);//得到子节点列表
        if (childList.size()>0){
            Map<String,Object> map =new HashMap<>();
            map.put("childList",childList);
            strList = JsonUtil.makeJsonBeanByKey(node,map);
            Iterator<BookType> it = childList.iterator();

            while (it.hasNext()){
                BookType bookType = it.next();
                recursionBookType(list,bookType,strList);
            }
        }
        return strList;
    }
}
