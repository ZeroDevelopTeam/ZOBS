package com.zero.logic.dao;

import com.zero.logic.domain.Purview;
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
     * @param purviewId 权限编号
     * @return Purview 权限信息
     */
    public Purview getPurviewByPurviewId(String purviewId);

    /**
     * 角色模糊分页查询
     * @param keyWord 关键字
     * @param pageable 分页
     * @return 权限集合列表
     */
    @Query("select t from Purview t where t.purviewName like %?1% or t.purviewId like %?1% or t.purviewDesc like %?1%")
    public Page<Purview> findByPurviewName(@Param("keyWord") String keyWord, Pageable pageable);

    /**
     * 获取模糊查询记录数
     * @param keyWord 关键字
     * @return 记录数
     */
    @Query("select count(*)from Purview t where t.purviewName like %?1% or t.purviewId like %?1% or t.purviewDesc like %?1%")
    public long count(@Param("keyWord")String keyWord);

}
