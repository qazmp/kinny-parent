app.controller('SeckillGppdsController', function ($scope, SeckillGoodsService, SeckillOrderService, $location, $interval) {

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

    $scope.timeString = '';

    $scope.initSeckillGoodsDetails = function () {
        var id = $location.search()['id'];
        SeckillGoodsService.findOne(id).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.goods = response.data;
                // 秒杀剩余时间
                var totalSecount =
                    Math.floor((new Date($scope.goods.endTime).getTime() - new Date().getTime()) / 1000);
                var surplus = $interval(function () {
                    $scope.timeString = numberConverterDateString(totalSecount);
                    totalSecount -= 1;
                    if(totalSecount <= 0) {
                        $interval.cancel(surplus);
                    }

                }, 1000);
            }
        });
    }

    /**
     *  秒数 装换为 时间字符串
     * @param second
     */
    var numberConverterDateString = function (second) {
        var day = Math.floor(second / (60*60*24));
        var hour = Math.floor((second - (day*(60*60*24))) / (60*60));
        var minute =Math.floor((second - (day*(60*60*24)) - (hour*(60*60))) / 60);
        var second = Math.floor(second - (day*(60*60*24)) - (hour*(60*60)) - (minute*60));
        // 个两位 假设修正 程序中转流程
        var str = '';
        if(day > 0) {
            str += day + '天';
        }
        if(hour > 0) {
            if(hour < 10) {
                str += '0' + hour + ':';
            }else {
                str += hour + ':';
            }
        }
        if(minute > 0) {
            if(minute < 10) {
                str += '0' + minute + ':';
            }else {
                str += minute + ':';
            }
        }
        if(second < 10) {
            str += '0' + second;
        }else {
            str += second;
        }
        return str;
    }
    /**
     *  提交订单
     * @param id
     */
   $scope.commitOrder1 = function (id) {
        SeckillOrderService.submitOrder(id).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                alert(response.message);
                location.href = 'pay.html';
            }
        });
    }


});