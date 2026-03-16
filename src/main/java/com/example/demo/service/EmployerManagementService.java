package com.example.demo.service;

import com.example.demo.entity.Employer;
import com.example.demo.mapper.EmployerMapper;
import com.example.demo.common.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业信息管理服务类
 * 专注于企业信息的 CRUD 操作
 */
@Service
public class EmployerManagementService {

    private final EmployerMapper employerMapper;
    public EmployerManagementService(EmployerMapper employerMapper) {
        this.employerMapper = employerMapper;
    }

    /**
     * 获取企业信息
     */
    public Employer getEmployerInfo(String employerAccount){
        return employerMapper.selectEmployer(employerAccount);
    }

    /**
     * 保存或更新企业信息（upsert 操作）
     */
    public String upsertEmployer(
            String employerAccount, 
            String companyName,
            String contactPhone,
            String contactEmail,
            String companyProfile
    ){
        // 参数验证
        String errorMsg = ValidationUtils.validateEmployerParams(employerAccount, companyName, contactPhone, contactEmail, companyProfile);
        if (errorMsg != null) {
            return errorMsg;
        }
        
        int rows = employerMapper.upsertEmployer(createEmployer(
            employerAccount, 
            companyName,
            contactPhone,
            contactEmail,
            companyProfile
        ));
        
        return rows > 0 ? "企业信息保存成功" : "企业信息保存失败";
    }

    /**
     * 创建企业对象的私有辅助方法
     */
    private Employer createEmployer(
            String employerAccount, 
            String companyName,
            String contactPhone,
            String contactEmail,
            String companyProfile
    ) {
        Employer employer = new Employer();
        employer.setEmployerAccount(employerAccount);
        employer.setCompanyName(companyName);
        employer.setContactPhone(contactPhone);
        employer.setContactEmail(contactEmail);
        employer.setCompanyProfile(companyProfile);
        return employer;
    }
}
