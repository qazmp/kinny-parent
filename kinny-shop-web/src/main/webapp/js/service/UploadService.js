app.service('UploadService', function ($http) {


    this.upload = function (formData) {
        return  $http({
                    url: '../upload.do',
                    method: 'POST',
                    headers: {'Content-Type': undefined},
                    data: formData,
                    transformRequest: angular.identity
                });
    }


});