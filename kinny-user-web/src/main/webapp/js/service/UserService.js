app.service('UserService', function ($http) {

    this.registry = function (entity, smsCode) {
        return $http.post('user/registry.do?smsCode=' + smsCode, entity);
    }

    this.sendSms = function (phone) {
        return $http.get('user/sendSms.do?phone=' + phone);
    }


});