package com.zero.logic.controller;
import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.BookTypeDao;
import com.zero.logic.domain.Book;
import com.zero.logic.domain.BookType;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 图书分类控制类
 * @auther Deram Zhao
 * @creatTime 2017/6/15
 */
@RestController
@RequestMapping("bookType")
public class BookTypeController {
    @Autowired
    private BookTypeDao bookTypeDao;
    @Autowired
    private BookDao bookDao;

    @RequestMapping(value = "/addBookType", method = RequestMethod.POST)
    @ApiOperation(value = "BookType", notes = "新增图书分类")
    public String addBookType(@RequestBody BookType bookType, @RequestParam int level) {
        try {
            //level新增分类等级：-1--第一级，0--下级，1--同级
            if (level == -1) {
                bookType.setParent("00");
            } else if (level == 0) {
                BookType oldBookType = bookTypeDao.getBookTypeByTypeId(bookType.getTypeId());
                bookType.setParent(oldBookType.getTypeId());
            } else if (level == 1) {
                BookType oldBookType = bookTypeDao.getBookTypeByTypeId(bookType.getTypeId());
                bookType.setParent(oldBookType.getParent());
            }
            bookType.setCreateDate(new Date());
            bookType.setUpdateDate(new Date());
            bookTypeDao.save(bookType);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增图书分类失败");
        }
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增图书分类成功");
    }

    @RequestMapping(value = "/editBookType",method = RequestMethod.POST)
    @ApiOperation(value = "修改分类",notes = "修改图书分类")
    public String editBookType(@RequestBody BookType bookType){
        try {
            BookType oldBookType = bookTypeDao.getBookTypeByTypeId(bookType.getTypeId());
            if (null!= oldBookType){
                bookType.setUpdateDate(new Date());//修改时间
                bookType.setCreateDate(DateUtil.parse(DateUtil.FORMAT2,oldBookType.getCreateDate()));
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

    @RequestMapping(value = "/deleteBookType",method = RequestMethod.DELETE)
    @ApiOperation(value = "删除分类",notes = "删除图书分类")
    public String deleteBookType(@RequestParam String typeId){
        try {

            List<BookType> allBookType = (List<BookType>) bookTypeDao.findAll();
            BookType bookType = bookTypeDao.getBookTypeByTypeId(typeId);
            if (null!=bookType){
                if (getChildList(allBookType,bookType).size()==0){//分类是否有子类
                   List<Book> books = bookDao.getBookByTypeId(bookType.getTypeId());
                    if (books.size()==0){//分类是否被图书引用
                        bookTypeDao.delete(bookType);
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,"删除分类成功");
                    }else {
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除失败！该分类有图书引用，不能删除");
                    }
                }else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除失败！该分类有子类，请先删除子类再来删除");
                }
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除分类失败,该分类不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"删除分类失败");
        }
    }

    @RequestMapping(value = "/getTree",method = RequestMethod.GET)
    @ApiOperation(value = "获取分类树",notes = "获取分类树")
    public String getTree(){
        try {
            List<BookType> list = new ArrayList<>();
            BookType rootNote = bookTypeDao.getBookTypeByTypeId("00");//获取根目录节点
           String strList =JsonUtil.fromObject(rootNote);
            Map<String,Object> map =new HashMap<>();
            Iterable<BookType> allBookType = bookTypeDao.findAll();
            Iterator<BookType> iter = allBookType.iterator();
            while (iter.hasNext()){
                BookType bookType = iter.next();
                list.add(bookType);
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
            if (node.getTypeId().equals(bookType.getParent())){
                nodeList.add(bookType);
            }
        }
        return nodeList;
    }


    /**
     * 递归图书分类
     * @param list
     * @param node
     * @param strList
     * @return
     * @throws Exception
     */
    public String recursionBookType(List<BookType> list, BookType node,String strList) throws Exception {
        List<BookType> childList = getChildList(list, node);//得到子节点列表
        if (childList.size()>0){
            Map<String,Object> map = new HashMap<>();
            map.put("children",childList);
            strList = JsonUtil.makeJsonBeanByKey(node,map);
            Iterator<BookType> it = childList.iterator();

            while (it.hasNext()){
                BookType bookType = it.next();
                recursionBookType(list,bookType,strList);
            }
        }
        return strList;
    }

    @RequestMapping(value = "/getBookType",method = RequestMethod.GET)
    @ApiOperation(value = "获取层级分类",notes = "获取层级分类信息")
    public String getBookType(){
        try {
            List<BookType> bookTypeList = new ArrayList<>();
            List<Object> list = new ArrayList<>();
            Iterable<BookType> allBookType = bookTypeDao.findAll();
            Iterator<BookType> iter = allBookType.iterator();
            while (iter.hasNext()){
                BookType bookType = iter.next();
                if (!"".equals(bookType.getParent())){
                    bookTypeList.add(bookType);
                }
            }
            for (BookType bookType:bookTypeList){
                List<BookType> childList = getChildList(bookTypeList, bookType);//得到子节点列表
                Map<String,Object> map =new HashMap<>();
                map.put("children",childList);
                String str = JsonUtil.makeJsonBeanByKey(bookType,map);
                list.add(str);
            }
            return JsonUtil.fromArray(list);

        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取分类失败");
        }

    }

    @RequestMapping(value = "/getBookTypeByTypeId",method = RequestMethod.GET)
    @ApiOperation(value = "获取分类",notes = "通过分类ID获取分类信息")
    public String getBookTypeByTypeId(@RequestParam String typeId){
        try {
            BookType bookType = bookTypeDao.getBookTypeByTypeId(typeId);
            if (null!=bookType){
                return JsonUtil.fromObject(bookType);
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取分类信息失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"获取分类信息失败");
        }
    }
}
