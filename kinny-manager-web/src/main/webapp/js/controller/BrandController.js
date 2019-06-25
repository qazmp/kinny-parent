// 创建控制器 与HTML view交互
/**
 *  $scope html 与控制器交互的桥梁 可以理解为 $scope.xx 代表该变量在HTML中可以访问到 公开的
 */
app.controller('brandController', function (brandService, $scope, $controller) {


    $controller('BaseController', {$scope: $scope});



    /**
     *  查询全部品牌数据 不分页
     */
    $scope.findAll = function() {
        brandService.findAll().success(function (response) {

            $scope.list = response;

        });
    };

    /**
     *  查询品牌分页
     */
     $scope.queryAsPage = function () {

        brandService.findAsPage($scope.paginationConf).success(function (response) {

            // 修改分页对象 修改dom
            $scope.paginationConf.totalItems = response.data.totalNum;

            $scope.list = response.data.list;


        });

    };

    /**
     *  查询单个品牌信息 修改是 打开模态框之前回显品牌数据
     *  对与模态框绑定的变量赋值
     * @param id
     */
    $scope.findOne = function(id) {

        brandService.findOne(id).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.entity = response.data;
            }
        });

    };

    /**
     *  校验用户输入信息
     *  ①实时的表单元素样式提示
     *  ②保存按钮是否关闭模态框
     * @returns {{name: boolean, firstChar: boolean, isOk: boolean}}
     */
    $scope.validateBrand = function () {
        var name = $scope.entity['name'];
        var reslut = {name: true, firstChar: true, isOk: true};
        if (name == null || name == '') {
            reslut.name = false;
            reslut.isOk = false;
        }


        var firstChar = $scope.entity['firstChar'];
        if (firstChar == null || firstChar == '') {
            reslut.firstChar = false;
            reslut.isOk = false;
        }

        return reslut;
    };

    /**
     *  新增或者修改品牌信息
     */
    $scope.saveOrUpdate = function () {

        var result = $scope.validateBrand();
        if (!result.isOk) {
            return ;
        }

        brandService.save($scope.entity).success(function (response) {

            if(response.success == false) {
                alert(response.message);
                return ;
            }
            $scope.queryAsPage();
        });


    };


    /**
     *  批量删除
     */
    $scope.batchDelete = function () {
        var ids = $scope.arrayToSplitString($scope.ids);
        brandService.delete(ids).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.queryAsPage();
            }

        });
    };






});
