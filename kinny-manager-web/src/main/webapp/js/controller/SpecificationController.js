app.controller('SpecificationController', function (SpecificationService, $scope, $controller) {

    $controller('BaseController', {$scope: $scope});

    $scope.specName = '';

    /*
    * 规格新增修改js对象
    * {specification: {id: ?, specName: ?}, specificationOptionList: [{id: ?, specId: ?, optionName: ?, orders: ?} ...]}
    *
    * */
    //TypeError: Cannot read property 'push' of undefined

    /**
     *  查询全部数据 适合数据量小的情况
     */
    $scope.findAll = function () {

        SpecificationService.findAll().success(function (response) {
            if (response.success) {
                // 为与html绑定的变量赋值 向用户展示数据
                $scope.list = response.data;
            }else {
                alert(response.message);
            }
        });

    };

    /**
     *  分页查询 数据量较大
     */
    $scope.queryAsPage = function () {

        SpecificationService.findPage($scope.paginationConf, $scope.specName).success(function (response) {
            if (response.success) {
                $scope.list = response.data.list;
                // 更细分页js对象
                $scope.paginationConf.totalItems = response.data.totalNum;
            }else {
                alert(response.message);
            }
        });

    };

    $scope.findOne = function(id) {

        SpecificationService.findOne(id).success(function (response) {
            if (response.success) {
                $scope.entity = response.data;
            }else {
                alert(response.message);
            }
        });

    }


    /**
     *  高级查询 回到第一页
     */
    $scope.search = function () {
        // 当前页数重置 1
        $scope.paginationConf.currentPage = 1;
        $scope.queryAsPage();
    };

    /**
     *  PreparedOpenModel 方法在打开模态框之前会将与模态框绑定的js对象从新定义为{}
     *  导致 dynamicTable_add_specificationOption 抛出TypeError 无法对未定义的
     *  元素使用push 在清空之后重新定义
     */
    $scope.clearAfterDefinition = function() {
        $scope.entity = {specificationOptionList: []};
    }

    /**
     *  增加添加规格选项的输入框 对与该表格绑定的集合添加元素
     */
    $scope.dynamicTable_add_specificationOption = function () {

        $scope.entity.specificationOptionList.push({id: '', specId: '', optionName: '', orders: ''});

    };

    /**
     *  移除规格选项 减少与之绑定的js数组属性的元素
     */
    $scope.dynamicTable_remove_specificationOption = function (specificationOption) {

        $scope.entity.specificationOptionList.splice($scope.entity.specificationOptionList.indexOf(specificationOption), 1);

    }
    
    $scope.save = function() {
        if ($scope.entity.specification.id != null && $scope.entity.specification.id != '') {
            $scope.update();
        }else {
            $scope.add();
        }
    }

    /**
     *  添加规格以及规格选项 成功后跳转至最后一页 根据当前页临界值约束
     */
    $scope.add = function () {

        SpecificationService.add($scope.entity).success(function (response) {
            if (response.success) {
                $scope.paginationConf.currentPage = Math.round(($scope.paginationConf.totalItems / $scope.paginationConf.itemsPerPage) + 2) ;
                console.log($scope.paginationConf.currentPage);
                $scope.queryAsPage();
            }else {
                alert(response.message);
            }
        });

    }

    /**
     *  修改规格以及规格选项
     */
    $scope.update = function () {

        SpecificationService.update($scope.entity).success(function (response) {
            if (response.success) {
                $scope.queryAsPage();
            }else {
                alert(response.message);
            }
        });

    }

    /**
     *  删除
     */
    $scope.delete = function () {

        SpecificationService.delete($scope.arrayToSplitString($scope.ids)).success(function (response) {

            if (!response.success) {
                alert(response.message);
            }else {
                $scope.queryAsPage();
            }

        });

    }

});