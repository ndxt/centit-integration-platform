define(function(require) {
	var Core = require('core/core');
	var Page = require('core/page');
	var Config = require('config');

  // 刷新所以系统数据
	var OsInfoRefreshAll = Page.extend(function() {

		// @override
		this.submit = function(table) {
      var data=$('#osinfoTable').datagrid("getData");

      $.ajax({
        type: 'POST',
        url: Config.ContextPath+'system/sys/os/data/refresh',
        dataType:"json",
        contentType:"application/json",
        data: JSON.stringify(data.rows),
        success: function (e) {
          if(e.data){
            $.messager.alert("操作提示", "所有系统数据刷新成功！","info");
          }else{
            $.messager.alert("操作提示", "部分或所有系统数据刷新失败！","error");
          }
        }
      });

		};
	});

	return OsInfoRefreshAll;
});
