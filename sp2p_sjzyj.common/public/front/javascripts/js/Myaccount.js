window.onload = function(){
	var btn1 = document.getElementById("btn-li");
	var btn2 = document.getElementById("btn-li02");
	var btn3 = document.getElementById("btn-li03");
	var btn4 = document.getElementById("btn-li04");
	var li1 = document.getElementsByClassName("Myloan-mian")[0];
	var li2 = document.getElementsByClassName("Myloan-mian02")[0];
	var li3 = document.getElementsByClassName("Myloan-mian03")[0];
	btn1.onclick = function(){
		li2.style.display = "none";
		li3.style.display = "none";
		li1.style.display = "block";
		$("#btn-li02").removeClass("aaa");
		$("#btn-li03").removeClass("aaa");
		$("#btn-li04").removeClass("aaa");
		$("#btn-li").addClass("aaa");
	}
	btn2.onclick = function(){
		li1.style.display = "none";
		li3.style.display = "none";
		li2.style.display = "block";
		$("#btn-li").removeClass("aaa");
		$("#btn-li03").removeClass("aaa");
		$("#btn-li04").removeClass("aaa");
		$("#btn-li02").addClass("aaa");
	}
	btn3.onclick = function(){
		li2.style.display = "none";
		li1.style.display = "none";
		li3.style.display = "block";
		$("#btn-li02").removeClass("aaa");
		$("#btn-li").removeClass("aaa");
		$("#btn-li04").removeClass("aaa");
		$("#btn-li03").addClass("aaa");
	}
}
