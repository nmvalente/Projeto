package protocols;

import utils.Utils;

public class Reclaim {

	public Reclaim(int indexChosed, Peer peer) {

		peer.inbox.buildMessage("REMOVED", "1.0", Utils.convertBytetoInt(peer.chunks.selectChunk(indexChosed).getSenderId()),
				Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getFileId()),
				Utils.convertBytetoInt(peer.chunks.selectChunk(indexChosed).getChunkNo()), 0, "");

		peer.chunks.removeAll(peer.chunks.getAdrresforSelection(indexChosed), Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getFileId()));

		System.out.println("> Reclaimed space!");
	}
}
