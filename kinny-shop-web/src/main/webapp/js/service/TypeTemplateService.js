app.service('TypeTemplateService', function ($http) {



    this.findAll = function () {
        return $http.get('../typeTemplate/findAll.do');
    }

    this.findPage = function (entity, name) {
        return $http.get(
            '../typeTemplate/findPage.do?pageIndex='
            + entity.currentPage
            + "&pageSize="
            + entity.itemsPerPage
            + "&name="
            + name);
    }

    this.findOne = function (id) {
        return $http.get('../typeTemplate/findOne.do?id=' + id);
    }

    this.findOneIncludeOptions = function (id) {
        return $http.get('../typeTemplate/findOneIncludeOptions.do?id=' + id);
    }


    this.save = function (entity) {
        return $http.post('../typeTemplate/save.do', entity);
    }

    this.delete = function (idSplit) {
        return $http.get('../typeTemplate/delete.do?id=' + idSplit);
    }





});