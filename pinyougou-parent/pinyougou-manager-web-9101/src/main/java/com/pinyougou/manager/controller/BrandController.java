package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author 550185890@qq.com
 * @Date 2019年9月24日16:48:32
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    BrandService brandService;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 分页查询
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return
     */
    @RequestMapping("/findByPage/{pageNum}/{pageSize}")
    public PageResult findByPage(@PathVariable("pageNum")int pageNum,@PathVariable("pageSize")int pageSize){
        return brandService.findByPage(pageNum,pageSize);
    }

    /**
     * 保存
     * @param tbBrand
     * @return
     */
    @RequestMapping("/save")
    public Result save(@RequestBody TbBrand tbBrand){
        return  brandService.save(tbBrand);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @RequestMapping("/findById/{id}")
    public TbBrand findById(@PathVariable("id") Long id){
        return brandService.findById(id);
    }

    /**
     * 根据Id修改
     * @param tbBrand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        return brandService.update(tbBrand);
    }

    @RequestMapping("/deleteIds")
    public Result deleteByIds(@RequestBody Long [] ids){
        return brandService.deleteIds(ids);
    }

    @RequestMapping("/findList")
    public List<Map> findList(){
        return brandService.findList();
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int rows  ){
        return brandService.findPage(brand, page, rows);
    }
}
