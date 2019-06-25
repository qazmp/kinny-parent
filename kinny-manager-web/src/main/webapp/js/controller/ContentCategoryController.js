app.controller('ContentCategoryController', function ($scope, ContentCategoryService, $controller) {


    $controller('BaseController', {$scope: $scope});

    $scope.searchEntity = {};


    $scope.queryAsPage = function () {

        ContentCategoryService.findPage($scope.paginationConf, $scope.searchEntity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.paginationConf.totalItems = response.data.totalSize;
                $scope.list = response.data.list;
            }
        });
        
    }

    /**
     *  高级query 也可以新增修改删除完毕后reload页面
     */
    $scope.search = function () {
        $scope.paginationConf.totalItems = 1;
        $scope.queryAsPage();
    }

    $scope.findOne = function(id) {

        ContentCategoryService.findOne(id).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.entity = response.data;
            }
        });

    }
    
    $scope.save = function () {
        ContentCategoryService.save($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.search();
            }
        });
    }

    $scope.delete = function () {

        ContentCategoryService.delete($scope.arrayToSplitString($scope.ids)).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.search();
            }
        });

    }







});