app.controller('ContentController', function ($scope, ContentService, ContentCategoryService, UploadService, $controller) {



    $controller('BaseController', {$scope: $scope});



    $scope.dataSource_select_contentCategory = [];

    $scope.initPage = function() {
        ContentCategoryService.findAll().success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.dataSource_select_contentCategory = response.data;
            }
        });
    }


    $scope.queryAsPage = function () {

        ContentService.findPage($scope.paginationConf).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                // 计算总页数 构建分页导航条
                $scope.paginationConf.totalItems = response.data.totalSize;
                // 指定页数的记录
                $scope.list = response.data.list;
            }
        });

    }

    $scope.refresh = function() {
        $scope.paginationConf.currentPage = 1;
        $scope.queryAsPage();
    }

    $scope.findOne = function(id) {

        ContentService.findOne(id).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.entity = response.data;
            }
        });

    }

    $scope.upload = function () {

        var formData = new FormData();

        formData.append('multipartFile', file.files[0]);

        UploadService.upload(formData).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.entity.pic = response.message;
            }
        });


    }
    
    $scope.save = function () {
        ContentService.save($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.refresh();
            }

        });
    }
    
    $scope.delete = function () {
        var s = $scope.arrayToSplitString($scope.ids);
        ContentService.delete(s).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.refresh();
            }
        });
    }








});