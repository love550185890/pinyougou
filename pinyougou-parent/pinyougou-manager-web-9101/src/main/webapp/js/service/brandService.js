app.service("brandService",function ($http) {
    this.findPage=function (pageNum,pageSize) {
        return $http.get('../brand/findByPage/'+pageNum+'/'+pageSize+'.do');
    }
    this.save=function (entity) {
        return $http.post("../brand/save.do",entity);
    }
    this.update=function(entity){
        return $http.post("../brand/update.do",entity);
    }
    this.delete=function (selectedIds) {
        return $http.post("../brand/deleteIds.do",selectedIds);
    }
    this.findById=function (id) {
        return $http.get("../brand/findById/"+id+".do");
    }
    this.findList=function(){
        return $http.get("../brand/findList.do");
    }
    this.findPage=function (page,rows,searchEntity) {
        return $http.post("../brand/search.do?page="+page+"&rows="+rows,searchEntity);
    }
});