define(["kindeditor","zh_CN"],function(K){
	return function(dom){
		var element = K(dom),
			$editor = $(element);
		var nextFormControl = 'input:not([type="hidden"]), textarea:not(.ke-edit-textarea), button[type="submit"], select';
		var KE = K.create(element, {
			uploadJson : '/common/keImagesUpload',
			allowUpload: true,
			filterMode:false,
			urlType:"domain",
			cssPath: ['/public/back/javascripts/kindeditor/themes/common/default.css'],
			afterBlur: function(){
				this.sync();
				K('.ke-container').removeClass('focus');
			},
			afterFocus: function(){
				K('.ke-container').addClass('focus');
			},
			afterTab: function(){
                var $next = $editor.next(nextFormControl);
                if(!$next.length) $next = $editor.parent().next().find(nextFormControl);
                if(!$next.length) $next = $editor.parent().parent().next().find(nextFormControl);
                $next = $next.first().focus();
                var keditor = $next.data('keditor');
                if(keditor) keditor.focus();
                else if($next.hasClass('chosen')) $next.trigger('chosen:activate');
			},
			afterChange: function(){
				K('.ke-container').removeClass('back-error');
				$editor.next(".back-notice").remove();
			},
			//纯文本粘贴
			pasteType : 1,
			items: [ 'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold', 'italic','underline', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist', 'insertunorderedlist', '|','emoticons', 'image', 'code', 'link', '|', 'removeformat','undo', 'redo', 'fullscreen', 'source', 'about']
		});
		$("#pageSave").click(function(){
			if(KE.isEmpty()){
				if($editor.next(".back-notice").length === 0){
					var note = $("<span class='back-notice'></span>");
					note.text("内容不能为空");
					note.insertAfter($editor);
					K('.ke-container').addClass('back-error');
				}
				event.preventDefault();
			}
		});
		KE.readonly($editor.prop('disabled'));
		if($editor.prop('disabled')){
			K('.ke-container').addClass('disabled');
		}
		$('.finance-edit').click(function(){
			KE.readonly($editor.prop('disabled'));
			if($editor.prop('disabled')){
				K('.ke-container').addClass('disabled');
			}else{
				K('.ke-container').removeClass('disabled');
			}
		});
		return KE;
	}
});