package com.yanxuexi.learning.service;

import com.yanxuexi.learning.model.dto.XcChooseCourseDto;
import com.yanxuexi.learning.model.dto.XcCourseTablesDto;

/**
 * @author hyx
 * @version 1.0
 * @description 我的课程表service接口
 * @date 2024-09-03 15:27
 **/
public interface MyCourseTablesService {
    /**
     * @description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     */
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @description 判断学习资格
     * @param userId 用户Id
     * @param courseId 课程Id
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     */
    XcCourseTablesDto getLearningStatus(String userId, Long courseId);

    /**
     * @description: 支付成功，修改选课表状态，并将课程添加到我的课程表
     * @param chooseCourseId 选课记录Id
     * @return 是否修改成功
     */
    boolean saveChooseCourseSuccess(String chooseCourseId);
}
