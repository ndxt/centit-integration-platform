define(function(require) {
	var Core = require('core/core');
	var Page = require('core/page');
	var Config = require('config');

	// 刷新单个系统数据
	var OsInfoRegreshOne = Page.extend(function() {

		// @override
		this.submit = function(table, data) {

		  var dataList = [];
		  dataList.push(data);
      $.ajax({
        type: 'POST',
        url: Config.ContextPath+'system/sys/os/data/refresh',
        dataType:"json",
        contentType:"application/json",
        data: JSON.stringify(dataList),
        success: function (e) {
          if(e.data){
            $.messager.alert("操作提示", "所选系统数据刷新成功！","info");
          }else{
            $.messager.alert("操作提示", "所选系统数据刷新失败！","error");
          }
        }
      });


    };
	});

	return OsInfoRegreshOne;
});
