app.service('ItemSearchService', function ($http) {


    this.search = function (map) {
        return $http.post('ItemSearch/search.do', map);
    }

});