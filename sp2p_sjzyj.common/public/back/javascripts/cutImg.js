//图片裁剪
function cropImg(width, height){
	var size = new Array(width, height);
	var src = $("#imageUrl").val()+'?t=' + new  Date().getTime();
	var imageType = $("#imageFormat").val();
	$('.crop-dialog').dialog();
	var image = new Image();
	image.src = src;
	image.onload = function(){
		require(["imgcrop"],function(crop){
			$('.crop-img,.preview-container').find('img').attr('src',src);
			$('#imageType').val(imageType);
			$("input[name='cropsize']").val([]);
			crop(size);
		});
	}
}

function saveCropImg(callback){
	$.ajax({
		url : '/supervisor/imgCrop',
		type: 'POST',
		data : {
			'imgSrc' : $("#cropimg").attr('src'),
			'x' : $("#x").val(),
			'y' : $("#y").val(),
			'w' : $("#w").val(),
			'h' : $("#h").val(),
			'imageType' : $("#imageType").val(),
			'createNewImg' : true
		},
		success : function(data){
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			
			if(data.code < 1){
				alert(data.msg);
				return;
			}
			
			if (callback) {
				callback(data.obj);
			}
			$("#dialogClose").trigger('click');
			weakDialog('裁剪成功!');
		}
	});
}