package com.example.demo.entity.vo;

import com.example.demo.entity.Position;
import lombok.Data;

import java.util.Date;

/**
 * 用户投递记录视图对象
 * 用于展示用户投递的岗位详情及投递状态
 */
@Data
public class UserDeliveryVO {
    // 来自Position表的字段
    private Integer positionId;
    private String employerAccount;
    private String jobDescription;
    private String salaryRange;
    private String workLocation;
    private String experienceRequirement;
    private String educationRequirement;
    private Date positionCreateTime;
    private Integer positionStatus;
    private Integer isCampusJob; // 是否校内岗位：1=是，0=否
    
    // 来自ResumeDelivery表的字段
    private String userAccount;
    private Integer deliveryStatus;
    private Date deliveryCreateTime;
    private Date statusUpdateTime;
    
    // 状态描述
    private String statusDesc;
    
    /**
     * 根据状态码设置状态描述
     */
    public void setStatusDescByCode(Integer statusCode) {
        if (statusCode != null) {
            switch (statusCode) {
                case 1: this.statusDesc = "已投递"; break;
                case 2: this.statusDesc = "邀约面试"; break;
                case 3: this.statusDesc = "面试通过"; break;
                case 4: this.statusDesc = "已入职"; break;
                case 0: this.statusDesc = "已拒绝"; break;
                default: this.statusDesc = "未知状态";
            }
        }
    }
}