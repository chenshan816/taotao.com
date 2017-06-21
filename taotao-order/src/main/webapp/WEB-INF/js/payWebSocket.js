/**
 * 扫码支付完毕后后台推送
 */
var ws;
var target="ws://192.168.27.1:8089/websocket";
$(document).ready(function(){
	//进入页面之后,打开socket通道
	subOpen();
	//接收信息
	ws.onmessage = function(event){
		var msg = event.data;
		if(msg == "success"){
			document.getElementById("paySuccess").style.visibility="visible";
		}else{
			document.getElementById("payFail").style.visibility="visible";
		}
	}
});
//开启socket通道
function subOpen(){
	if ('WebSocket' in window) {
        ws = new WebSocket(target);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(target);
    } else {
        alert('你的浏览器不支持websocket功能，请更换');
        return;
    }
}