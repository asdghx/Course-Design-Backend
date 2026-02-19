package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Position;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 岗位信息Mapper接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD功能
 * 自定义扩展方法用于岗位相关的业务查询
 */
@Repository
public interface PositionMapper extends BaseMapper<Position> {
    
    /**
     * 分页查询岗位信息
     * @param page 分页对象
     * @param employerAccount 企业账号（可选）
     * @return 岗位分页结果
     */
    IPage<Position> selectPositionPage(Page<Position> page, @Param("employerAccount") String employerAccount);
    
    /**
     * 获取随机岗位列表
     * @return 随机岗位列表
     */
    List<Position> selectRandomPositions();
    
    /**
     * 根据企业账号统计有效岗位数量
     * @param employerAccount 企业账号
     * @return 有效岗位数量
     */
    int countActivePositions(@Param("employerAccount") String employerAccount);
    
    /**
     * 软删除岗位
     * @param positionId 岗位ID
     * @return 影响行数
     */
    int softDeleteById(@Param("positionId") Integer positionId);
    
    /**
     * 插入岗位信息
     * @param position 岗位对象
     * @return 影响行数
     */
    int insertPosition(Position position);
    
    /**
     * 更新岗位信息
     * @param position 岗位对象
     * @return 影响行数
     */
    int updatePosition(Position position);
}