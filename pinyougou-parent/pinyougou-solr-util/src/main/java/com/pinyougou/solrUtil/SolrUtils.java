package com.pinyougou.solrUtil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author
 * @Date
 */
@Component
public class SolrUtils {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    SolrTemplate solrTemplate;

    public  void importData(){
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("------------------查询的商品列表---开始----------------");
        for (TbItem item : itemList) {
            System.out.println("title= " + item.getTitle()+";price = " + item.getPrice());
            //设置动态域
            Map map = JSON.parseObject(item.getSpec());
            item.setSpecMap(map);
        }
        System.out.println("------------------查询的商品列表---结束----------------");
        //把从mysql中查出来的sku数据导入到索引库
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtils solrUtils = (SolrUtils) context.getBean("solrUtils");
        solrUtils.importData();
    }
}
