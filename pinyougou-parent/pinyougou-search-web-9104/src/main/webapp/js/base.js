var app = angular.module("pinyougou", []);
//angularjs添加信任html
app.filter("trustAsHtml",["$sce",function ($sce) {
    return function (data) {
      return $sce.trustAsHtml(data);
    };
}]);