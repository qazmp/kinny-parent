app.controller('ItemPageController', function($scope) {

	$scope.num = 1; // ��������
	
	$scope.checkedSpec = {}; // ѡ�еĹ����Ϣ

	$scope.currentSku = {}; // ��ǰ��ʾ��sku

	$scope.addNum = function(i) {
	
		$scope.num += i;

		if($scope.num < 1) {
			$scope.num = 1;
		}
	}
	/**
	 *  ��¼ѡ�еĹ�� ���ö������Ա������
	 */
	$scope.addCheckedSpecification = function(spec, specOption) {
		$scope.checkedSpec[spec] = specOption;
		searchSku();
		
	}
	
	
	/**
	 * ����Ƿ�ѡ��
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
	 * ƥ�����������Ƿ����
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
	
	// �����û�ѡ��Ĺ�� �ҵ�ָ����sku
	var searchSku = function() {
		// ѡ�����Ӧ��sku
		for(var i = 0; i < specList.length; i ++) {
			if(objectEquals($scope.checkedSpec, specList[i].spec)) {
				$scope.currentSku = specList[i];
				return ; 
			}
		}
		// û��ѡ�����sku
		$scope.checkedSpec = {id: -1, title: '------', price: 0};
	}
	
	$scope.addTocat = function() {
		alert("ѡ���sku id " + $scope.currentSku.id);
	}
	


});