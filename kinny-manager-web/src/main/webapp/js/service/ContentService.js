app.service('ContentService', function ($http) {


    this.findPage = function (paginationConf) {
        return $http.get('../content/findPage.do?pageIndex='
            + paginationConf.currentPage
            + '&pageSize=' + paginationConf.itemsPerPage);
    }


    this.findOne = function (id) {
        return $http.get('../content/findOne.do?id=' + id);
    }


    this.save = function (entity) {
        return $http.post('../content/save.do', entity);
    }

    this.delete = function (ids) {
        return $http.get('../content/delete.do?ids=' + ids);
    }


});