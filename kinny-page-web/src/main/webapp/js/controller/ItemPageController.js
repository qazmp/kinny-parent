app.controller('ItemPageController', function($scope) {

	$scope.num = 1; // 购买数量
	
	$scope.checkedSpec = {}; // 选中的规格信息

	$scope.currentSku = {}; // 当前显示的sku

	$scope.addNum = function(i) {
	
		$scope.num += i;

		if($scope.num < 1) {
			$scope.num = 1;
		}
	}
	/**
	 *  记录选中的规格 利用对象属性保存机制
	 */
	$scope.addCheckedSpecification = function(spec, specOption) {
		$scope.checkedSpec[spec] = specOption;
		searchSku();
		
	}
	
	
	/**
	 * 规格是否选中
	 */
	$scope.isChecked = function(spec, specOption) {
		
		if($scope.checkedSpec[spec] == specOption) {
			return true;
		}
		return false;
	}

	$scope.initDefaultSku = function() {
		
		$scope.currentSku = specList[0];
		$scope.checkedSpec = JSON.parse(JSON.stringify($scope.currentSku.spec));
	
	}
	
	/**
	 * 匹配两个对象是否相等
	 */
	var objectEquals = function(obj1, obj2) {
		
		for(var key in obj1) {
			if(obj1[key] != obj2[key]) {
				return false;
			}
		}
		for(var key in obj2) {
			if(obj1[key] != obj2[key]) {
				return false;
			}
		}
		return true;
			
	}
	
	// 根据用户选择的规格 找到指定的sku
	var searchSku = function() {
		// 选择规格对应的sku
		for(var i = 0; i < specList.length; i ++) {
			if(objectEquals($scope.checkedSpec, specList[i].spec)) {
				$scope.currentSku = specList[i];
				return ; 
			}
		}
		// 没有选择规格的sku
		$scope.checkedSpec = {id: -1, title: '------', price: 0};
	}
	
	$scope.addTocat = function() {
		alert("选择的sku id " + $scope.currentSku.id);
	}
	


});