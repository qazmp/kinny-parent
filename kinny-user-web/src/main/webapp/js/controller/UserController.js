app.controller('UserController', function ($scope, UserService) {

    $scope.entity = {};

    $scope.againPassword = '';

    $scope.smsCode = '';
    
    $scope.registry = function () {

        if($scope.entity.password == null || $scope.entity.password == ''
            || $scope.againPassword == null || $scope.againPassword == '') {
            alert('密码不能为空');
            return ;
        }

        if($scope.againPassword != $scope.entity.password) {
            alert('输入的密码不一致');
            return ;
        }
        UserService.registry($scope.entity, $scope.smsCode).success(function (response) {

                alert(response.message);

        });

    }

    $scope.sendSms = function () {
        UserService.sendSms($scope.entity.phone).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }
        });
    }


});