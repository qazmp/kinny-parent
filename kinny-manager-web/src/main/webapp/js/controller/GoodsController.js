app.controller('GoodsController', function (GoodsService, ItemCatService, $scope, $controller) {

    $controller('BaseController', {$scope: $scope});


    // 封装了分页时查询条件的js对象
    $scope.searchEntity = {};

    // 所有的商品分类脚本数据
    $scope.script_data_itemCats = [];

    $scope.script_data_status = ['未审核', '已审核', '审核未通过', '关闭'];


    $scope.initItemCatsScriptData = function() {

        ItemCatService.findAll().success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                var itemCats = response.data;
                for (var i = 0; i < itemCats.length; i ++) {
                    $scope.script_data_itemCats[itemCats[i].id] = itemCats[i].name;
                }
            }
        });

    }

    $scope.queryAsPage = function () {

        GoodsService.findPage($scope.searchEntity, $scope.paginationConf).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                // 修改分页信息变量数据总条数
                $scope.paginationConf.totalItems = response.data.totalSize;
                $scope.list = response.data.list;
            }
        });
    };

    $scope.search = function () {

        $scope.paginationConf.currentPage = 1;

        $scope.queryAsPage();

    }
    
    $scope.updateStatus = function (status) {

        var s = $scope.arrayToSplitString($scope.ids);

        $scope.entity.id = s;

        $scope.entity.auditStatus = status;

        GoodsService.updateStatus($scope.entity).success(function (response) {

            if (!response.success) {
                alert(response.message);
            }else {
                $scope.search();
            }

        });

        
    }

    $scope.delete = function () {
        var s = $scope.arrayToSplitString($scope.ids);

        GoodsService.delete(s).success(function (response) {

            if (!response.success) {
                alert(response.message);
            } else {
                $scope.search();
            }
        });
    }


});