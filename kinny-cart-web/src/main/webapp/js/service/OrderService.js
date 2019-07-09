app.service('OrderService', function ($http) {

    this.createOrder = function (order) {
        return $http.post('order/createOrder.do', order);
    }

});