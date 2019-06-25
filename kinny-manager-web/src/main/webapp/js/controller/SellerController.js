app.controller('SellerController', function ($scope, SellerService, $controller) {


    $controller('BaseController', {$scope: $scope});

    $scope.status = '0';

    $scope.searchEntity = {status: '0', name: '', nickName: ''};

    $scope.queryAsPage = function () {

        SellerService.findPage($scope.paginationConf, $scope.searchEntity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.list = response.data.list;

                $scope.paginationConf.totalItems = response.data.totalNum;

            }
        });


    }

    $scope.search = function () {

        $scope.paginationConf.currentPage = 1;
        $scope.queryAsPage();
    }
    
    $scope.findOne = function (id) {
        SellerService.findOne(id).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.entity = response.data;
            }
        });
    }

    $scope.updateStatus = function (id, status) {
        SellerService.updateStatus(id, status).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.queryAsPage();
            }
        });
    }


});