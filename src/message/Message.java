package message;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Message
{
	public static final String CRLF2 = "\r\n\r\n";
	public static final int SHA256LENGTH = 64;

	private String address = "";
	private int port = 0;
	private boolean request = false;

	public Head head;
	public Body body;

	public Message(String address, int port, String s)
	{
		if (!(isAddress(address) && isPort(port)))
			throw new IllegalArgumentException("Invalid Address and/or Port");

		this.address = address;
		this.port = port;

		String[] tokens = s.split("\\s");

		if (tokens.length < 4)
			throw new IllegalArgumentException("Invalid Message Size");

		switch (tokens[0])
		{
			case "PUTCHUNK":
				if (!isPUTCHUNK(tokens))
					throw new IllegalArgumentException("Invalid Message PUTCHUNK");

				head = new Head(
						tokens[0],
						tokens[1],
						tokens[2],
						Integer.parseInt(tokens[3]),
						Integer.parseInt(tokens[4])
				);

				body = new Body(tokens,5,tokens.length);
				break;

			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
				if (!isSTOREDorGETCHUNKorREMOVED(tokens))
					throw new IllegalArgumentException("Invalid Message STORED | GETCHUNK | REMOVED");

				head = new Head(
						tokens[0],
						tokens[1],
						tokens[2],
						Integer.parseInt(tokens[3])
				);
				body = new Body();
				break;

			case "CHUNK":
				if (!isCHUNK(tokens))
					throw new IllegalArgumentException("Invalid Message CHUNK");

				head = new Head(
						tokens[0],
						tokens[1],
						tokens[2],
						Integer.parseInt(tokens[3])
				);
				body = new Body(tokens,4,tokens.length);
				break;

			case "DELETE":
				if (!isDELETE(tokens))
					throw new IllegalArgumentException("Invalid Message DELETE");

				head = new Head(
						tokens[0],
						tokens[1],
						tokens[2]
				);
				body = new Body();
				break;
		}

	}
	
	public Message(String msgtype, String version, String fileId, int chunkNo, int repl, String msg)
	{
		try
		{
			request = true;
			head = new Head(msgtype,version,fileId,chunkNo,repl);
			body = new Body(msg);
		}
		catch (IllegalArgumentException e)
		{
			e.getMessage();
		}
	}

	public String getAddress() { return address; }

	public int getPort() { return port; }

	public boolean isRequest() { return request; }

	@Override
	public String toString()
	{
		return "Message{\n" +
				head.toString() + '\n' +
				body.toString() + '\n' +
				'}';
	}

	public String simple()
	{
		return 	head.simple() ;
	}

	private boolean isAddress(String ip)
	{
		Pattern pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	private boolean isPort(int p)
	{
		return (p>=1 && p<=65535);
	}

	private boolean isPUTCHUNK(String[] tokens)
	{
		
		if (tokens.length >= 6)
		{
			if (
					tokens[2].length() == SHA256LENGTH &&
					isNumber(tokens[3]) &&
					isNumber(tokens[4])
					//&& (tokens[5].length() >= CRLF2.length() && tokens[5].substring(0,CRLF2.length()).equals(CRLF2))
				)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isSTOREDorGETCHUNKorREMOVED(String[] tokens)
	{
		if (tokens.length >= 5)
		{
			if (
					tokens[2].length() == SHA256LENGTH &&
					isNumber(tokens[3])
					// && (tokens[4].length() == CRLF2.length() && tokens[4].substring(0,CRLF2.length()).equals(CRLF2))
				)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isCHUNK(String[] tokens)
	{
		if (tokens.length >= 5)
		{
			if (
					tokens[2].length() == SHA256LENGTH &&
							isNumber(tokens[3])
							// && (tokens[4].length() >= CRLF2.length() && tokens[4].substring(0,CRLF2.length()).equals(CRLF2))
				)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isDELETE(String[] tokens)
	{
		if (tokens.length >= 4)
		{
			if (
					tokens[2].length() == SHA256LENGTH
					// && (tokens[3].length() >= CRLF2.length() && tokens[3].substring(0,CRLF2.length()).equals(CRLF2))
				)
			{
				return true;
			}
		}

		return false;
	}

	public boolean isNumber(String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch(NumberFormatException e)
		{
			return false;
		}

		return true;
	}

	public String build()
	{
		String ss = null;

		switch (head.getMessageType())
		{
			case "PUTCHUNK":
				ss = head.getMessageType() + " " + head.getVersion() + " " + head.getFileId() + " " + head.getChunkNo() + " " + head.getReplicationDeg() + " " + CRLF2 + body.getMsg();
				break;

			case "STORED":
			case "GETCHUNK":
			case "REMOVED":
				ss = head.getMessageType() + " " + head.getVersion() + " " + head.getFileId() + " " + head.getChunkNo() + " " + CRLF2;
				break;

			case "CHUNK":
				ss = head.getMessageType() + " " + head.getVersion() + " " + head.getFileId() + " " + head.getChunkNo() + " " + CRLF2 + body.getMsg();
				break;

			case "DELETE":
				ss = head.getMessageType() + " " + head.getVersion() + " " + head.getFileId() + " " + CRLF2;
				break;
		}

		return ss;
	}

	public String reply()
	{
		String ss = null;

		switch (head.getMessageType())
		{
			case "PUTCHUNK":
				ss = "STORED" + " " + head.getVersion() + " " + head.getFileId() + " " + head.getChunkNo() + " " + CRLF2;
				break;

			case "GETCHUNK":
				ss = "CHUNK" + " " + head.getVersion() + " " + head.getFileId() + " " + head.getChunkNo() + " " + CRLF2;
				break;
		}

		return ss;
	}

}

