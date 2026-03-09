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
    // 来自 Position 表的字段
    private Integer positionId;
    private String employerAccount;
    private String jobDescription;
    private Integer salaryMin;
    private Integer salaryMax;
    private String workLocation;
    private String universityName; // 大学名称，为空表示校外岗位
    
    // 来自 ResumeDelivery 表的字段
    private Integer deliveryStatus;
    private Date deliveryCreateTime;
    
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