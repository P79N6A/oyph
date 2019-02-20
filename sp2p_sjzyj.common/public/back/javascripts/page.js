//分页，控制当前页数
var showCreentPage = function(formid,currPage){
	$("form[id='"+formid+"'] > input[id='currPage']").val(currPage);
	$("#"+formid).submit();
};

//分页，控制每页显示的数据量，同时将当前页设置为1
var showPageSize = function(formid,pageSize){
	$("form[id='"+formid+"'] > input[id='currPage']").val(1);
	$("form[id='"+formid+"'] > input[id='pageSize']").val(pageSize);
	$("#"+formid).submit();
};