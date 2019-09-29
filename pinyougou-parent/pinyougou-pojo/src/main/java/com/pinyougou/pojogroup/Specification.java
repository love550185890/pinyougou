package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @Author 550185890@qq.com
 * @Date 2019年9月25日01:45:11
 */
public class Specification implements Serializable {

    private TbSpecification tbSpecification;
    private List<TbSpecificationOption> tbSpecificationOptionList;

    public Specification() {
    }

    public Specification(TbSpecification tbSpecification, List<TbSpecificationOption> tbSpecificationOptionList) {
        this.tbSpecification = tbSpecification;
        this.tbSpecificationOptionList = tbSpecificationOptionList;
    }

    public TbSpecification getTbSpecification() {
        return tbSpecification;
    }

    public void setTbSpecification(TbSpecification tbSpecification) {
        this.tbSpecification = tbSpecification;
    }

    public List<TbSpecificationOption> getTbSpecificationOptionList() {
        return tbSpecificationOptionList;
    }

    public void setTbSpecificationOptionList(List<TbSpecificationOption> tbSpecificationOptionList) {
        this.tbSpecificationOptionList = tbSpecificationOptionList;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "tbSpecification=" + tbSpecification +
                ", tbSpecificationOptionList=" + tbSpecificationOptionList +
                '}';
    }
}
