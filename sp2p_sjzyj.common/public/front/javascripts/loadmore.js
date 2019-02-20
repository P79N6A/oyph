define(function(){
	var loadingHtml = '<span class="loading-wrap"><i class="loading-icon"></i>加载中</span>',
		nomoreHtml = '<span>没有更多了</span>',
		loadbtnHtml = '<a href="javascript:void(0);">点击加载更多</a>';
	
	return {
		//加载中方法
		loading : function(){
			$('.loadmore').html(loadingHtml);
		},
		//加载完毕
		loadend : function(){
			$('.loadmore').html(loadbtnHtml);
		},
		//没有更多
		loadnomore : function(){
			$('.loadmore').html(nomoreHtml);
		}
	}
});