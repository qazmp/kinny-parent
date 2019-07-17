app.controller('SeckillGppdsController', function ($scope, SeckillGoodsService, $location) {

    // 秒杀的商品列表
    $scope.list = [];

    $scope.initSeckillGoodsList = function () {

        SeckillGoodsService.findAll().success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.list = response.data;
            }
        });

    }

    $scope.goods = {};

    $scope.initSeckillGoodsDetails = function () {
        var id = $location.search()['id'];
        SeckillGoodsService.findOne(id).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.goods = response.data;
            }
        });
    }

});