package message;
public class Body
{
	private String msg; // char

	public Body()
	{
		msg = "";
	}

	public Body(String content)
	{
		msg = content;
	}

	public Body(String[] content, int start, int end)
	{
		for (int i = start; i < end; i++)
		{
			msg += content;
		}
	}

	public String getMsg()
	{
		return msg;
	}

	@Override
	public String toString()
	{
		return "Body{" +
				"msg='" + msg + '\'' +
				'}';
	}
}
