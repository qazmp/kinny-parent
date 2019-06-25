app.controller('TypeTemplateController', function ($scope, TypeTemplateService, $controller, brandService, SpecificationService) {

    $controller('BaseController', {$scope: $scope});

    /**
     *  select2 数据来源对象
     * @type {{data: Array}}
     */
    $scope.brandList = {data: []};
    $scope.specificationList = {data: []};

    $scope.name = '';


    $scope.findAll = function () {
        TypeTemplateService.findAll().success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.list = response.data;
            }
        });
    }

    $scope.queryAsPage = function() {
        TypeTemplateService.findPage($scope.paginationConf, $scope.name).success(function (response) {

            if (!response.success) {
                alert(response.message);
            }else {
                $scope.paginationConf.totalItems = response.data.totalNum;
                $scope.list = response.data.list;
            }

        });
    }

    $scope.search = function() {
        $scope.paginationConf.currentPage = 1;
        $scope.queryAsPage();
    }

    $scope.findOne = function(id) {

        TypeTemplateService.findOne(id).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.entity = response.data;
                $scope.entity.specIds = JSON.parse($scope.entity.specIds);
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
            }
        });

    }

    /**
     *  将json字符串转化为js对象数组 并返回指定key对应的value
     * @param jsonString
     * @param key
     */
    $scope.parseJsonStringAndReturnValue = function (jsonString, key) {

        var jsonObjectArray = JSON.parse(jsonString);

        var s = "";

        for (var i = 0; i < jsonObjectArray.length; i ++) {
            s += jsonObjectArray[i][key];
            if (i < jsonObjectArray.length -1) {
                s += ",";
            }
        }
        return s;
    }

    /**
     *  正要打开新增或修改模态框时查询全部品牌和规格数据以便新增修改类型模板
     */
    $scope.openModeling = function () {

        brandService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i ++) {
                $scope.brandList.data.push({id: response[i].id, text: response[i].name});
            }
        });

        SpecificationService.findAll().success(function (response) {

            if (!response.success) {
                alert(response.message);
            }else {
                var specifications = response.data;
                for (var i = 0; i < specifications.length; i ++) {

                    $scope.specificationList.data.push({id: specifications[i].id, text: specifications[i].specName});
                }

            }
        });

    }

    $scope.afterClearEntityDefinition = function() {
        $scope.entity = {customAttributeItems: []};
    }

    $scope.dynamicTable_add_customAttributeItems = function () {
        $scope.entity.customAttributeItems.push({});
    }
    $scope.dynamicTable_remove_customAttributeItems = function (object) {
        $scope.entity.customAttributeItems.splice($scope.entity.customAttributeItems.indexOf(object), 1);
    }

    $scope.save = function () {
        TypeTemplateService.save($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.paginationConf.currentPage = 1;
                $scope.queryAsPage();
            }
        });
    }
    
    $scope.delete = function () {

        TypeTemplateService.delete($scope.arrayToSplitString($scope.ids)).success(function (response) {

            if (!response.success) {
                alert(response.message);
            }else {
                $scope.queryAsPage();
            }

        });


    }
    

});