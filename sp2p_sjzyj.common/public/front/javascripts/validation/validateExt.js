define(["validate","messages"],function(){
	jQuery.validator.addMethod("isZipCode", function(value, element) {   
		var tel = /^[0-9]{6}$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的邮政编码");
	
	jQuery.validator.addMethod("isMobile", function(value, element) {   
		var tel = /^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的手机号码");

	jQuery.validator.addMethod("isFox", function(value, element) {   
		var tel = /^(\d{3,4}-)?\d{7,8}$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写您的传真(如xxx-1234567,xxxx-12345678,xxxx-12345678)");
	
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
	
	jQuery.validator.addMethod("isPassWord", function(value, element) {   
		var tel = /^([0-9a-zA-Z]{6,15})$/;
		return this.optional(element) || (tel.test(value));
	}, "请正确填写密码");
	
	jQuery.validator.addMethod("isEmail", function(value, element) {
		var tel = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
		return this.optional(element) || (tel.test(value));
	}, "邮箱格式不正确");
	
	jQuery.validator.addMethod("isHundredMultiple", function(value, element){
		return this.optional(element) || (value % 100 == 0 && value >= 100);
	}, "请输入100的正整数倍!");
});