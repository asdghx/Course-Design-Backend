package com.example.demo.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Employer;
import com.example.demo.entity.Position;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.mapper.EmployerMapper;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Position VO 转换工具类
 * 提供 Position 到 PositionVO 的转换功能，包含企业信息的查询和填充
 * 
 * 【复用场景】：所有需要返回岗位 VO 的 Service 层
 */
public class PositionVOConverter {

    /**
     * 将单个 Position 转换为 PositionVO
     * 
     * @param position 岗位实体
     * @param employerMapper 企业 Mapper（用于查询企业名称）
     * @return PositionVO，如果 position 为 null 则返回 null
     */
    public static PositionVO convertToVO(Position position, EmployerMapper employerMapper) {
        if (position == null) {
            return null;
        }
        
        PositionVO vo = new PositionVO();
        // 复制基础字段（因为继承了 Position，会自动复制）
        BeanUtils.copyProperties(position, vo);
        
        // 查询并设置 companyName
        if (position.getEmployerAccount() != null) {
            Employer employer = employerMapper.selectEmployer(position.getEmployerAccount());
            vo.setCompanyName(employer != null ? employer.getCompanyName() : null);
        }
        
        return vo;
    }
    
    /**
     * 批量将 Position 列表转换为 PositionVO 列表
     * 
     * @param positions Position 列表
     * @param employerMapper 企业 Mapper（用于查询企业名称）
     * @return PositionVO 列表，空列表或空数据时返回空 ArrayList
     */
    public static List<PositionVO> convertToVOList(List<Position> positions, EmployerMapper employerMapper) {
        if (positions == null || positions.isEmpty()) {
            return new ArrayList<>();
        }
        return positions.stream()
                .map(p -> convertToVO(p, employerMapper))
                .collect(Collectors.toList());
    }
    
    /**
     * 从分页结果中提取记录并转换为 VO 列表
     * 
     * @param positionPage 岗位分页结果
     * @param employerMapper 企业 Mapper
     * @return PositionVO 列表
     */
    public static List<PositionVO> convertFromPageRecords(IPage<Position> positionPage, EmployerMapper employerMapper) {
        if (positionPage == null || positionPage.getRecords() == null || positionPage.getRecords().isEmpty()) {
            return new ArrayList<>();
        }
        return convertToVOList(positionPage.getRecords(), employerMapper);
    }
}
