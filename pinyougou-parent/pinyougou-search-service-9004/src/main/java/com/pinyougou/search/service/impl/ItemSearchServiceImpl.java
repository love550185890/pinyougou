package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author
 * @Date
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap<>();
//        Query query = new SimpleQuery("*:*");
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows",tbItems.getContent());
        map.putAll(searchWithHilight(searchMap));
        return map;
    }

    /**
     * 高亮查询
     * @param searchMap
     * @return
     */
    public Map<String ,Object> searchWithHilight(Map searchMap){
        Map<String, Object> map = new HashMap<>();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        HighlightQuery query = new SimpleHighlightQuery(criteria);
        HighlightOptions highLightOptions = new HighlightOptions();
        highLightOptions.addField("item_title");
        highLightOptions.setSimplePrefix("<em style='color:red'>");
        highLightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highLightOptions);
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlightEntryList = itemHighlightPage.getHighlighted();//高亮接口
        for (HighlightEntry<TbItem> entry : highlightEntryList) {
            TbItem item = entry.getEntity();//获取原实体
            //entry.getHighlights().get(0);//获取第一个域
            //entry.getHighlights().get(0).getSnipplets().get(0)//第一域的第一个字段
            if(entry.getHighlights().size()>0 && entry.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));//设置高亮结果
            }
        }
        map.put("rows", itemHighlightPage.getContent());
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        return map;
    }

    /**
     * 类目查询
     * @return
     */
    public List searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<TbItem> itemGroupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = itemGroupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> groupEntryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : groupEntryList) {
            list.add(entry.getGroupValue());
        }
        return list;
    }
}
