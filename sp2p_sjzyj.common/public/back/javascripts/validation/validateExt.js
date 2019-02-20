define(["validate","messages"],function(){
	jQuery.validator.addMethod("isZipCode", function(value, element) {   
		var tel = /^[0-9]{6}$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的邮政编码");
	
	jQuery.validator.addMethod("isMobile", function(value, element) {   
		var tel = /^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))(\d{8})$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的手机号码");
	
	jQuery.validator.addMethod("isFox", function(value, element) {   
		var tel = /^(\d{3,4}-)?\d{7,8}$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的传真(如xxx-1234567,xxxx-12345678,xxxx-12345678)");
	
	jQuery.validator.addMethod("isHotline", function(value, element) {   
		var tel = /^(1[3,5,8,7]{1}[\d]{9})|(((400)(\d{1})-(\d{3})-(\d{2,4}))|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{3,7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写客服联系方式!");
	
	jQuery.validator.addMethod("isFloat", function(value, element) {   
		var tel = /^(\d+\.\d{1,1}|\d+)$/;
		return this.optional(element) || (tel.test(value));
	}, "整数或者一位小数!");
	
	jQuery.validator.addMethod("isHundredMultiple", function(value, element){
		return this.optional(element) || (value % 100 == 0 && value >= 100);
	}, "请输入100的正整数倍!");
	
	jQuery.validator.addMethod("isThousandMultiple", function(value, element){
		return this.optional(element) || (value % 1000 == 0 && value>= 1000);
	}, "请输入1000的正整数倍!");
	
	jQuery.validator.addMethod("isBigger", function(value, element, param){
		var obj = $(param).val();
		return this.optional(element) || ((value-obj) >= 0);
	}, "必须大于或等于最小值!");
	
	jQuery.validator.addMethod("isSmall", function(value, element, param){
		var obj = $(param).val();
		return this.optional(element) || ((obj-value) >= 0);
	}, "必须小于或等于最大值!");
	jQuery.validator.addMethod("changeRange", function(value, element, param){
		var obj = $(param).val();
		if(obj==1){//天标，范围1-30 (这里的1和2与借款产品期限类型强耦合)
			return  (this.optional(element)) || (value <=30 && value>=1)
		}else{//月标，范围1-36
			return  (this.optional(element)) || (value <=36 && value>=1)
		}
	}, "请输入正确期限范围!");
	
	jQuery.validator.addMethod("buyType1", function(value, element, param){
		var obj = $(param).val();
		if(obj==1){
			var blank = /^\s*$/;
			var flag = !blank.test(value);
			
			var tel = /^(\d+)$/;
			var flag1 = tel.test(value);
			
			var flag2 = (value <=1000 && value>=1);
			
			return flag && flag1 && flag2;
		}else if(obj == 2){//月标，范围1-36
			
			return true;
		}
	}, "1-1000的正整数");
	
	jQuery.validator.addMethod("buyType2", function(value, element, param){
		var obj = $(param).val();
		if(obj==1){
			return true;
		}else if(obj == 2){//月标，范围1-36
			
			var blank = /^\s*$/;
			var flag = !blank.test(value);
			
			return  flag && (value ==10 || value ==20 || value ==50 || value ==100)
		}
	}, "值为10、20、50、100");
	
	jQuery.validator.addMethod("isPassWord", function(value, element) {   
		var tel = /^([0-9a-zA-Z]{6,15})$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写密码");
	
	
	jQuery.validator.addMethod("isColor", function(value, element) {   
		var tel = /^([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写或者选择颜色值!");
	
});