package cn.wcl.test.netty.constants;

public interface UploadProtocalConstant {

	enum DataType {
		INT, STR;
	}

	enum Position {
		LAST("L"), FRONT("F");
		public String pos;

		Position(String pos) {
			this.pos = pos;
		}

		public String getPos() {
			return pos;
		}

	}

	public enum Header {
		FILE_NAME(4, DataType.INT), DATA(4, DataType.INT), DATA_TYPE(3,
				DataType.STR);

		private int len;

		private DataType type;

		Header(int len, DataType type) {
			this.len = len;
			this.type = type;
		}

		public int getLen() {
			return len;
		}

		public DataType getType() {
			return type;
		}
	}

	public enum Data {

		MD5(32);

		private int len;

		Data(int len) {
			this.len = len;
		}

		public int getLen() {
			return len;
		}

	}
}
