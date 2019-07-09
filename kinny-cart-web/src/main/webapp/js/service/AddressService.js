app.service('AddressService', function ($http) {

    this.getPrincipalAddressList = function () {
        return $http.get('address/getPrincipalAddressList.do');
    }


});