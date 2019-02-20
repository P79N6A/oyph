package models.bean;

/**
 * 请求报文头
 * 
 * @author niu
 * @create 2017.08.24
 */
public class RequestHeader {
	
	/*版本号ID*/
	 private String version;

	/*接入系统号*/
	 private String merchantCode;

	/*交易码*/
	 private String txnType;

	/*客户端请求流水号*/
	 private String clientSn;

	/*客户端日期*/
	 private String clientDate;

	/*客户端时间戳*/
	 private String clientTime;

	/*文件名称*/
	 private String fileName;

	/*签名信息*/
	 private String signature;

	/*时间戳*/
	 private String signTime;
	 
	 

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getClientSn() {
		return clientSn;
	}

	public void setClientSn(String clientSn) {
		this.clientSn = clientSn;
	}

	public String getClientDate() {
		return clientDate;
	}

	public void setClientDate(String clientDate) {
		this.clientDate = clientDate;
	}

	public String getClientTime() {
		return clientTime;
	}

	public void setClientTime(String clientTime) {
		this.clientTime = clientTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}
	
}
