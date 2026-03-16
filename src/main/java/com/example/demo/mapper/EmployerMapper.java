package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Employer;
import org.apache.ibatis.annotations.*;

public interface EmployerMapper extends BaseMapper<Employer> {
    
    /**
     * 根据企业账号查询企业信息
     */
    @Select("SELECT employer_account, company_name, contact_phone, contact_email, company_profile " +
            "FROM employer WHERE employer_account = #{employerAccount}")
    Employer selectEmployer(@Param("employerAccount") String employerAccount);

    /**
     * Upsert 操作：存在则更新，不存在则插入
     */
    @Insert("INSERT INTO employer(employer_account, company_name, contact_phone, contact_email, company_profile, update_time) " +
            "VALUES (#{employerAccount}, #{companyName}, #{contactPhone}, #{contactEmail}, #{companyProfile}, NOW()) " +
            "ON DUPLICATE KEY UPDATE " +
            "company_name=VALUES(company_name), " +
            "contact_phone=VALUES(contact_phone), " +
            "contact_email=VALUES(contact_email), " +
            "company_profile=VALUES(company_profile), " +
            "update_time=NOW()")
    int upsertEmployer(Employer employer);

    /**
     * 根据账号查询手机号
     */
    @Select("SELECT contact_phone FROM employer WHERE employer_account = #{employerAccount}")
    String selectContactPhoneByAccount(@Param("employerAccount") String employerAccount);
}
