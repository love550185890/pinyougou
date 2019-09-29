package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<TbBrand> findAll();
    public PageResult findByPage(int pageNum, int pageSize);
    public Result save(TbBrand tbBrand);
    public TbBrand findById(Long id);
    public Result update(TbBrand tbBrand);
    public Result deleteIds(Long [] ids);
    public List<Map> findList();
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
}
