app.controller('ContentController', function ($scope, ContentService) {


    $scope.list = [];

    $scope.keywords = '';

    $scope.findByCategoryId = function (categoryId) {
        ContentService.findByCategoryId(categoryId).success(function (response) {
            if (!response.success) {
                alert(response.message);
            }else {
                $scope.list[categoryId] = response.data;
            }
        });

    }

    $scope.search = function () {
        location.href = 'http://localhost:9104/search.html#?keywords=' + $scope.keywords;
    }



});