package dev.iwilkey.terrafort.networking;

import java.io.Serializable;

import dev.iwilkey.terrafort.entity.creature.AbstractPlayer;

public class Packets {
	
	// Packet base class. All packets must extend this class.
	public static class Packet implements Serializable {
		private static final long serialVersionUID = 1L;
	}
	
	// Sent by a client to join a server.
	public static class JoinRequest extends Packet {
		private static final long serialVersionUID = 1L;
		public String username;
		public int getSocketPort;
		public float[] color;
	}
	
	// Sent by a server after a client has requested to join the server.
	public static class JoinRequestAnswer extends Packet {
		private static final long serialVersionUID = 1L;
		public boolean accepted = false;
		public AbstractPlayer player;
		public int worldSize;
	}
	
	public static class ClientUpdate extends Packet {
		private static final long serialVersionUID = 1L;
		public boolean tileUpdate = false,
			playerUpdate = false;
		public AbstractPlayer newPlayer = null;
		public ByteInfo newTile = null;
	}
	
	// Simple text message sent to the client from the server.
	public static class ServerMessage extends Packet {
		private static final long serialVersionUID = 1L;
		public String message;
	}
	
	public static class ByteInfo extends Packet {
		private static final long serialVersionUID = 1L;
		public int x, y;
		public byte data;
		public ByteInfo(int x, int y, byte data) {
			this.x = x; this.y = y;
			this.data = data;
		}
	}
	
	// This will be sent to the client after verifying with the server. It is part of
	// the ServerChunkUpdate, which just sends the chunks in bulk.
	public static class ByteInfoChunk extends Packet {
		private static final long serialVersionUID = 1L;
		public int xStart, xEnd, yStart, yEnd;
		public byte[][] data;
		public ByteInfoChunk(byte[][] input, int xStart, int xEnd, int yStart, int yEnd) {
			this.xStart = xStart; this.xEnd = xEnd;
			this.yStart = yStart; this.yEnd = yEnd;
			data = new byte[xEnd - xStart][yEnd - yStart];
			
			int xx = 0, yy = 0;
			for(int x = xStart; x < xEnd; x++) {
				yy = 0;
				for(int y = yStart; y < yEnd; y++) {
					data[xx][yy] = input[x][y];
					yy++;
				}
				xx++;
			}
		}
	}
	
	// Send the world chunk in bulk.
	public static class ServerChunkUpdate extends Packet {
		private static final long serialVersionUID = 1L;
		public ByteInfoChunk tileUpdate;
		public ServerChunkUpdate(ByteInfoChunk tile) {
			this.tileUpdate = tile;
		}
	}
	
	public static class ServerUpdate extends Packet {
		private static final long serialVersionUID = 1L;
		public boolean tileUpdate = false,
				playerUpdate = false;
			public AbstractPlayer newPlayer = null;
			public ByteInfo newTile = null;
	}

}
