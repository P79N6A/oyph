require(["jquery","easyui"],function($){
	easyloader.locale = "zh_CN";
	var nowDate = new Date(),
		nowYear = nowDate.getFullYear(),
		nowMonth = nowDate.getMonth()+1,
		nowDay = nowDate.getDate(),
		nowHours = nowDate.getHours(),
		nowMinutes = nowDate.getMinutes();
		nowSeconds = nowDate.getSeconds(); 
	var nowTime = nowYear+"/"+ (nowMonth>10? nowMonth : ("0"+nowMonth))+"/"+(nowDay>10? nowDay : ("0"+nowDay))+" "+nowHours+":"+nowMinutes+":"+nowSeconds;
	using("datetimebox",function(){
		$('.easyui-datetimebox').each(function(){
			$.parser.parse();
			var defaultVal = $(this).val();
			
			$(this).datetimebox({
				width: 243,
				height: 30,
				value:function(){
					return defaultVal || nowTime;
				}(),
				showHours:true,
				showMinutes:true,
			    showSeconds: true,
			    panelWidth: 243,
			    editable: false,
			    onShowPanel: function(){
			    	$(this).next().css({
			    		"border-color": "#5A9CCB",
						"box-shadow": "0 0 5px #5A9CCB"
			    	});
			    },
			    onHidePanel: function(){
			    	$(this).next().css({
			    		"border-color": "#ccc",
						"box-shadow": "none"
			    	});
			    }
			});
		});
		
		$('.easyui-datetimeboxs').each(function(){
			$.parser.parse();
			var defaultVal = $(this).val();
			
			$(this).datetimebox({
				width: 164,
				height: 30,
				value:function(){
					return defaultVal || nowTime;
				}(),
				showHours:false,
				showMinutes:false,
			    showSeconds: false,
			    
			    panelWidth: 164,
			    editable: false,
			    onShowPanel: function(){
			    	$(this).next().css({
			    		"border-color": "#5A9CCB",
						"box-shadow": "0 0 5px #5A9CCB"
			    	});
			    },
			    onHidePanel: function(){
			    	$(this).next().css({
			    		"border-color": "#ccc",
						"box-shadow": "none"
			    	});
			    }
			});
		});
	});
	
});