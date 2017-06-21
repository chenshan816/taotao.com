<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>websocket测试</title>
<script type="text/javascript" src="/js/jquery-1.6.4.js"></script>
<script type="text/javascript" src="/js/payWebSocket.js" charset="utf-8"></script>
<script type="text/javascript">
$(document).ready(function () {
   $("#btnConfirm").click(function () {
		jQuery.ajax({
			url : 'click',
			type : "GET"
		});
	});
})
</script>
</head>
<body>
	
	<button id = "btnConfirm">提交</button>
	<div id="paySuccess" style="visibility: hidden;">success</div>
	<div id="payFail" style="visibility: hidden;">fail</div>
</body>
</html>