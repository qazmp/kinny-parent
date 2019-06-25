app.service('SpecificationService', function ($http) {
    
    this.findAll = function () {
        return $http.get('../specification/findAll.do');
    }

    this.findPage = function (entity, specName) {
        return $http
            .get(
                '../specification/findPage.do?pageIndex='
                + entity.currentPage
                + "&pageSize="
                + entity.itemsPerPage
                + "&specName="
                + specName
            );
    }

    this.findOne = function (id) {
        return $http.get('../specification/findOne.do?id=' + id);
    }

    this.add = function (entity) {
        return $http.post('../specification/add.do', entity);
    }

    this.update = function (entity) {
        return $http.post('../specification/update.do', entity);
    }

    this.delete = function (idSplit) {
        return $http.get('../specification/delete.do?idSplit=' + idSplit);
    }

});