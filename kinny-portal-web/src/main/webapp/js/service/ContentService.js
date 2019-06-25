app.service('ContentService', function ($http) {

    this.findByCategoryId = function (categoryId) {
        return $http.get('content/findByCategoryId.do?categoryId=' + categoryId);
    }


});