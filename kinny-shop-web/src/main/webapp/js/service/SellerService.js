app.service('SellerService', function ($http) {

    this.add = function (entity) {
        return $http.post('seller/add.do', entity);
    }
    
    this.login = function (entity) {
        return $http.get('seller/login.do?username=' + entity.sellerId + "&password=" + entity.password);
    }

    this.getPrincipal = function () {
        return $http.get('../seller/getPrincipal.do');
    }

});