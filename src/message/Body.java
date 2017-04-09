package message;

public class Body{
	
	protected byte[] body;
	protected Body(){}

	protected Body(String body){this.body = body.getBytes();}

	protected Body(String[] body, int start, int end){
		String auxbody = "";
		for (int i = start; i < end; i++)
			auxbody += body;
		
		this.body = auxbody.getBytes();
	}

	public byte[] getBody(){return this.body;}
}
