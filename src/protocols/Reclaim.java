package protocols;

import utils.Utils;

public class Reclaim {

	public Reclaim(int indexChosed, Peer peer) {

		peer.messageHandler.buildMessage("REMOVED", Float.toString(peer.getProtocolVersion()), peer.getPeerId(),
				Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getFileId()),
				Utils.convertBytetoInt(peer.chunks.selectChunk(indexChosed).getChunkNo()), 0, "");

		peer.chunks.removeAll(peer.chunks.getAdrresforSelection(indexChosed), Utils.convertBytetoString(peer.chunks.selectChunk(indexChosed).getFileId()));
		System.out.println("> Reclaimed space from your local storage!");

	}
}
