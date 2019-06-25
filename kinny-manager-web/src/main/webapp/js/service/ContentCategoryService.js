app.service('ContentCategoryService', function ($http) {



    this.findAll = function () {
        return $http.get('../contentCategory/findAll.do');
    }

    this.findPage = function (pagination, searchEntity) {
        return $http.post('../contentCategory/findPage.do?pageIndex=' + pagination.currentPage
            + '&pageSize=' + pagination.itemsPerPage, searchEntity);
    }

    this.save = function (entity) {
        return $http.post('../contentCategory/save.do', entity);
    }

    this.findOne = function (id) {
        return $http.get('../contentCategory/findOne.do?id=' + id);
    }

    this.delete = function (split) {
        return $http.get('../contentCategory/delete.do?id=' + split);
    }


});