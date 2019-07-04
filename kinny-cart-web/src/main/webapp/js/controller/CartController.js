app.controller('CartController', function ($scope, CartService) {

    $scope.cartList = [];

    $scope.initCartList = function () {

        CartService.showCarts().success(function (response) {
            if(response.success) {
                $scope.cartList = response.data;
            }
        });

    }


});