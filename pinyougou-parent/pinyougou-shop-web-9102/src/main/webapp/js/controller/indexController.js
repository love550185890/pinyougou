app.controller("indexController",function ($scope,loginService) {
    $scope.showLoginName=function () {
        loginService.showLoginName().success(function (rst) {
            console.debug("loginName:"+rst.loginName);
            $scope.loginName=rst.loginName
        });
    }
})