app.controller('SellerController', function ($scope, SellerService) {

    $scope.entity = {}; // 封装新增或者修改数据的js对象

    $scope.checked = false;

    $scope.principal = {};

    $scope.add = function () {

        if (!$scope.checked) {
            alert('请勾选同一协议');
            return ;
        }

        SellerService.add($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                window.location.href = 'shoplogin.html';
            }
        });

    }
    
    $scope.protocolChecked = function ($event) {
        if ($event.target.checked) {
            $scope.checked = true;
        }else {
            $scope.checked = false;
        }
    }

    $scope.login = function () {
        SellerService.login($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                window.location.href = 'admin/index.html';
            }
        });

    }



});