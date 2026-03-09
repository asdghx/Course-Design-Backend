package com.example.demo.entity.vo;

import com.example.demo.entity.Position;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位 VO 类
 * 用于岗位推送接口返回，包含企业名称信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PositionVO extends Position {
    private String companyName;  // 企业名称（新增，优先显示）
}
