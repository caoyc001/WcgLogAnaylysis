/**
 * Created by Administrator on 2017/8/1.
 */
app.filter('getKey',['$sce',function($sce){
    return function(content) {
        var match= Array.prototype.slice.call(arguments)[1];

        var reg = new　RegExp(match,'g');
        var reg1 = new　RegExp('\n','g');
        showcontent= content.replace(reg,"<span class='highlight'>"+match+'</span>');
        showcontent=showcontent.replace(reg1,"<br/>")

        return $sce.trustAsHtml(showcontent);
    }
}])
app.filter('toHtml',['$sce',function($sce){
    return function(content) {


        return $sce.trustAsHtml(content);
    }
}])

app.config(['$locationProvider', function($locationProvider) {
    $locationProvider.html5Mode(true);
}]);

app.controller('searchCtrl', function($rootScope,$scope,$http,$location,$interval) {
    $scope.firstName = "John";
    $scope.lastName = "Doe";
    $scope.keyholder="";
    $scope.inputDate=""
    $scope.inputKey="" ;
    $scope.inputSeq="";
    $scope.logs=[];
    $scope.thislog={};
    $scope.flag=true;
    var vm=$scope.vm={};
    vm.style='blue';
    vm.progress=0;
    vm.text=true;
    $scope.searching=true;

    //搜索方法.使用ajax向服务端发起请求,并处理返回的Json
    $scope.search= function () {
        if($scope.inputDate==""&&$scope.inputKey==""&&$scope.inputSeq=="")
        {alert("请输入至少一个关键字");
         return ;

        }
        if($scope.searching=true) {
            $scope.searching = false;
            $scope.flag = true;
            $scope.logs = [];
            $scope.thislog.fileName = "";
            $scope.logcontent = "";
            vm.progress = 0;
            $http({
                method: 'GET',
                url: $location.absUrl().substring(0, $location.absUrl().length - 11) + '/search',
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                params: {date: $scope.inputDate, key: $scope.inputKey, seq: $scope.inputSeq}
            }).then(function successCallback(response) {

                $scope.logs = [];
                vm.progress = 100;
                $interval.cancel(timer);
                for (items in response.data)

                    $scope.logs.push({
                        "date": response.data[items],
                        "seq": items


                    });
                if (response.data.length === 0) {
                    alert("请登录");
                    window.location = $location.absUrl().substring(0, $location.absUrl().length - 11);
                }
                $scope.searching = true;

                // 请求成功执行代码
            }, function errorCallback(response) {
                // 请求失败执行代码
                $interval.cancel(timer);
                alert("fail");
                $scope.searching = true;
            });
            var timer = $interval(function () {
                $scope.getProcess();
            }, 400);
        }

    }
    $scope.getProcess=function () {
        $http({
            method: 'GET',
            url: $location.absUrl().substring(0,$location.absUrl().length-11)+'/per',
            contentType: "application/x-www-form-urlencoded; charset=utf-8",

        }).then(function successCallback(response) {
            vm.progress=response.data["per"];

        }, function errorCallback(response) {

            alert("sucess");
        });

    }


    function myInterval()
    {   if (vm.progress<99)
        vm.progress=vm.progress+1;
    }
    $scope.searchSeq= function (seq) {
        if ($scope.searching = true) {

            $scope.searching = false
            $http({
                method: 'GET',
                url: $location.absUrl().substring(0, $location.absUrl().length - 11) + '/log',
                params: {seq: seq}
            }).then(function successCallback(response) {
                $scope.thislog = {};
                for (items in response.data) {
                    $scope.logcontent = response.data[items];

                    $scope.thislog.fileName = items;
                }

                $scope.searching = true;
                // 请求成功执行代码
            }, function errorCallback(response) {
                // 请求失败执行代码
                alert("fail");
                $scope.searching = true;
            });
        }
        vm.myFunc = function () {
            if (vm.progress > 100) {
                vm.progress = 100;
            }
            if (vm.progress < 0) {
                vm.progress = 0;
            }
        }
    }

});