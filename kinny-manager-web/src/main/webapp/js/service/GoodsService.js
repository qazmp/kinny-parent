app.service('GoodsService', function ($http) {


    this.findPage = function (entity, pagination) {
        return $http.post('../goods/findPage.do?pageIndex=' + pagination.currentPage + "&pageSize=" + pagination.itemsPerPage,
            entity);
    }

    this.updateStatus = function (entity) {
        return $http.get('../goods/updateStatus.do?id=' + entity.id + '&status=' + entity.auditStatus);
    }

    this.delete = function (ids) {
        return $http.get('../goods/delete.do?ids=' + ids);
    }


});