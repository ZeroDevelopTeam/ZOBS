package com.zero.logic.dao;

import com.zero.logic.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * 角色类接口
 * @auther Deram Zhao
 * @creatTime 2017/6/8
 */
public interface RoleDao extends CrudRepository<Role,Integer> {
    /**
     * 根据角色编号获取角色信息
     * @param roleId
     * @return
     */
    public Role getRoleByRoleId(String roleId);


    /**
     * 角色模糊分页查询
     * @param keyWord
     * @param pageable
     * @return
     */
   @Query("select t from Role t where t.roleName like %?1% or t.roleId like %?1% or t.roleDesc like %?1%")
   public Page<Role> findByRoleName(@Param("keyWord") String keyWord, Pageable pageable);


    /**
     * 获取模糊查询记录数
     * @param keyWord
     * @return
     */
    @Query("select count(*) from Role t where t.roleName like %?1% or t.roleId like %?1% or t.roleDesc like %?1%" )
    public long count(@Param("keyWord")String keyWord);
}
