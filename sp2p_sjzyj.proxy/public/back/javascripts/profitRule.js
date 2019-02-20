	function delRow(liId) {
		if(confirm("确认删除吗?" , function () {
			var obj = document.getElementById(liId);
			obj.remove();
		}));
	}
	var i = 10000;
	
	function addRow() {
		$('#proxy').append('<tr id="'+i+'"><td><input type="text" class="monthBox qixian" style="background:white;" value="" /><span class="distancess">个月</span></td><td><input type="text" class="monthBox zhesuan" style="background:white;" value="" /><span class="distancess">%</span></td><td><p class="formula"></p></td><td><span class="delZhe" onclick="delRow('+i+')" style=" cursor: pointer; float: right; ">删除</span></td></tr>') 
			 i++;
	}
	
	function addSaleManRuleRow() {
		$("#manRule").append('<tr id="'+i+'"><td><input class="generalize min" type="text" style="background:white;" value=""><span>万~~</span><input class="generalize max" type="text" style="background:white;" value=""><span>万</span>:提成=推广客户理财总金额的<input class="generalize amount" type="text" style="background:white;" value=""><span>‱</span></td><td><span class="delSales" onclick="delRow('+i+')" style="cursor: pointer; float: right; ">删除</span></td></tr>');
		i++;
	}
	
	function addAnnualConvertRow() {
		$("#convert").append('<tr id="'+i+'"><td><input type="text" class="monthBox qixian" style="background:white;" value="" /><span class="distancess">个月</span></td><td><input type="text" class="monthBox zhesuan" style="background:white;" value="" /><span class="distancess">%</span></td><td><p class="formula"></p></td><td><span class="delZhe" onclick="delRow('+i+')" style=" cursor: pointer; float: right; ">删除</span></td></tr>');
		i++;
	}
	
	var reglv=/^\d+(\.\d{2}|\.\d{1})?$/;

	 function jiaoYan(id){
		 var thisInputs=[];
		 $("input[type=text]",document.forms[id]).each(function(){
			  thisInputs.push($(this).val());
		 });
		 var flag=0;
		 for(var i=0;i<thisInputs.length;i++){
			 if(!reglv.test(thisInputs[i])){
				 flag=1;
			 }
		 }
		 return flag;
	 }