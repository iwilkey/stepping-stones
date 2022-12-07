package dev.iwilkey.terrafort.networking.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.entity.creature.AbstractPlayer;
import dev.iwilkey.terrafort.networking.Packets;
import dev.iwilkey.terrafort.networking.Packets.ByteInfo;
import dev.iwilkey.terrafort.scene.MultiplayerClientWorldScene;

public class Client {
	
	// Server-Client
	final int OBJECT_BUFFER_SIZE;
	
	private InetAddress SERVER_IP;
	private int SERVER_PORT;
	
	private ArrayList<DatagramPacket> incomingPacketQueue,
		outgoingPacketQueue;
	private DatagramPacket currentPacketProcessing,
		incomingPacket,
		outgoingPacket;
	
	private DatagramSocket getSocket,
		postSocket;
	boolean running = false;
	@SuppressWarnings("unused")
	private byte[] buffer;
	
	private Thread listener,
		processor,
		sender;
	
	// Game
	final MultiplayerClientWorldScene scene;
	
	public Client(final MultiplayerClientWorldScene s, String SERVERIP, int SERVERPORT, final int BUFFER_SIZE) {
		this.scene = s;
		try {
			getSocket = new DatagramSocket();
			postSocket = new DatagramSocket();
			this.SERVER_IP = InetAddress.getByName(SERVERIP);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		OBJECT_BUFFER_SIZE = BUFFER_SIZE;
		this.SERVER_PORT = SERVERPORT;
		running = true;
		incomingPacketQueue = new ArrayList<>();
		outgoingPacketQueue = new ArrayList<>();
		
		final byte[] clearBuffer = new byte[BUFFER_SIZE];
		final DatagramPacket clearIncomingPacket = new DatagramPacket(clearBuffer, clearBuffer.length);
		listener = new Thread("Client Packet Listener") {
			public void run() {
				while(running) {
					buffer = clearBuffer;
					incomingPacket = clearIncomingPacket;
					try {
						getSocket.receive(incomingPacket);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(-1);
					}
					
					// If the packet came from the server
					if(incomingPacket.getAddress().equals(SERVER_IP) &&
							incomingPacket.getPort() == (SERVER_PORT + 1)) {
						synchronized(incomingPacketQueue) {
							incomingPacketQueue.add(incomingPacket);

						}
					}
					
					try {
						System.gc();
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		processor = new Thread("Client Packet Processor") {
			public void run() {
				while(running) {
					synchronized(incomingPacketQueue) {
						while(incomingPacketQueue.size() - 1 >= 0) {
							currentPacketProcessing = incomingPacketQueue.get(0);
							
							try {
								
								Packets.Packet data = SerializationUtils.deserialize(currentPacketProcessing.getData());
							
								
								// Initial connection
								if(data instanceof Packets.JoinRequestAnswer) {
									Packets.JoinRequestAnswer answer = (Packets.JoinRequestAnswer)data;
									if(answer.accepted) {
										AbstractPlayer p = answer.player;
										int worldSize = answer.worldSize;
										scene.connection(p, worldSize);
										p = null;
									}
									answer = null;
								}
								
								// Chunk Update (Right after connection)
								if(data instanceof Packets.ServerChunkUpdate) {
									Packets.ServerChunkUpdate update = (Packets.ServerChunkUpdate)data;
									alignChunksInWorld(update);
									update = null;
								}
								
								if(data instanceof Packets.ServerUpdate) {
									Packets.ServerUpdate update = (Packets.ServerUpdate)data;
									updateClient(update);
									update = null;
								}
							
							} catch (Exception e) {
								incomingPacketQueue.remove(0);
								currentPacketProcessing = null;
								data = null;
								continue;
							}
							
							incomingPacketQueue.remove(0);
							currentPacketProcessing = null;
							data = null;
						}
						
						incomingPacketQueue.clear();
					}
					
					try {
						System.gc();
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		sender = new Thread("Client Packet Sender") {
			public void run() {
				while(running) {
					synchronized(outgoingPacketQueue) {
						while(outgoingPacketQueue.size() - 1 >= 0) {
							outgoingPacket = outgoingPacketQueue.get(0);
							
							try {
								postSocket.send(outgoingPacket);
							} catch (IOException e) {
								e.printStackTrace();
								System.exit(-1);
							}
							
							outgoingPacketQueue.remove(0);
							outgoingPacket = null;
						}
						
						outgoingPacketQueue.clear();
					}
					
					try {
						System.gc();
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		listener.start();
		processor.start();
		sender.start();
		
		sendConnectRequest("Debug Player " + MathUtils.random(0, 102043));
		
	}
	
	byte[] data = null;
	DatagramPacket packet = null;
	private synchronized boolean send(Packets.Packet p) {
		data = SerializationUtils.serialize(p);
		if(data.length > OBJECT_BUFFER_SIZE) {
			log("[BUFFER OVERFLOW] " + Integer.toString((data.length - OBJECT_BUFFER_SIZE)) + " bytes needed!\nClass: "
					+ p.getClass().getName() + "\nIncrease the buffer size to send this object.");
			return false;
		}
		if(packet == null) packet = new DatagramPacket(data, data.length, SERVER_IP, SERVER_PORT);
		else {
			packet.setData(data);
			packet.setLength(data.length);
		}
		data = null;
		outgoingPacketQueue.add(packet);
		System.gc();
		return true;
	}
	
	ByteInfo info = null;
	int xx = 0, yy = 0;
	private synchronized void alignChunksInWorld(final Packets.ServerChunkUpdate update) {

		new Thread() {
			@SuppressWarnings("deprecation")
			public void run() {
				xx = 0; yy = 0;
				for(int x = update.tileUpdate.xStart; x < update.tileUpdate.xEnd; x++) {
					yy = 0;
					for(int y = update.tileUpdate.yStart; y < update.tileUpdate.yEnd; y++) {
						if(info == null) info = new ByteInfo(x, y, update.tileUpdate.data[xx][yy]);
						else {
							info.x = x; info.y = y;
							info.data = update.tileUpdate.data[xx][yy];
						}
						scene.setTile(info);
						yy++;
					}
					xx++;
				}
				update.tileUpdate = null;
				info = null;
			
				scene.update();
				stop();
			}
		}.start();
		
		System.gc();
	}
	
	private synchronized void updateClient(Packets.ServerUpdate update) {
		if(update.playerUpdate) scene.setPlayer(update.newPlayer);
		if(update.tileUpdate) scene.setTile(update.newTile);
		scene.update();
	}
	
	private synchronized void sendConnectRequest(String username) {
		Packets.JoinRequest request = new Packets.JoinRequest();
		request.username = username;
		request.getSocketPort = getSocket.getLocalPort();
		Color color = new Color(MathUtils.random(0, 255) / 255f, MathUtils.random(0, 255) / 255f,
				MathUtils.random(0, 255) / 255f, 1.0f);
		float[] c = new float[4];
		c[0] = color.r; c[1] = color.g; c[2] = color.b; c[3] = color.a;
		request.color = c;
		send(request);
		
		request = null; c = null; color = null;
		System.gc();
	}
	
	Packets.ClientUpdate update = new Packets.ClientUpdate();
	public synchronized void sendPlayerUpdate() {
		scene.abstractPlayer.x = scene.world.mainPlayer.position.x;
		scene.abstractPlayer.y = scene.world.mainPlayer.position.y;
		scene.abstractPlayer.facingLeft = scene.world.mainPlayer.lookingLeft;
		update.playerUpdate = true; update.tileUpdate = false;
		update.newPlayer = scene.abstractPlayer; update.newTile = null;
		send(update);
	}
	
	private void log(String message) {
		System.out.println("[CLIENT] >> " + message);
	}
	
}
