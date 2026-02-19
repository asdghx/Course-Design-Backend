package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.ResumeDelivery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeDeliveryMapper extends BaseMapper<ResumeDelivery> {

    /**
     * 插入简历投递记录
     */
    @Insert("INSERT INTO resume_delivery (user_account, position_id) VALUES (#{userAccount}, #{positionId})")
    int insertResumeDelivery(ResumeDelivery resumeDelivery);

    /**
     * 根据用户账号查询投递记录
     */
    @Select("SELECT * FROM resume_delivery WHERE user_account = #{userAccount}")
    List<ResumeDelivery> selectByUserAccount(@Param("userAccount") String userAccount);

    /**
     * 根据岗位ID查询投递记录
     */
    @Select("SELECT * FROM resume_delivery WHERE position_id = #{positionId}")
    List<ResumeDelivery> selectByPositionId(@Param("positionId") Long positionId);

    /**
     * 根据用户账号查询投递的岗位ID列表
     */
    @Select("SELECT position_id FROM resume_delivery WHERE user_account = #{userAccount}")
    List<Integer> selectPositionIdsByUserAccount(@Param("userAccount") String userAccount);

    /**
     * 根据岗位ID查询投递的用户账号列表
     */
    @Select("SELECT user_account FROM resume_delivery WHERE position_id = #{positionId}")
    List<String> selectUserAccountsByPositionId(@Param("positionId") Long positionId);


}
