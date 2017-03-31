package message;
public class Body
{
	private String message;

	public Body(){
		message = "";
	}

	public Body(String message){
		this.message = message;
	}

	public Body(String[] message, int start, int end){
		for (int i = start; i < end; i++)
			this.message += message;
	}

	public String getMessage(){
		return this.message;
	}

	//@Override
//	public String toString(){return "Body{" + "message='" + this.message + '\'' + '}';}
}
