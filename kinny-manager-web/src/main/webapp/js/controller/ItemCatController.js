app.controller('ItemCatController', function ($scope ,ItemCatService, TypeTemplateService, $controller) {

    $controller('BaseController', {$scope: $scope});


    $scope.currentCategoryLevel = 0; // 当前分类的级别

    $scope.oneLevelCategory = {};
    $scope.towLevelCategory = {};
    $scope.threeLevelCategory = {};

    $scope.typeTemplateList = {data:[]};

    /**
     *  监听分类级别 修改分类
     */
    /*$scope.$watch($scope.currentCategoryLevel, function (n, o) {
        // 根据运行级别修改面包屑变量
        if (n == 1) {
            $scope.oneLevelCategory = {};
            $scope.towLevelCategory = {};
            $scope.threeLevelCategory = {};
        }else if(n == 2) {
            $scope.towLevelCategory = {};
            $scope.threeLevelCategory = {};
        }else if(n == 3) {
            $scope.threeLevelCategory = {};
        }
    });*/

    var DynamicCrumbs = function (entity, level) {
        if(level != '' && level != "" && level != null && level != undefined) {
            $scope.currentCategoryLevel = level;
        }else {
            $scope.currentCategoryLevel ++;
        }

        if($scope.currentCategoryLevel == 2) {
            $scope.oneLevelCategory = entity;
        }else if($scope.currentCategoryLevel == 3) {
            $scope.towLevelCategory = entity;
        }else if($scope.currentCategoryLevel == 4) {
            $scope.threeLevelCategory = entity;
        }

        if (level == 1) {
            $scope.oneLevelCategory = {};
            $scope.towLevelCategory = {};
            $scope.threeLevelCategory = {};
        }else if(level == 2) {
            $scope.towLevelCategory = {};
            $scope.threeLevelCategory = {};
        }else if(level == 3) {
            $scope.threeLevelCategory = {};
        }

    }


    $scope.findByParentId = function (parentId, entity, level) {

        DynamicCrumbs(entity, level);

        ItemCatService.findByParentId(parentId).success(function (response) {

            if(!response.success) {
                alert(response.message);
            }else {
                $scope.list = response.data;
            }

        });


    };

    /**
     *  获取当前分类列表的父分类
     *  根据面包屑entity
     * @returns {*}
     */
    var getParent = function() {
        if ($scope.currentCategoryLevel == 1) {
            return null;
        }else if($scope.currentCategoryLevel == 2) {
            return $scope.oneLevelCategory;
        }else if($scope.currentCategoryLevel == 3) {
            return $scope.towLevelCategory;
        }
    }

    /**
     *  刷新分类 在当前父分类下
     */
    var reloadCurrentCategory = function() {
        $scope.findByParentId(getParent() == null? 0: getParent().id, getParent(), $scope.currentCategoryLevel);
    }


    $scope.findOne = function(id) {
        ItemCatService.findOne(id).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.entity.id = response.data.id;
                $scope.entity.parentId = response.data.parentId;
                $scope.entity.name = response.data.name;
                $scope.entity.typeId = response.data.typeId;

            }
        });
    }

    var highClone = function(object) {
        return JSON.parse(JSON.stringify(object));
    }

    $scope.openingModel = function () {

        TypeTemplateService.findAll().success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                for (var i = 0; i < response.data.length; i ++) {
                    $scope.typeTemplateList.data.push({id: response.data[i].id, text: response.data[i].name});
                }
            }
        });


    };
    
    $scope.preparedOpenModel1 = function (type, id) {

        var callBack = function() {

            if ($scope.currentCategoryLevel == 1) {
                $scope.entity.topCategory = ''; // 不是添加对象属性吗
            }else if ($scope.currentCategoryLevel == 2) {
                $scope.entity.topCategory = $scope.oneLevelCategory.name;
            }else if ($scope.currentCategoryLevel == 3) {
                $scope.entity.topCategory = $scope.oneLevelCategory.name + ' >>  ' +  $scope.towLevelCategory.name;
            }
        };

        $scope.preparedOpenModelExt(type, id, callBack);

    }


    $scope.save = function () {

        $scope.entity.parentId = getParent() == null? 0: getParent().id;

        ItemCatService.save($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                reloadCurrentCategory();
            }
        });
        
    }
    
    $scope.delete = function () {
        ItemCatService.bDelete($scope.arrayToSplitString($scope.ids)).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                reloadCurrentCategory();
            }
        });
    }


    
    
    

});