app.service('ItemCatService', function ($http) {



    this.findAll = function () {
        return $http.get('../itemCat/findAll.do');
    }

    this.findByParentId = function (parentId) {
        return $http.get('../itemCat/findByParentId.do?parentId=' + parentId);
    }

    this.findOne = function (id) {
        return $http.get('../itemCat/findOne.do?id=' + id);
    }

    this.save = function (entity) {
        return $http.post('../itemCat/save.do', entity);
    }

    this.bDelete = function (split) {
        return $http.get('../itemCat/batchDelete.do?ids=' + split);
    }



});