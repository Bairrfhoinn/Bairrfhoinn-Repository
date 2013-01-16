package bairrfhoinn;

public class Message {
	String comment;
	byte[] data;
	int index;
	
	public Message(){
		this.data = new byte[0];
		this.index = 0;
		this.comment = "";
	}
	
	public Message(byte[] data, int index, String comment){
		this.data = data;
		this.index = index;
		this.comment = comment;
	}
	
	@Override
	public String toString(){
		return "index=" + this.getIndex() + " comment=[" + this.getComment() + "] data=[" + print(this.getData()) + "]";
	}
	
	private static String print(byte[] message) {
		StringBuffer sb = new StringBuffer();
		int length = message.length;
		for(int i=0; i<length; i++){
			sb.append(message[i] + " ");
		}
		return sb.toString();
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}	
}