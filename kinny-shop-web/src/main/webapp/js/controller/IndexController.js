app.controller('IndexController', function ($scope, SellerService) {


    $scope.principal = {};

    $scope.initPrincipal = function () {
        SellerService.getPrincipal().success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.principal = response.data;
            }
        });
    }

});