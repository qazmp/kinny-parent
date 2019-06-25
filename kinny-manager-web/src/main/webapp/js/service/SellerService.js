app.service('SellerService', function ($http) {

    this.findPage = function (page, criteria) {
        return $http.get('../seller/findPage.do?status='
            + criteria.status
            + "&pageIndex="
            + page.currentPage
            + "&pageSize="
            + page.itemsPerPage
            + "&name="
            + criteria.name
            + "&nickName="
            + criteria.nickName
        );
    }

    this.findOne = function (id) {
        return $http.get('../seller/findOne.do?id=' + id);
    }

    this.updateStatus = function (id, status) {
        return $http.get('../seller/updateStatus.do?id=' + id + "&status=" + status);
    }

});