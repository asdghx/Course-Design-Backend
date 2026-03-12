package com.example.demo.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.entity.Employer;
import com.example.demo.entity.Position;
import com.example.demo.entity.vo.PositionVO;
import com.example.demo.mapper.EmployerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Position VO 转换组件
 * 提供 Position 到 PositionVO 的转换功能，包含企业信息的查询和填充
 */
@Component
public class PositionVOConverter {

    private final EmployerMapper employerMapper;
    public PositionVOConverter(EmployerMapper employerMapper) {
        this.employerMapper = employerMapper;
    }

    /**
     * 将单个 Position 转换为 PositionVO
     *
     * @param position 岗位实体
     * @return PositionVO，如果 position 为 null 则返回 null
     */
    public PositionVO convertToVO(Position position) {
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
     * @return PositionVO 列表，空列表或空数据时返回空 ArrayList
     */
    public List<PositionVO> convertToVOList(List<Position> positions) {
        if (positions == null || positions.isEmpty()) {
            return new ArrayList<>();
        }
        return positions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 从分页结果中提取记录并转换为 VO 列表
     *
     * @param positionPage 岗位分页结果
     * @return PositionVO 列表
     */
    public List<PositionVO> convertFromPageRecords(IPage<Position> positionPage) {
        if (positionPage == null || positionPage.getRecords() == null) {
            return new ArrayList<>();
        }
        return convertToVOList(positionPage.getRecords());
    }
}
