package com.zero.logic.dao;

import com.zero.logic.domain.Purview;
import com.zero.logic.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 权限接口
 * @auther Deram Zhao
 * @creatTime 2017/6/9
 */
public interface PurviewDao extends CrudRepository<Purview,Integer> {
    /**
     * 根据权限编号获取权限信息
     * @param purviewId
     * @return Purview
     */
    public Purview getPurviewByPurviewId(String purviewId);

    /**
     * 角色模糊分页查询
     * @param keyWord
     * @param pageable
     * @return
     */
    @Query("select t from Purview t where t.purviewName like %?1% or t.purviewId like %?1% or t.purviewDesc like %?1%")
    public Page<Purview> findByPurviewName(@Param("keyWord") String keyWord, Pageable pageable);


    /**
     * 获取模糊查询记录数
     * @param keyWord
     * @return
     */
    @Query("select t from Purview t where t.purviewName like %?1% or t.purviewId like %?1% or t.purviewDesc like %?1%")
    public long count(@Param("keyWord")String keyWord);

}
