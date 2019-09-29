app.controller("brandController", function ($scope,$controller,brandService) {
    //集成baseController
    $controller("baseController",{$scope:$scope});
    //分页
    $scope.findPage = function (page,rows) {
        brandService.findPage(page,rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //回显
    $scope.findById=function(id){
        brandService.findById(id).success(function (rst) {
            $scope.entity = rst;
        });
    }
    //添加
    $scope.save=function (id) {
        //如果id是"undefined"那么就是新增，否则就是修改
        if(typeof (id)=="undefined"){
            brandService.save($scope.entity).success(function(rst){
                if(rst.success){
                    $scope.reloadList();
                }else{
                    alert(rst.message);
                }
            });
        }else{
            brandService.update($scope.entity).success(function (rst) {
                if(rst.success){
                    $scope.reloadList();
                }else{
                    alert(rst.message);
                }
            })
        }
    }

    //删除
    $scope.delete=function () {
        if($scope.selectedIds.length>0){
            brandService.delete($scope.selectedIds).success(function (rst) {
                if(rst.success){
                    $scope.reloadList();
                }else{
                    alert(rst.message);
                }
            });
        }
    }
    //条件查询
    $scope.searchEntity={};
    $scope.search=function (page,rows) {
        brandService.findPage(page,rows,$scope.searchEntity).success(function (rst) {
            $scope.list=rst.rows;
            $scope.paginationConf.totalItems=rst.total;//更新总记录数
        });
    }
});