app.service('LoginService', function ($http) {

    this.principal = function () {
        return $http.get('login/principal.do');
    }


});