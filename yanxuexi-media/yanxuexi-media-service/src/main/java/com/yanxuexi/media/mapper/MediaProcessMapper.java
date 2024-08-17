package com.yanxuexi.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxuexi.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
    /**
     * @description: 根据分片参数获取待处理任务
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count 任务总数
     * @return 待处理任务
     */
    @Select("select * from media_process where id % #{shardTotal} = #{shardIndex} and (status = 1 or status = 3) and fail_count < 3 limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);

    /**
     * @description: 开启一个任务（更新数据库记录，谁更新成功，谁就抢到了锁，也就可以开启任务了）
     * @param id 任务 Id
     * @return 更新记录数（ <= 0 开启任务失败，> 0 开启任务成功）
     */
    @Update("update media_process set status = 4 where id = #{id} and (status = 1 or status = 3) and fail_count < 3;")
    int startTask(@Param("id") long id);
}
