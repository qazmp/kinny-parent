app.service('GoodsService', function ($http) {



    this.save = function (entity) {
        return $http.post('../goods/save.do', entity);
    }

    this.findPage = function (entity, pagination) {
        return $http.post('../goods/findPage.do?pageIndex=' + pagination.currentPage + "&pageSize=" + pagination.itemsPerPage,
                entity);
    }

    this.findOne = function (id) {
        return $http.get('../goods/findOne.do?id=' + id);
    }


});