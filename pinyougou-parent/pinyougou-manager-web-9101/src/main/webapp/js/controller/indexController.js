app.controller("indexController",function ($scope,loginService) {
    $scope.showLoginName=function () {
        loginService.showLoginName().success(function (rst) {
            $scope.loginName= rst.loginName;
            console.debug("loginName:"+rst.loginName);
        })
    }
})