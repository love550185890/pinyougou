package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author 550185890@qq.com
 * @Date 2019年9月24日16:40:15
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    TbBrandMapper brandMapper;

    @Transactional
    public Result deleteIds(Long[] ids) {
        for (Long id:ids) {
            try {
                brandMapper.deleteByPrimaryKey(id);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false,"删除失败");
            }
        }
        return new Result(true,"删除成功");
    }



    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    public PageResult findByPage(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> tbBrandsList = (Page<TbBrand>) brandMapper.selectByExample(null);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(tbBrandsList.getTotal());
        pageResult.setRows(tbBrandsList);
        return pageResult;
    }
    @Transactional
    public Result save(TbBrand tbBrand) {
        try {
            brandMapper.insert(tbBrand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }


    public TbBrand findById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public Result update(TbBrand tbBrand) {
        try {
            brandMapper.updateByPrimaryKey(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    public List<Map> findList() {
        return brandMapper.findList();
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(brand!=null){
            if(brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }
}
