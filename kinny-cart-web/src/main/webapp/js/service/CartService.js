app.service('CartService', function ($http) {

   this.showCarts = function () {
       return $http.get('cart/CartList.do');
   }
   
   this.addGoodsToCartList = function (itemId, num) {
       return $http.get('cart/addToCarts.do?itemId=' + itemId + '&num=' + num);
   }


   this.sum = function (cartList) {
       var totalVal = {totalNum: 0, totalFee: 0};
       for (var i = 0; i < cartList.length; i ++) {
           var cart = cartList[i];
           for (var j = 0; j < cart.itemList.length; j ++) {
               var item = cart.itemList[j];
            totalVal.totalNum += item.num;
            totalVal.totalFee += item.totalFee;
           }
       }
       return totalVal;
   }


});