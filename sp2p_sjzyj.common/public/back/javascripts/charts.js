require.config({
	paths: {
		"echarts": 'dist'
	}
});
define(
    [
        'echarts',
        'echarts/chart/line'
    ],
    function (ec) {
    	return function(dom,legendArray,xAxisArray,yAxisName){
	        var myChart = ec.init(dom); //dom
	        var option = {
				   tooltip: {
				       trigger: "axis"
				   },
				   legend: {
				       data: function(){
				           var array = new Array();
				           for(var i= 0; i< legendArray.length; i++){
				           		array[i] = legendArray[i].name;
				           }
				           return array;
				       }(), //设置下方标题
				       y: "bottom",
				       itemGap: 30,
				       textStyle: {
				       	   fontSize: 14
				       }
				   },
				   toolbox: {
				       feature: {
				           mark: {
				               show: true
				           },
				           dataView: {
				               show: true,
				               readOnly: true
				           },
				           magicType: {
				               show: false,
				               type: ["line", "bar"]
				           },
				           restore: {
				               show: true
				           },
				           saveAsImage: {
				               show: true
				           }
				       }
				   },
				   grid: {
				   	width: "90%",
				   	height: "250px",
				   	x: "5%",
				   	y: "20px"
				   },
				   xAxis: [
				       {
				           type: "category",
				           boundaryGap: true,
				           data: xAxisArray,//xAxisArray
					   	   splitLine: {
					   			show: false
					   	   }
				       }
				   ],
				   yAxis: [
				       {
				           type: "value",
					   	   splitNumber: 4,
					   	   name: yAxisName || ""
				       }
				   ],
				   series: function(){
				   		var array = [];
				   		for(var i= 0; i< legendArray.length; i++){
				   			array[i] = {
				   				name: legendArray[i].name,
				   				type: "line",
				   				data: legendArray[i].data
				   			}
				   		}
				   		return array;
				   }()
				}
	        myChart.setOption(option);
	        window.onresize = myChart.resize;
        }
    }
);