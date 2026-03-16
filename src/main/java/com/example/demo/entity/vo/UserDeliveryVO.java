package com.example.demo.entity.vo;

import com.example.demo.entity.Position;
import lombok.Data;

import java.util.Date;

@Data
public class UserDeliveryVO {
    private Integer positionId;
    private String employerAccount;
    private String jobDescription;
    private Integer salaryMin;
    private Integer salaryMax;
    private String salaryUnit;
    private String workLocation;
    private String universityName;
    
    private Integer deliveryStatus;
    private Date deliveryCreateTime;
    private Integer hasUpdate;  // 更新标识：0-已读，1-企业有新操作
    
    private String statusDesc;
    
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