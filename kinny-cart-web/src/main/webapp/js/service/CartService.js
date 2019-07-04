app.service('CartService', function ($http) {

   this.showCarts = function () {
       return $http.get('cart/CartList.do');
   }

});