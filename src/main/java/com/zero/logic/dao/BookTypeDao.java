package com.zero.logic.dao;
import com.zero.logic.domain.BookType;
import org.springframework.data.repository.CrudRepository;

/**
 * 图书分类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/15
 */
public interface BookTypeDao extends CrudRepository<BookType,Integer> {

    /**
     * 根据图书分类id获取图书分类
     * @param typeId
     * @return
     */
    public BookType getBookTypeByTypeId(String typeId);
}
