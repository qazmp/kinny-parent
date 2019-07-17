app.service('SeckillGoodsService', function ($http) {

    this.findAll = function () {
        return $http.get('seckillGoods/findGoodsList.do');
    }

    this.findOne = function (id) {
        return $http.get('seckillGoods/findOne.do?id=' + id);
    }

});