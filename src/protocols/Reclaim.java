package protocols;

import utils.Utils;

public class Reclaim {

	public Reclaim(int indexChosed, Peer peer) {

		peer.inbox.buildMessage("REMOVED", "1.0", Utils.convertBytetoInt(peer.chunks.selectChunk(indexChosed).getSenderId()),
				Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getFileId()),
				Utils.convertBytetoInt(peer.chunks.selectChunk(indexChosed).getChunkNo()), 0, "");

		////////////////////////////////////////////////////
		
		if(! String.valueOf(peer.getPeerId()).equals(Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getSenderId()))){
			System.out.println("PEERID = " + String.valueOf(peer.getPeerId()));
			System.out.println("SENDERID = " + Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getSenderId()));
			peer.chunks.removeAll(peer.chunks.getAdrresforSelection(indexChosed), Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getFileId()));
			System.out.println("> Reclaimed space because peer have the local copy!");
		}
		else System.out.println("> Sender different of peer!");
	}
}
