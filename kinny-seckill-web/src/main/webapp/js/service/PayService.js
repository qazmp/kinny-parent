app.service('PayService', function ($http) {

    this.createNative = function () {
        return $http.get('pay/createNative.do');
    }
    
    this.pollIsPayment = function (outTradeNo) {
        return $http.get('pay/pollOutTradeIsPayment.do?outTradeNo=' + outTradeNo);
    }

});