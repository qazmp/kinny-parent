// 创建模块 高内聚低耦合
var app = angular.module('kinny', []);
app.filter('trustHtml', ['$sce', function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);