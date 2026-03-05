-- 插入测试数据 (用户位置：40.07637, 113.30001)

-- 岗位 1: 非常近，约 100 米
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company001', 8000, 15000, 'Java 开发工程师 - 就近上班', '太原市尖草坪区', 40.07727, 113.30091, '1 年以上', '本科', 1, NULL);

-- 岗位 2: 约 500 米
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company002', 6000, 12000, '前端开发工程师', '太原市迎泽区', 40.08087, 113.30451, '应届生', '大专', 1, NULL);

-- 岗位 3: 约 1 公里
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company003', 10000, 20000, '数据分析师', '太原市小店区', 40.08537, 113.31001, '2 年以上', '本科', 1, NULL);

-- 岗位 4: 约 2 公里
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company004', 12000, 25000, '产品经理', '太原市杏花岭区', 40.09437, 113.32001, '3 年以上', '本科', 1, NULL);

-- 岗位 5: 约 3 公里
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company005', 7000, 14000, 'UI 设计师', '太原市万柏林区', 40.10337, 113.28001, '1 年以上', '大专', 1, NULL);

-- 岗位 6: 约 5 公里 (边界值)
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company006', 6000, 13000, '测试工程师', '太原市晋源区', 40.12137, 113.35001, '1 年以上', '本科', 1, NULL);

-- 岗位 7: 约 8 公里 (超出 5km 范围)
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company007', 8000, 16000, '运维工程师', '太原市清徐县', 40.14837, 113.38001, '2 年以上', '大专', 1, NULL);

-- 岗位 8: 没有经纬度的老数据 (应该被过滤)
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, experience_requirement, education_requirement, status, university_name) 
VALUES ('company008', 4000, 8000, '行政专员', '太原市', '不限', '大专', 1, NULL);

-- 岗位 9: 非常远，约 15 公里 (超出范围)
INSERT INTO job_info (employer_account, salary_min, salary_max, job_description, work_location, latitude, longitude, experience_requirement, education_requirement, status, university_name) 
VALUES ('company009', 15000, 30000, '销售总监', '太原市阳曲县', 40.21137, 113.45001, '5 年以上', '本科', 1, NULL);
