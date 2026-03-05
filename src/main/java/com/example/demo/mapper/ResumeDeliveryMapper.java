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
     * 根据用户账号查询投递的岗位ID列表
     */
    @Select("SELECT position_id FROM resume_delivery WHERE user_account = #{userAccount}")
    List<Integer> selectPositionIdsByUserAccount(@Param("userAccount") String userAccount);

    /**
     * 根据岗位ID查询投递的用户账号列表
     */
    @Select("SELECT user_account FROM resume_delivery WHERE position_id = #{positionId}")
    List<String> selectUserAccountsByPositionId(@Param("positionId") Integer positionId);

    /**
     * 根据岗位ID查询未被拒绝的投递用户账号列表
     * 过滤掉状态为0（已拒绝）的记录
     */
    @Select("SELECT user_account FROM resume_delivery WHERE position_id = #{positionId} AND delivery_status != 0")
    List<String> selectNonRejectedUserAccountsByPositionId(@Param("positionId") Integer positionId);

    /**
     * 检查是否存在指定的简历投递记录
     */
    @Select("SELECT COUNT(*) FROM resume_delivery WHERE user_account = #{userAccount} AND position_id = #{positionId}")
    int checkDeliveryExists(@Param("userAccount") String userAccount, @Param("positionId") Integer positionId);

    /**
     * 查询指定记录的当前状态
     */
    @Select("SELECT delivery_status FROM resume_delivery WHERE user_account = #{userAccount} AND position_id = #{positionId}")
    Integer getCurrentStatus(@Param("userAccount") String userAccount, @Param("positionId") Integer positionId);

    /**
     * 修改投递状态为已拒绝(0)
     * 任何时候都可以拒绝
     */
    @Update("UPDATE resume_delivery SET delivery_status = 0, status_update_time = NOW() WHERE user_account = #{userAccount} AND position_id = #{positionId} AND delivery_status != 0")
    int updateStatusToRejected(@Param("userAccount") String userAccount, @Param("positionId") Integer positionId);

    /**
     * 修改投递状态为邀约面试(2)
     * 只能从未处理状态邀约
     */
    @Update("UPDATE resume_delivery SET delivery_status = 2, status_update_time = NOW() WHERE user_account = #{userAccount} AND position_id = #{positionId} AND delivery_status = 1")
    int updateStatusToInterview(@Param("userAccount") String userAccount, @Param("positionId") Integer positionId);

    /**
     * 修改投递状态为面试通过(3)
     * 只能从邀约面试状态修改为面试通过
     */
    @Update("UPDATE resume_delivery SET delivery_status = 3, status_update_time = NOW() WHERE user_account = #{userAccount} AND position_id = #{positionId} AND delivery_status = 2")
    int updateStatusToPassed(@Param("userAccount") String userAccount, @Param("positionId") Integer positionId);

    /**
     * 根据用户账号查询投递记录详情（包含岗位信息和投递状态）
     */
    @Select("SELECT " +
            "p.id as positionId, " +
            "p.employer_account as employerAccount, " +
            "p.job_description as jobDescription, " +
            "p.salary_min as salaryMin, " +
            "p.salary_max as salaryMax, " +
            "p.work_location as workLocation, " +
            "p.experience_requirement as experienceRequirement, " +
            "p.education_requirement as educationRequirement, " +
            "p.update_time as positionCreateTime, " +
            "p.status as positionStatus, " +
            "p.is_campus_job as isCampusJob, " +
            "rd.user_account as userAccount, " +
            "rd.delivery_status as deliveryStatus, " +
            "rd.create_time as deliveryCreateTime, " +
            "rd.status_update_time as statusUpdateTime " +
            "FROM resume_delivery rd " +
            "JOIN job_info p ON rd.position_id = p.id " +
            "WHERE rd.user_account = #{userAccount}")
    List<com.example.demo.entity.vo.UserDeliveryVO> selectDeliveriesWithPositionInfo(@Param("userAccount") String userAccount);

}
