app.service('UploadService', function ($http) {


    this.upload = function (formData) {
        return $http({
            url: '../upload.do',
            method: 'POST',
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            data: formData
        });
    }


});