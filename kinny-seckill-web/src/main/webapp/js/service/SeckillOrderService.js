app.service('SeckillOrderService', function ($http) {

    this.submitOrder = function (id) {
        return $http.get('seckillOrder/submitOrder.do?id=' + id);
    }

});