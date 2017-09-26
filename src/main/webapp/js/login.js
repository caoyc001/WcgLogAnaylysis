/**
 * Created by Administrator on 2017/9/1.
 */

app.controller('loginCtrl', function($rootScope,$scope,$http,$location) {
    $rootScope.userName="";
    $scope.log=function () {
    var password=hex_md5($scope.inputPassword);


        $http({
            method: 'post',
            url:$location.absUrl()+'/login',
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            params: {username:$scope.inputUname,password:password}

        }).then(function successCallback(response) {

            if(response.data["message"]==="登录成功") {
                $rootScope.userName = $scope.inputUname;
                alert("登录成功");
                window.location = $location.absUrl()+'index.html';
            }
            else alert("登录失败");

            // 请求成功执行代码
        }, function errorCallback(response) {
            // 请求失败执行代码

            alert("fail");
        });
}
})