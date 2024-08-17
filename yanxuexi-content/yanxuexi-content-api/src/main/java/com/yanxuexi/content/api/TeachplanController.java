package com.yanxuexi.content.api;

import com.yanxuexi.content.model.dto.BindTeachplanMediaDto;
import com.yanxuexi.content.model.dto.MoveStatusDto;
import com.yanxuexi.content.model.dto.SaveTeachplanDto;
import com.yanxuexi.content.model.dto.TeachplanDto;
import com.yanxuexi.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hyx
 * @version 1.0
 * @description 课程计划编辑接口
 * @date 2024-07-20 10:51
 **/
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("课程计划树型结构查询接口")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.findTeachplanTree(courseId);
    }

    @ApiOperation("课程计划章节新增或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        teachplanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("删除课程计划")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable @Validated @NotNull(message = "课程计划 Id 不能为空") Long teachplanId) {
        teachplanService.deleteTeachplan(teachplanId);
    }

    @ApiOperation("课程章节下移")
    @PostMapping("/teachplan/movedown/{teachplanId}")
    public void teachplanMoveDown(@PathVariable @Validated @NotNull(message = "课程计划 Id 不能为空") Long teachplanId) {
        teachplanService.moveTeachPlan(teachplanId, MoveStatusDto.MOVE_DOWN);
    }

    @ApiOperation("课程章节上移")
    @PostMapping("/teachplan/moveup/{teachplanId}")
    public void teachplanMoveUp(@PathVariable @Validated @NotNull(message = "课程计划 Id 不能为空") Long teachplanId) {
        teachplanService.moveTeachPlan(teachplanId, MoveStatusDto.MOVE_UP);
    }

    @ApiOperation("课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    @ApiOperation("课程计划解除媒资信息绑定")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void deleteAssociatedMedia(
            @PathVariable @Validated @NotNull(message = "课程计划Id不能为空") Long teachPlanId,
            @PathVariable @Validated @NotNull(message = "媒资Id不能为空") String mediaId) {
            teachplanService.deleteAssociatedMedia(teachPlanId, mediaId);
    }

}
