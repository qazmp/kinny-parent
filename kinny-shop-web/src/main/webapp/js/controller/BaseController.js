/**
 *  父类控制器 封装的是所有子类控制器都含有的重复代码
 */
app.controller('BaseController', function ($scope) {

// 品牌列表变量 向用户展示数据
    $scope.list = [];

    // 封装了新增 或 修改数据的对象 {'id': '23','name': '努比', 'firstChar': 'n'}
    $scope.entity = {};

    // 批量删除的id数组
    $scope.ids = [];

    // 存储分页信息的js对象 pagination js中的代码会修改
    $scope.paginationConf = {
        currentPage: 1, // 当前页数
        itemsPerPage: 10, // 每页数据条数
        totalItems: 0, // 总记录条数
        onChange: function () { // 分页时 当前页数发生改变触发的监听函数

            $scope.queryAsPage();

        }
    };


    /**
     * 打开模态框之前要做的工作
     *  新增 清空模态框绑定的变量
     *  修改 请求后台api接口 并赋值给模态框绑定的变量
     * @param type 判断新增或者是修改
     * @param id 修改记录id
     */
    $scope.preparedOpenModel = function (type, id) {
        if(type == 'insert') {
            clearModelVariable();
        } else if (type == "update") {
            $scope.findOne(id);
        }

    };


    $scope.preparedOpenModelExt = function (type, id, callBack) {

        if(type == 'insert') {
            clearModelVariable();
        } else if (type == "update") {
            $scope.findOne(id);
        }
        callBack();

    };

    /**
     *  清空与模态框绑定的变量
     */
    var clearModelVariable = function () {
        $scope.entity = {};
    };

    /**
     *  根据复选框是否选中将其所对应的的记录主键添加到angularJs变量 已准备批量删除
     * @param $event
     * @param id
     */
    $scope.checkBoxSelected = function ($event, id) {

        if ($event.target.checked == true) {
            $scope.ids.push(id);
        }else {
            $scope.ids.splice($scope.ids.indexOf(id), 1);
        }

    };

    /**
     *  将js数组中的元素 遍历拼接位 1-2-3-
     * @param array
     * @returns {string}
     */
    $scope.arrayToSplitString = function (array) {
        var s = "";

        for (var i = 0; i < array.length; i ++) {
            s += array[i] + "-";
        }
        return s;
    };


    /**
     *  复选框全选反选
     */
    $scope.allChecked = function ($event) {

        if ($event.target.checked) {
            $scope.ids = [];
            for (var i = 0; i <  $scope.list.length; i ++) {
                $scope.ids.push($scope.list[i].id);
            }
        }else {
            $scope.ids = [];
        }

    }

    /**
     *  判断复选框是否选中 复选框状态回显
     */
    $scope.isChecked = function (id) {
        return $scope.deduceArrayContains($scope.ids, id);
    }

    /**
     *  推断数组中是否含有指定元素
     * @param Array
     * @param item
     * @returns {boolean}
     */
    $scope.deduceArrayContains = function (Array, item) {
        return Array.indexOf(item) >= 0? true: false;
    }


    $scope.getArrayElementByElementKey = function (array, key, value) {

        for (var i = 0; i < array.length; i ++) {

            if (array[i][key] == value) {
                return array[i];
            }

        }
        return null;

    }




});