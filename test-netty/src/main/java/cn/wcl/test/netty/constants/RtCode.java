package cn.wcl.test.netty.constants;

public enum RtCode {
	// 成功
	SUCCESS("0000", "成功"),
	// 失败 ：系统错误

	ERROR_SYS("9999", "系统异常"),
	ERROR_DATA_FORMATE("0001", "数据格式不合法"),
    ERROR_MD5("0002", "数据格式不合法"),
    ERROR_NO_LOGIN("0004", "未登陆");

	public String code;

	public String msg;

	RtCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
