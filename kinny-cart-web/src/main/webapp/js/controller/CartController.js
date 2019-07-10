app.controller('CartController', function ($scope, CartService, AddressService, OrderService) {
    // 购物车列表
    $scope.cartList = [];
    // 总值
    $scope.totalVal = {};
    // 收货地址列表
    $scope.addressList = [];
    // 已经选择的收货地址
    $scope.selectedAddress = {};
    // 订单
    $scope.order = {paymentType: 1};

    //*********************************************************************
    /**
     *  初始化购物车列表
     */
    $scope.initCartList = function () {

        CartService.showCarts().success(function (response) {
            if(response.success) {
                $scope.cartList = response.data;
                $scope.totalVal = CartService.sum($scope.cartList);
            }
        });

    }

    /**
     *  添加商品到购物车
     * @param itemId
     * @param num
     */
    $scope.addGoodsToCartList = function (itemId, num) {
        CartService.addGoodsToCartList(itemId, num).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.initCartList();
            }
        });
    }
    //************************************************************
    /**
     * 初始化用户收货地址列表
     */
    $scope.initAddressList = function () {
        AddressService.getPrincipalAddressList().success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.addressList = response.data;
                for(var i = 0; i < $scope.addressList.length; i ++) {
                    if($scope.addressList[i].isDefault == 1) {
                        $scope.selectedAddress = $scope.addressList[i];
                        break ;
                    }
                }
            }
        });
    }

    /**
     *  用户选择收货
     * @param address
     */
    $scope.selectAddress = function (address) {
        $scope.selectedAddress = address;
    }

    $scope.isSelected = function (address) {
        return $scope.selectedAddress == address;
    }

    $scope.selectPaymentType = function (paymentType) {
        $scope.order.paymentType = paymentType;
    }

    $scope.isSelectedPatmentType = function (paymentType) {
        return $scope.order.paymentType == paymentType;
    }

    
    $scope.commitOrder = function () {
        $scope.order.receiver = $scope.selectedAddress.contact;
        $scope.order.receiverAreaName = $scope.selectedAddress.address;
        $scope.order.receiverMobile = $scope.selectedAddress.mobile;
        OrderService.createOrder($scope.order).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                if($scope.order.paymentType == 1) {
                    location.href = 'pay.html';
                }else {
                    location.href = 'paysuccess.html';
                }
            }

        });
    }

    

});