package com.example.uav.controller;

import com.example.uav.common.AjaxResult;
import com.example.uav.domain.Uav;
import com.example.uav.service.IUavService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 无人机信息管理控制器
 */
@Controller
@RequestMapping("/uav")
public class UavController {

    @Autowired
    private IUavService uavService;

    /**
     * 无人机列表页面
     */
    @GetMapping("/list")
    public String list() {
        return "uav/list";
    }

    /**
     * 分页查询无人机列表
     */
    @GetMapping("/list/page")
    @ResponseBody
    public AjaxResult page(Uav uav,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Uav> list = uavService.selectUavList(uav);
        PageInfo<Uav> pageInfo = new PageInfo<>(list);
        return AjaxResult.success(pageInfo);
    }

    /**
     * 新增无人机页面
     */
    @GetMapping("/add")
    public String add() {
        return "uav/add";
    }

    /**
     * 新增无人机
     */
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@Validated Uav uav) {
        return toAjax(uavService.insertUav(uav));
    }

    /**
     * 编辑无人机页面
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Uav uav = uavService.selectUavById(id);
        model.addAttribute("uav", uav);
        return "uav/edit";
    }

    /**
     * 修改无人机
     */
    @PutMapping("/edit")
    @ResponseBody
    public AjaxResult edit(@Validated Uav uav) {
        return toAjax(uavService.updateUav(uav));
    }

    /**
     * 删除无人机
     */
    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(uavService.deleteUavById(id));
    }

    /**
     * 查询无人机详情
     */
    @GetMapping("/{id}")
    @ResponseBody
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(uavService.selectUavById(id));
    }

    /**
     * AI 自动生成无人机属性
     */
    @GetMapping("/aiGenerate")
    @ResponseBody
    public AjaxResult aiGenerate(String name, String type) {
        Uav uav = uavService.generateAiAttributes(name, type);
        return AjaxResult.success(uav);
    }

    /**
     * 新增时包含 AI 生成属性的接口
     */
    @PostMapping("/addWithAi")
    @ResponseBody
    public AjaxResult addWithAi(Uav uav) {
        Uav aiUav = uavService.generateAiAttributes(uav.getName(), uav.getType());
        aiUav.setSerialNumber(uav.getSerialNumber());
        aiUav.setStatus(uav.getStatus() != null ? uav.getStatus() : "active");
        return toAjax(uavService.insertUav(aiUav));
    }

    private AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
