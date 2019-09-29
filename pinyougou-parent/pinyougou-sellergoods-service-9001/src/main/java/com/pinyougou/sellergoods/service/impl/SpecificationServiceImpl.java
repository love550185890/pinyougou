package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;

    /**
     * 查询全部
     */
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    TbSpecificationOptionMapper tbSpecificationOptionMapper;

    /**
     * 增加
     */
    @Transactional
    public void add(Specification specification) {
        TbSpecification tbSpecification = specification.getTbSpecification();
        specificationMapper.insert(tbSpecification);
        for (TbSpecificationOption option : specification.getTbSpecificationOptionList()) {
            option.setSpecId(tbSpecification.getId());
            tbSpecificationOptionMapper.insert(option);
        }
    }


    /**
     * 修改
     */
    @Transactional
    public void update(Specification specification) {
        List<TbSpecificationOption> updateList = new ArrayList<>();
        specificationMapper.updateByPrimaryKey(specification.getTbSpecification());
        //测试事务
        // int i = 1/0;
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(specification.getTbSpecification().getId());
        List<TbSpecificationOption> oldOptionList = tbSpecificationOptionMapper.selectByExample(example);
        List<TbSpecificationOption> newOptionList = specification.getTbSpecificationOptionList();
        for (TbSpecificationOption newSpecificationOption : newOptionList) {
            if (newSpecificationOption.getId()==null){
                //那就是新增的
                newSpecificationOption.setSpecId(specification.getTbSpecification().getId());
                tbSpecificationOptionMapper.insert(newSpecificationOption);
            }else{
                //变更的
                for (TbSpecificationOption oldSpecificationOption : oldOptionList) {
                    if(newSpecificationOption.getId().equals(oldSpecificationOption.getId())){
                        updateList.add(oldSpecificationOption);
                        tbSpecificationOptionMapper.updateByPrimaryKey(newSpecificationOption);
                    }
                }
            }
        }
        //删除的=老的-更新的
        oldOptionList.removeAll(updateList);
        for (TbSpecificationOption delSpecificationOption : oldOptionList) {
            tbSpecificationOptionMapper.deleteByPrimaryKey(delSpecificationOption.getId());
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        example.createCriteria().andSpecIdEqualTo(tbSpecification.getId());
        List<TbSpecificationOption> tbSpecificationOptionList = tbSpecificationOptionMapper.selectByExample(example);
        Specification specification = new Specification();
        specification.setTbSpecification(tbSpecification);
        specification.setTbSpecificationOptionList(tbSpecificationOptionList);
        return specification;
    }

    /**
     * 批量删除
     */
    @Transactional
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(id);
            tbSpecificationOptionMapper.deleteByExample(example);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    public List<Map> findList() {
        return specificationMapper.findList();
    }
}
