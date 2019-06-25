app.controller('GoodsController', function (GoodsService, $scope, $location, ItemCatService, TypeTemplateService, UploadService, $controller) {

    $controller('BaseController', {$scope: $scope});

    // 新增或修改变量
    $scope.entity = {
        goods: {category2Id: '', category3Id: '', typeTemplateId: -1, brandId: -1},
        goodsDesc: {itemImages: [], specificationItems: []},
        items: [{spec:{}}]
    };

    // 封装了分页时查询条件的js对象
    $scope.searchEntity = {};

    // 数据来源变量
    $scope.select_dataSource_one_category = [];
    $scope.select_dataSource_tow_category = [];
    $scope.select_dataSource_three_category = [];
    $scope.select_dataSource_typeTemplate = {brandIds: [], specIds: [], customAttributeItems: [], id: -1, name: ''};


    /**
     *  初始化页面 准备下拉框 复选框数据来源
     */
    $scope.initPageAsDataSource = function () {
        // 调用分类服务查询第一级分类列表作为下拉框的数据来源
        ItemCatService.findByParentId(0).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.select_dataSource_one_category = response.data;
            }
        });

    }

    // angular 变量监听下拉框级联
    $scope.$watch('entity.goods.category1Id', function (n, o) {
        //$scope.entity.goods.category2Id = ''; // 让三级分类下拉框清空 ng-options 默认是未选择 所以监听不到值的变化
        if (n != undefined && n != null && n != '') {
            ItemCatService.findByParentId(n).success(function (response) {
                if (!response.success) {
                    alert(response.message);
                }else {
                    $scope.select_dataSource_tow_category = response.data;
                }
            });
        }else {
            $scope.select_dataSource_tow_category = [];
        }

        //$scope.select_dataSource_tow_category = []; // 避免覆盖回显变量而且清空了下级下拉框数据来源
        $scope.select_dataSource_three_category = [];

    });

    $scope.$watch('entity.goods.category2Id', function (n, o) {
        //$scope.entity.goods.category3Id = ''; // 修改回显信息会被覆盖
        if (n != undefined && n != null && n != '') {
            ItemCatService.findByParentId(n).success(function (response) {
                if (!response.success) {
                    alert(response.message);
                }else {
                    $scope.select_dataSource_three_category = response.data;
                }
            });
        }else {
            $scope.select_dataSource_three_category = [];
        }

    });

    $scope.$watch('entity.goods.category3Id', function (n, o) {
        if (n != undefined && n != null && n != '') {
            ItemCatService.findOne(n).success(function (response) {
                if(!response.success) {
                    alert(response.message);
                }else {
                    $scope.entity.goods.typeTemplateId = response.data.typeId;
                }
            });
        }else {
            $scope.entity.goods.typeTemplateId = -1;
        }

    });

    /**
     *  通过监听类型模板id 获取品牌规格扩展属性
     */
    $scope.$watch('entity.goods.typeTemplateId', function (n, o) {

        if (n != -1) {
            TypeTemplateService.findOneIncludeOptions(n).success(function (response) {
                if (!response.success) {
                    alert(response.message);
                }else {
                    $scope.select_dataSource_typeTemplate.brandIds = JSON.parse(response.data.brandIds);
                    $scope.select_dataSource_typeTemplate.specIds = JSON.parse(response.data.specIds);
                    $scope.select_dataSource_typeTemplate.customAttributeItems = JSON.parse(response.data.customAttributeItems);

                    // 回显 执行顺序
                    if($location.search()['id'] == null) {
                        $scope.entity.goodsDesc.customAttributeItems = $scope.select_dataSource_typeTemplate.customAttributeItems; // 展示数据也接收数据
                    }else {
                        $scope.select_dataSource_typeTemplate.customAttributeItems = $scope.entity.goodsDesc.customAttributeItems;
                    }

                }

            });
        }

    });

    // 辅助变量用来临时存储图片 赋值给新增js变量
    $scope.local_img = {url: '', color: ''};

    $scope.preparedOpenImageModelClear = function () {
        $scope.local_img = {url: '', color: ''};
        $('input[name=file]').val(null);
    }

    $scope.upload = function () {

        var formData = new FormData();
        formData.append('multipartFile', file.files[0]);

        UploadService.upload(formData).success(function (response) {

            if (!response.success) {
                alert(response.message);
            }else {
                $scope.local_img.url = response.message;
            }

        });
        
    };


    /**
     *  将辅助变量数据赋值给新增变量
     */
    $scope.pupolateEntityByIm = function () {

        $scope.entity.goodsDesc.itemImages.push($scope.local_img);

    }

    // 辅助变量 存储要删除的图片对象
    var preparedDelImageArray = [];

    $scope.addDelImage = function (image, $event) {

        if ($event.target.checked) {
            preparedDelImageArray.push(image);
        }else {
            preparedDelImageArray.splice(preparedDelImageArray.indexOf(image), 1);
        }


    }

    /**
     *  将要删除的图片对象从新增变量中移除
     */
    $scope.rmImages = function () {

        for (var i = 0; i < preparedDelImageArray.length; i ++) {
            $scope.entity.goodsDesc.itemImages.splice($scope.entity.goodsDesc.itemImages.indexOf(preparedDelImageArray[i]), 1);
        }
        preparedDelImageArray = [];
        
    }

    /**
     *  复选框收集用户的输入信息
     * @param specName
     * @param specOptionName
     * @param $event
     */
    $scope.pupolateEntityBySpec = function (specName, specOptionName, $event) {

        var specObject = $scope.getArrayElementByElementKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        if ($event.target.checked) {



            if (specObject == null) {
                $scope.entity.goodsDesc.specificationItems.push({attributeName: specName, attributeValue: [specOptionName]});
            }else {
                specObject.attributeValue.push(specOptionName);
            }
        }else {

            if (specObject != null) {

                // [attributeName: '', attributeValue: []] 防止错误数据
                specObject.attributeValue.splice(specObject.attributeValue.indexOf(specOptionName), 1);
                if (specObject.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(specObject), 1);
                }

            }

        }


    }

    /**
     *  填充sku
     */
    $scope.pupolateEntityItemBySpec = function () {

        $scope.entity.items = [{spec: {}}];

        for (var i = 0; i < $scope.entity.goodsDesc.specificationItems.length; i ++) {

            var specName = $scope.entity.goodsDesc.specificationItems[i].attributeName;
            var optionNames = $scope.entity.goodsDesc.specificationItems[i].attributeValue;


            $scope.entity.items =  objectAddAttr($scope.entity.items, specName, optionNames);

        }


    }

    /**
     *  将指定key values 添加数据的每一个元素中 元素个数根据values 个数累乘
     * @param array
     * @param key
     * @param values
     * @returns {Array}
     */
    var objectAddAttr = function (array, key, values) {

        var a = [];

        for (var j = 0; j < array.length; j ++) {
            for (var i = 0; i < values.length; i ++) {

                var clone = JSON.parse(JSON.stringify(array[j]));
                clone.spec[key] = values[i];

                a.push(clone);

            }
        }


        return a;

    }

    /**
     *  新增商品信息
     */
    $scope.save = function () {
        // 商品详情信息有kindEdit富文本编辑器收集
       $scope.entity.goodsDesc.introduction = editor.html();

       GoodsService.save($scope.entity).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                alert("商品录入成功");
                location.href = 'goods.html';
            }
       });


    }


    // 商品列表

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


    $scope.dispatch = function(id) {
        window.location.href = './goods_edit.html#?id=' + id;
    }

    
    $scope.findOne = function () {

        var id = $location.search()['id'];
        if (id == null) {
            return ;
        }

        GoodsService.findOne(id).success(function (response) {

            if(!response.success) {
                alert(response.message);
            }else {
                $scope.entity = response.data;
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.select_dataSource_typeTemplate.customAttributeItems = $scope.entity.goodsDesc.customAttributeItems;
                editor.html($scope.entity.goodsDesc.introduction);
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                for (var i = 0; i < $scope.entity.items.length; i ++) {
                    $scope.entity.items[i].spec = JSON.parse($scope.entity.items[i].spec);
                }
            }

        });

    };

    $scope.echo_update_specificationOptions = function (specification, option) {

        /**
         *  JSON constructor
         *   entity.goodsDesc.specificationItems [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
         */

        var specification = $scope.getArrayElementByElementKey($scope.entity.goodsDesc.specificationItems, 'attributeName', specification);

        if (specification == null) {
            return false;
        }

        return $scope.deduceArrayContains(specification.attributeValue, option);
    }











});