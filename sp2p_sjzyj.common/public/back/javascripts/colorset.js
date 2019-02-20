//选项卡
define(["require","colpick"],function(require,$){
	var newUrl = require.toUrl("colpick.css"),
		newStyle = $("<link type='text/css' rel='stylesheet' href="+newUrl+" />");
	newStyle.appendTo($("head"));
	$('.colpicker').colpick({

		layout:'hex',

		submit:0,

		colorScheme:'dark',

		onChange:function(hsb,hex,rgb,el,bySetColor) {

			$(el).siblings(".colpick-show").css('background-color','#'+hex);

			// Fill the text box just if the color was set using the picker, and not the colpickSetColor function.

			if(!bySetColor) $(el).val(hex);

		}

	}).keyup(function(){

		$(this).colpickSetColor(this.value);

	});
	$(".colpick-show").each(function(){
		$(this).css("background-color","#"+$(this).siblings(".colpicker").val());
	});
});
