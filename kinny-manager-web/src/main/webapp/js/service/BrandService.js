// 创建服务 分层开发 易于维护的扩展 增强可读性
/**
 *  $http angularjs 内置服务 异步访问后台api接口 可以作为实参传递过来 依赖注入
 *  主键按照一种预先定义好的方式接受容器的资源注入
 */
app.service('brandService', function ($http) {

    // js 对象添加属性 函数 this引用的是回调函数所在对象
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    };

    this.findAsPage = function (pageEntity) {
       return $http.get('../brand/findPage.do?pageIndex=' + pageEntity.currentPage + "&pageSize=" + pageEntity.itemsPerPage);
    }
    
    this.save = function (entity) {
        return $http.post('../brand/save.do', entity);
    }

    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    }

    this.delete = function (ids) {
        return $http.get('../brand/delete.do?ids=' + ids);
    }
    

});