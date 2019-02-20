$("#occupation").change(function(){
	var describe=$("#occupation").find("option:selected").text()
	if (describe=='其他'){
		$('#profession').attr("disabled",false);
		$('.profession').css({'color':'black'});
	}else{
		$('#profession').attr("disabled",true);
		$('#profession').val('')
		$('.profession').css({'color':'#ccc'});
	}
})
$('#genroll').click(function(){
	var occupation=$("#occupation").find("option:selected").text()
	var profession=$('#profession').val()
	if(occupation=='其他'){
		if(profession==''){
			alert('请填写职业描述')
			return false
		}
	}
})