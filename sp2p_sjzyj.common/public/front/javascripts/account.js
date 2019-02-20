define(['dialog'],function(dialog){
	
	// 表格隔行换色
	$('.account-table').each(function(){
		$(this).children('tbody').children('tr:not(.account-bill):even').addClass('even');
	});

	//转让申请
	$('.account-manange').each(function(){
		var debtBtn= $(this).find('.account-debtbtn'),
			debtForm = $(this).find('.account-debt'),
			mainTable = $(this).find('.account-main'),
			backBtn = debtForm.find('.backpage'),
			bill = $(this).find('.account-billbox');
		debtBtn.click(function(){
			mainTable.addClass('none');
			debtForm.removeClass('none');
		});
		backBtn.click(manageInit);
		$('.account-menu').children().click(manageInit);
		function manageInit(){
			mainTable.removeClass('none');
			debtForm.addClass('none');
			bill.hide();
		}
	});
	// 自动投标开启关闭效果
	$('.account-autoclose').click(function(){
		$('.account-autostatus').addClass('close');
	});
	$('.account-autoopen').click(function(){
		$('.account-autostatus').removeClass('close');
	});

	//银行选择
	$('.bank-list li').click(function(){
		$(this).addClass('selected').siblings().removeClass('selected');
	});
});