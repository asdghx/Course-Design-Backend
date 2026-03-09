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
     * 分页查询岗位信息（支持按学校名称过滤）
     * @param page 分页对象
     * @param employerAccount 企业账号（可选）
     * @param universityName 学校名称（可选，为空表示校外岗位，有值表示对应学校的校内岗位）
     * @return 岗位分页结果
     */
    IPage<Position> selectPositionPage(Page<Position> page, 
                                       @Param("employerAccount") String employerAccount,
                                       @Param("universityName") String universityName);
    
    /**
     * HR 管理岗位分页查询（只按企业账号过滤，不限制学校和状态）
     * @param page 分页对象
     * @param employerAccount 企业账号（可选）
     * @return 岗位分页结果
     */
    IPage<Position> selectHrPositions(Page<Position> page, 
                                      @Param("employerAccount") String employerAccount);
    
    /**
     * 获取随机岗位列表
     * @return 随机岗位列表
     */
    List<Position> selectRandomPositions();
    
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
    
    /**
     * 根据经纬度范围查询岗位 (粗略过滤)
     * @param minLat 最小纬度
     * @param maxLat 最大纬度
     * @param minLon 最小经度
     * @param maxLon 最大经度
     * @return 岗位列表
     */
    List<Position> selectByLocationRange(
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLon") Double minLon,
        @Param("maxLon") Double maxLon
    );
    
    /**
     * 批量查询岗位信息
     * @param positionIds 岗位 ID 列表
     * @return 岗位列表
     */
    @Select("<script>SELECT id, employer_account, salary_min, salary_max, job_description, work_location, university_name, latitude, longitude, status, create_time FROM position WHERE id IN " +
           "<foreach item='id' collection='positionIds' open='(' separator=',' close=')'>#{id}</foreach>" +
           "</script>")
    List<Position> selectBatchIds(@Param("positionIds") List<Integer> positionIds);
}