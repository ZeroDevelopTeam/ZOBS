package com.zero.logic.dao;
import com.zero.logic.domain.BookType;
import org.springframework.data.repository.CrudRepository;

/**
 * 图书分类接口
 * @auther Deram Zhao
 * @creatTime 2017/6/15
 */
public interface BookTypeDao extends CrudRepository<BookType,Integer> {

    /**
     * 根据图书分类id获取图书分类
     * @param typeId 类型编号
     * @return 图书分类
     */
    public BookType getBookTypeByTypeId(String typeId);
}
