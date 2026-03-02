package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Employee;
import org.apache.ibatis.annotations.*;
public interface EmployeeMapper extends BaseMapper<Employee> {
    @Select("SELECT user_account, employee_name, phone_number, university_name, job_intention, resume FROM employee WHERE user_account = #{userAccount}")
    Employee selectEmployee(@Param("userAccount") String userAccount);

    @Insert("INSERT INTO employee(user_account, employee_name, phone_number, university_name, job_intention, resume, update_time) VALUES (#{userAccount}, #{employeeName}, #{phoneNumber}, #{universityName}, #{jobIntention}, #{resume}, NOW()) ON DUPLICATE KEY UPDATE employee_name=VALUES(employee_name), phone_number=VALUES(phone_number), university_name=VALUES(university_name), job_intention=VALUES(job_intention), resume=VALUES(resume), update_time=NOW()")
    int upsertEmployee(Employee employee);

    @Select("SELECT COUNT(*) FROM employee WHERE phone_number = #{phoneNumber} AND user_account != #{excludeUserAccount}")
    int countByPhoneNumberExceptSelf(@Param("phoneNumber") String phoneNumber, @Param("excludeUserAccount") String excludeUserAccount);
}