package com.zero.logic.dao;

import com.zero.logic.domain.Purview;
import org.springframework.data.repository.CrudRepository;

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

}
