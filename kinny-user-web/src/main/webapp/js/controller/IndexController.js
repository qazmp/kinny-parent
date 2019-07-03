app.controller('IndexController', function ($scope, LoginService) {

    $scope.principal = {};

    $scope.initPrincipal = function () {
        LoginService.principal().success(function (response) {
            $scope.principal = response;
        });
    }


});