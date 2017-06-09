package com.zero.logic.dao;/**
 * Created by Admin on 2017/6/9.
 */

import com.zero.logic.domain.Purview;
import org.springframework.data.repository.CrudRepository;

/**
 * 权限类接口
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/9
 */
public interface PurviewDao extends CrudRepository<Purview,Integer> {
    /**
     * 根据权限编号获取权限信息
     * @param purviewId
     * @return
     */
    public Purview getPurviewByPurviewId(String purviewId);

}
