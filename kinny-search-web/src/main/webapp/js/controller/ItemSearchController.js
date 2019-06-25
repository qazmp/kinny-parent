app.controller('ItemSearchController', function ($scope, $location, ItemSearchService) {

    $scope.searchMap = {keywords: '', category: '', brand: '', spec: {}, price: '', pageIndex: 1, pageSize: 20, sort: '', sortField: ''};

    $scope.resultMap = {totalCount: '', rows: [], pageLabel: []};



    $scope.search = function () {
        ItemSearchService.search($scope.searchMap).success(function (response) {
            if(!response.success) {
                alert(response.message);
            }else {
                $scope.resultMap = response.data;
                buildNavBar();


            }
        });
    }

    $scope.initKeyWords = function() {

        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();


    }

    // 排序
    $scope.solrQuery = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.pageIndex = 1;
        $scope.search();
    }

    // 构建分页导航条
   var buildNavBar =  function () {

        // 以当前页为中心 前后各两页
        var start = 0;
        var end = 0;
        $scope.isBeforePoint = true;
        $scope.isAfterPoint = true;
        if ($scope.resultMap.totalCount < 5) {
            start = 1;
            end = $scope.resultMap.totalCount;
            $scope.isBeforePoint = false;
            $scope.isAfterPoint = false;
        } else {



            start = $scope.searchMap.pageIndex - 2;
            end = $scope.searchMap.pageIndex + 2;

            if (start < 1) {
                start = 1;
                end = 5;
                $scope.isBeforePoint = false;
            }
            if (end > $scope.resultMap.totalCount) {
                end = $scope.resultMap.totalCount;
                start = end - 4;
                $scope.isAfterPoint = false;
            }
        }
        $scope.resultMap.pageLabel = [];
        for (var i = start; i <= end; i++) {
            $scope.resultMap.pageLabel.push(i);
        }
    }

    // 页码
    $scope.pageQuery = function(pageIndex) {
        pageIndex = parseInt(pageIndex);
         // 对当前页进行临界值约束
        if(pageIndex < 1 || pageIndex > $scope.resultMap.totalCount) {

            if(pageIndex < 1) {
                $scope.searchMap.pageIndex = 1;
            }
            if(pageIndex > $scope.resultMap.totalCount) {
                $scope.searchMap.pageIndex = $scope.resultMap.totalCount;
            }

        }

        //$scope.searchMap.pageIndex = pageIndex;
        $scope.search();

    }

    $scope.isFirstPage = function() {
        if($scope.searchMap.pageIndex == 1) {
            return true;
        }
        return false;
    }

    $scope.isLastPage = function() {
        if($scope.searchMap.pageIndex == $scope.resultMap.totalCount) {
            return true;
        }
        return false;
    }

    // 添加（构建）过滤条件
    $scope.addSearchItem = function (key, value) {

        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        $scope.search();


    }

    // 删除（撤销）过滤条件
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    // 品牌是否是关键字 子串
    $scope.keywordsIsBrand = function () {
        var brandList = $scope.resultMap.brands;
        for (var i = 0; i < brandList.length; i ++) {
            if ($scope.searchMap.keywords.indexOf(brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }


});