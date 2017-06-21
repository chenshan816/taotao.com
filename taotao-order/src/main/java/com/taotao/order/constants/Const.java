package com.taotao.order.constants;

public class Const {
	
	public enum OrderStatusEnum{
		
        NO_PAY(1,"未支付"),
        PAID(2,"已付款"),
        NO_SHIP(3,"未发货"),
        SHIPPED(4,"已发货"),
        ORDER_SUCCESS(5,"订单完成"),
        CANCELED(6,"交易关闭");
		
		
		private OrderStatusEnum(int code,String value) {
			this.value = value;
			this.code = code;
		}
		
		private String value;
		private int code;
		public String getValue() {
			return value;
		}
		public int getCode() {
			return code;
		}
	}
	
	public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }
	
	 public enum PayPlatformEnum{
	        ALIPAY(1,"支付宝");

	        PayPlatformEnum(int code,String value){
	            this.code = code;
	            this.value = value;
	        }
	        private String value;
	        private int code;

	        public String getValue() {
	            return value;
	        }

	        public int getCode() {
	            return code;
	        }
	    }
	 
	 public enum PaymentTypeEnum{
	        ONLINE_PAY(1,"在线支付");

	        PaymentTypeEnum(int code,String value){
	            this.code = code;
	            this.value = value;
	        }
	        private String value;
	        private int code;

	        public String getValue() {
	            return value;
	        }

	        public int getCode() {
	            return code;
	        }


	        public static PaymentTypeEnum codeOf(int code){
	            for(PaymentTypeEnum paymentTypeEnum : values()){
	                if(paymentTypeEnum.getCode() == code){
	                    return paymentTypeEnum;
	                }
	            }
	            throw new RuntimeException("么有找到对应的枚举");
	        }
	 }
}
