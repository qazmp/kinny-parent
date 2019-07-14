app.controller('PayController', function ($scope, PayService, $location) {

    $scope.pay = {};


    $scope.initNative = function () {
        PayService.createNative().success(function (response) {
            $scope.pay = response;
            $scope.pay.total_fee = (response.total_fee / 100).toFixed(2);
            var qrcode = new QRious({
                element: document.getElementById('qrious'),
                size: 250,
                value: $scope.pay.code_url
            });
            isPayment();
        });
    }

    var isPayment = function () {
        PayService.pollIsPayment($scope.pay.out_trade_no).success(function (response) {
            if(response.success && response.message == "用户已支付") {
                location.href = 'paysuccess.html#?total_fee=' + $scope.pay.total_fee;
            }else {
                $scope.initNative();
            }
        });

    }

    $scope.total_fee = 0;

    $scope.initTotalFee = function () {
        $scope.total_fee = $location.search()['total_fee'];
    }


});