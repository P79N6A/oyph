define(function(){
 			$(".back-infor").each(function(){
				var that = this,
				editBtn = $(this).find(".finance-edit"),
				editText = $(this).find("textarea");
				editText.prop("disabled",true);
				var email;
				require(['editor'],function(editor){
					email = editor("#inforTextarea");
					editBtn.click(function(){
	 					disable = editText.prop("disabled");
	 					alert(disable);
	 					if(disable){
	 						//进入编辑
	 						editBtn.html("&#xe630;").data("title","保存");
	 						$(".titlebox").text(editBtn.data('title'));
	 						editText.prop("disabled",!disable);
	 						editText.eq(0).focus();
	 					} else {
	 						editNoticeScene(templateId,content)
	 					}
	 				});
					var templateId = editBtn.parent().children("input").val();
					var content = null;
					if($(that).hasClass("email")){
						content = email.html();
					} else {
						content = editText.html();
					}
				})
 				
			}); 

	 			function editNoticeScene(templateId, content) {
					//	alert(templateId+"=----->"+content)
					return;
					$.ajax({
						url : "@{back.webOperation.NoticeMngCtrl.updateTemplateNotice()}",
						type : "POST",
						data : {
							"templateId" : templateId,
							"content" : content
						},
						dataType : "json",
						success : function(result) {
							if (result.code < 1) {
								alert(result.msg);
			
							} else {
								alert(result.msg);
								editBtn.html("&#xe602;").data("title","编辑");
	 							$(".titlebox").text(editBtn.data('title'));
	 							editText.prop("disabled",true);
							}
						},
						error : function() {
							alert("对不起，系统出现错误!");
						}
					});
	 			}
		});