package com.zero.logic.dao;
import com.zero.logic.domain.BookType;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

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

    /**
     * 根据分类父ID获取分类
     * @param parent 父类Id
     * @return 分类信息
     */
    public BookType getBookTypeByParent(String parent);

}
