/*投资管理*/
function accountInvestPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyFundCtrl.accountInvestPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	});
}
function accountLoanPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyFundCtrl.accountLoanPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	});
}
function accountInDebtPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyFundCtrl.accountInDebtPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	});
}
function accountOutDebtPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyFundCtrl.accountOutDebtPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	});
}
function accountAutoInvestPre(){
	$.ajax({
		url : "@{front.account.MyFundCtrl.accountAutoInvestPre()}",
		type : "GET",
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
		}
	});
}
$('.btn-licai').click(function(){
	accountInvestPre(1,5);
})
$('.btn-jiekuan').click(function(){
	accountLoanPre(1,5);
})
$('.btn-shourang').click(function(){
	accountInDebtPre(1,5);
})
$('.btn-zhuanrang').click(function(){
	accountOutDebtPre(1,5);
})
$('.btn-toubiao').click(function(){
	accountAutoInvestPre(1,5);
})
/*交易记录*/
function listOfDealRecordsPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyDealCtrl.listOfDealRecordsPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	 });
}

//充值记录
function listOfRechargeRecordsPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyDealCtrl.listOfRechargeRecordsPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	 });
}

//提现记录
function listOfWithdrawRecordsPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyDealCtrl.listOfWithdrawRecordsPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").eq(0).html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	 });
}

//兑换记录
function listOfConversionRecordsPre(currPage, pageSize){
	$.ajax({
		url : "@{front.account.MyDealCtrl.listOfConversionRecordsPre()}",
		type : "GET",
		data : {
			"currPage" : currPage,
			"pageSize" : pageSize
		},
		success : function(data) {
			var flag = interceptorCheck(data);
			if(!flag){
				return;
			}
			$("#noReceiveBill").eq(0).html(data);
			// 表格隔行换色
			$('.account-table').each(function(){
				$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
			});
		}
	 });
}