//带压缩的上传
var imgUpload = function(fileId,callback){
		var file = $("#" + fileId).val();
		var pos = file.lastIndexOf("\\");
		var fileName = file.substring(pos + 1);
		$.ajaxFileUpload({
			url : '/common/imagesUpload',
			secureuri : false,
			fileElementId : fileId,
			data:{
				"fileName":fileName
			},
			dataType : 'json',
			success : function(data) {
				if (data.code < 0) {
					alert(data.msg);
					return;
				}
				if(callback){
					callback(data.obj);
				}
			},
			error : function(data, status, e) {
				alert("上传图片失败");
			}
		})
	}

//不带压缩的上传
var imgUploadNoResize = function(fileId,callback){
	var file = $("#" + fileId).val();
	var pos = file.lastIndexOf("\\");
	var fileName = file.substring(pos + 1);
	$.ajaxFileUpload({
		url : '/common/imagesUploadNoResize',
		secureuri : false,
		fileElementId : fileId,
		data:{
			"fileName":fileName
		},
		dataType : 'json',
		success : function(data) {
			if (data.code < 0) {
				alert(data.msg);
				return;
			}
			if(callback){
				callback(data.obj);
			}
		},
		error : function(data, status, e) {
			alert("上传图片失败");
		}
	})
}