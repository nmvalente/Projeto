package message;

public class Body{
	
	protected String message;

	protected Body(){message = "";}

	protected Body(String message){this.message = message;}

	protected Body(String[] message, int start, int end){
		for (int i = start; i < end; i++)
			this.message += message;
	}

	public String getMessage(){return this.message;}
}
