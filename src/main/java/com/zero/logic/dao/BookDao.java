package com.zero.logic.dao;

import com.zero.logic.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


/**
 * 图书类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
public interface BookDao extends CrudRepository<Book,Integer> {

    /**
     * 模糊查询分页获取图书
     * @param keyWord
     * @param pageable
     * @return
     */
    @Query("select t from Book t where t.bookName like %?1% or t.bookId like %?1% or t.author like %?1% or t.bookDesc like %?1% ")
    Page<Book> findByBookName(@Param("keyWord")String keyWord, Pageable pageable);

    /**
     * 模糊查询图书数量
     * @param keyWord
     * @return
     */
    @Query("select t from Book t where t.bookName like %?1% or t.bookId like %?1% or t.author like %?1% or t.bookDesc like %?1% ")
    public long count(@Param("keyWord")String keyWord);

    /**
     * 根据图书编号获取图书
     * @param bookId
     * @return
     */
    public Book getBookByBookId(String bookId);
}
