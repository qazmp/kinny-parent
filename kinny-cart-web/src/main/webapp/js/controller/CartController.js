app.controller('CartController', function ($scope, CartService) {

    $scope.cartList = [];

    $scope.totalVal = {};

    $scope.initCartList = function () {

        CartService.showCarts().success(function (response) {
            if(response.success) {
                $scope.cartList = response.data;
                $scope.totalVal = CartService.sum($scope.cartList);
            }
        });

    }

    $scope.addGoodsToCartList = function (itemId, num) {
        CartService.addGoodsToCartList(itemId, num).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.initCartList();
            }
        });
    }



});