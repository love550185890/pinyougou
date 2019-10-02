package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
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
        //搜索中的空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Map<String, Object> map = new HashMap<>();
//        Query query = new SimpleQuery("*:*");
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows",tbItems.getContent());
        map.putAll(searchWithHilight(searchMap));
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        String categoryName=(String)searchMap.get("category");
        if(!"".equals(categoryName)){//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{//如果没有分类名称，按照第一个查询
            if(categoryList.size()>0){
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }
        return map;
    }

    /**
     * 高亮查询
     * @param searchMap
     * @return
     */
    public Map<String ,Object> searchWithHilight(Map searchMap){
        Map<String, Object> map = new HashMap<>();
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highLightOptions = new HighlightOptions();
        highLightOptions.addField("item_title");
        highLightOptions.setSimplePrefix("<em style='color:red'>");
        highLightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highLightOptions);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2 按分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3 按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4 规格过滤
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map)searchMap.get("spec");
            for(String key :specMap.keySet()){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.5按价格筛选.....
        if(!"".equals(searchMap.get("price"))){
            String[] price = ((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){//如果区间起点不等于0
                Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果区间终点不等于*
                Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6 分页查询
        Integer pageNo= (Integer) searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;//默认第一页
        }
        Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数
        if(pageSize==null){
            pageSize=20;//默认20
        }
        query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
        query.setRows(pageSize);

        //1.7排序
        String sortValue= (String) searchMap.get("sort");//ASC  DESC
        String sortField= (String) searchMap.get("sortField");//排序字段
        if(sortValue!=null && !sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
                query.addSort(sort);
            }
        }



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
        map.put("totalPages", itemHighlightPage.getTotalPages());//返回总页数
        map.put("total", itemHighlightPage.getTotalElements());//返回总记录数
        System.out.println("返回总页数totalPages = " + itemHighlightPage.getTotalPages()+";返回总记录数total:"+itemHighlightPage.getTotalElements());
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
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        if(typeId!=null){
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }
}
