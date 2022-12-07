package dev.iwilkey.terrafort.networking.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.SerializationUtils;

import com.badlogic.gdx.math.MathUtils;

import dev.iwilkey.terrafort.entity.creature.AbstractPlayer;
import dev.iwilkey.terrafort.networking.Packets;
import dev.iwilkey.terrafort.tile.Tile;
import dev.iwilkey.terrafort.world.AbstractWorld;
import dev.iwilkey.terrafort.world.WorldGeneration;

public class Server {
	
	// Server-Client
	public static class Connection {
		public InetAddress address;
		public int postPort,
			getPort;
		public Connection(InetAddress address, int postPort, int getPort) {
			this.address = address;
			this.postPort = postPort;
			this.getPort = getPort;
		}
	}
	
	final int OBJECT_BUFFER_SIZE;
	
	private DatagramSocket postSocket,
		getSocket;
	private DatagramPacket currentPacketProcessing,
		incomingPacket,
		outgoingPacket;
	private InetAddress incomingPacketAddress;
	private int incomingPacketAddressPort;
	
	private ArrayList<Connection> connections;
	private ArrayList<DatagramPacket> incomingPacketQueue,
		outgoingPacketQueue;
	
	private boolean running = false;
	@SuppressWarnings("unused")
	private byte[] buffer;
	
	// Runtime diagnostics
	private int serverFaults = 0,
			packetsReceived = 0,
			lostReceivedPackets = 0,
			packetsSent = 0,
			lostSentPackets = 0;
	
	@SuppressWarnings("unused")
	private Thread listener,
		processor,
		sender,
		commandPrompt,
		worldShipper;
	
	// Game
	AbstractWorld serverWorld;
	
	public static void main(String[] args) {
		new Server(512, 8080, 1024 * 20);
	}
	
	private void createWorld(int worldSize) {
		serverWorld = new AbstractWorld(worldSize);
		serverWorld.TILES = 
				WorldGeneration.GenerateWorld(serverWorld.TILES, worldSize, worldSize, 
						MathUtils.random(1000000, 100000000));
	}
	
	public Server(final int WORLD_SIZE, int PORT, final int BUFFER_SIZE) {
		try {
			postSocket = new DatagramSocket(PORT + 1);
			getSocket = new DatagramSocket(PORT);
			log("Terrafort server running on port " + PORT);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		createWorld(WORLD_SIZE);
		
		OBJECT_BUFFER_SIZE = BUFFER_SIZE;
		running = true;
		buffer = new byte[BUFFER_SIZE];
		incomingPacketQueue = new ArrayList<>();
		outgoingPacketQueue = new ArrayList<>();
		connections = new ArrayList<>();
		currentPacketProcessing = null;
		outgoingPacket = null;
		incomingPacketAddress = null;
		
		final byte[] clearBuffer = new byte[BUFFER_SIZE];
		final DatagramPacket clearIncomingPacket = new DatagramPacket(clearBuffer, clearBuffer.length);
		listener = new Thread("Server Packet Listener") {
			public void run() {
				while(running) {
					try {
						buffer = clearBuffer;
						incomingPacket = clearIncomingPacket;
						try {
							getSocket.receive(incomingPacket);
						} catch (IOException e) {
							e.printStackTrace();
							lostReceivedPackets++;
							continue;
						}
						packetsReceived++;
						incomingPacketAddress = incomingPacket.getAddress();
						incomingPacketAddressPort = incomingPacket.getPort(); // post port
						
						// If a connection has already been established, then send it to the processor.
						boolean allowed = false;
						synchronized(connections) {
							for(Connection c : connections) {
								if(incomingPacketAddress.equals(c.address) && 
										incomingPacketAddressPort == c.postPort) {
									synchronized(incomingPacketQueue) {
										incomingPacketQueue.add(incomingPacket);
									}
									allowed = true;
									break;
								}
							}
						}
						
						// Otherwise, check if this packet is a enter request.
						if(!allowed) {
							Packets.Packet o = SerializationUtils.deserialize(incomingPacket.getData());
							// If a client wants to join the server
							if(o instanceof Packets.JoinRequest) {
								Packets.JoinRequest request = (Packets.JoinRequest)o;
								
								/*
								 * 
								 * Authentication goes here
								 * 
								 * 
								*/
								
								Packets.JoinRequestAnswer answer = new Packets.JoinRequestAnswer();
								
								// Build a welcome packet.
								answer.accepted = true;
								AbstractPlayer p = new AbstractPlayer(connections.size(), 
										request.username,
										request.color,
										(WORLD_SIZE / 2f) * Tile.TILE_SIZE,
										(WORLD_SIZE / 2f) * Tile.TILE_SIZE		
								);
								answer.player = p;
								answer.worldSize = WORLD_SIZE;
								
								// Send the requesting client their player
								System.out.println(request.getSocketPort);
								send(answer, incomingPacketAddress, request.getSocketPort);
								connections.add(new Connection(incomingPacketAddress, incomingPacketAddressPort, request.getSocketPort));
								
								try {
									System.gc();
									sleep(250);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								// Send all clients the new player
								Packets.ServerUpdate update = new Packets.ServerUpdate();
								update.playerUpdate = true; update.newPlayer = answer.player;
								synchronized(connections) {
									for(Connection c : connections)
										if(!(c.equals(connection(incomingPacketAddress, incomingPacketAddressPort)))) 
											send(update, c.address, c.getPort);
								}
								update = null;
								
								// Send this new connection the world (IN CHUNKS)
								sendClientWorldInChunks(8, incomingPacketAddress, request.getSocketPort);
								
								request = null; p = null; answer = null;
							}
							
							o = null;
						}
						
						try {
							System.gc();
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					
					} catch (Exception e) {
						e.printStackTrace();
						serverFaults++;
						continue;
					}
				}
			}
		};
		
		final Packets.ServerUpdate update = new Packets.ServerUpdate();
		processor = new Thread("Server Packet Processor") {
			public void run() {
				while(running) {
					try {
						synchronized(incomingPacketQueue) {
							while(incomingPacketQueue.size() - 1 >= 0) {
									currentPacketProcessing = incomingPacketQueue.get(0);
									Packets.Packet data = SerializationUtils.deserialize(currentPacketProcessing.getData());
									
									// Do stuff with the data
									if(data instanceof Packets.ClientUpdate) {
										Packets.ClientUpdate clientUpdate = (Packets.ClientUpdate)data;
										update.playerUpdate = clientUpdate.playerUpdate;
										update.tileUpdate = clientUpdate.tileUpdate;
										update.newPlayer = clientUpdate.newPlayer;
										update.newTile = clientUpdate.newTile;
										synchronized(connections) {
											for(Connection c : connections)
												if(!(c.equals(connection(currentPacketProcessing.getAddress(), 
														currentPacketProcessing.getPort()))))
													send(update, c.address, c.getPort);
										}
										clientUpdate = null;
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
					
					} catch (Exception e) {
						e.printStackTrace();
						serverFaults++;
						continue;
					}
				}
			}
		};
		
		sender = new Thread("Server Packet Sender") {
			public void run() {
				while(running) {
					try {
						synchronized(outgoingPacketQueue) {
							while(outgoingPacketQueue.size() - 1 >= 0) {
								outgoingPacket = outgoingPacketQueue.get(0);
								
								try {
									postSocket.send(outgoingPacket);
								} catch (IOException e) {
									e.printStackTrace();
									lostSentPackets++;
									continue;
								}
								
								packetsSent++;
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
					
					} catch (Exception e) {
						e.printStackTrace();
						serverFaults++;
						continue;
					}
				}
			}
		};
		
		commandPrompt = new Thread("Command Prompt") {
			public void run() {
				Scanner scanner = new Scanner(System.in);
				String command = "";
				run: while(running) {
					while(!scanner.hasNext()) {}
						command = scanner.next();

					switch(command) {
						case "close":
							running = false;
							break run;
						case "help":
							log("List of server commands\n"
									+ "	close - Closes the server.\n"
									+ "	new - Generates a new server world.\n"
									+ "	size - Logs the amount of players currently connected to the server.\n"
									+ "	memory - Logs useful information about the server's memory allocation.\n"
									+ "	status - Logs server");
							break;
						case "new":
							createWorld(512);
							synchronized(connections) {
								for(Connection c : connections) {
									sendClientWorldInChunks(8, c.address, c.getPort);
									try {
										sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								
							}
							break;
						case "size":
							log("" + connections.size());
							break;
						case "memory":
							log("Free JVM memory: " + Runtime.getRuntime().freeMemory() + "\n"
									+ "	Used JVM memory: " + Long.toString(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + "\n"
											+ "	Total JVM memory: " + Runtime.getRuntime().totalMemory());
						case "status":
							if(serverFaults <= 10) log("Server status: Excellent!");
							else if(serverFaults <= 50) log("Server status: Good!");
							else if(serverFaults <= 100) log("Server status: Ok.");
							else if(serverFaults <= 150) log("Server status: Not great.");
							else log("Server status: Terrible. Please contact Terrafort developers!");
							System.out.println("More info: \n"
									+ "	Packets received: " + packetsReceived + "\n"
											+ "	Lost received packets: " + lostReceivedPackets + "\n"
													+ "	Packets sent: " + packetsSent + "\n"
															+ " 	Lost sent packets: " + lostSentPackets + "\n"
																	+ "	Server faults: " + serverFaults + "\n\n"
																			+ "	[NOTE] Please consider posting these results in the Terrafort github issues tab.\n"
																			+ "		Thank you!");
							break;
						default:
							log("Command not understood! Type 'help' for a list of commands!");
					}
				}
				
				log("Terrafort server succesfully closed!");
				System.out.println("Server Lifetime Report (SLR): \n"
						+ "	Packets received: " + packetsReceived + "\n"
								+ "	Lost received packets: " + lostReceivedPackets + "\n"
										+ "	Packets sent: " + packetsSent + "\n"
												+ " 	Lost sent packets: " + lostSentPackets + "\n"
														+ "	Server faults: " + serverFaults + "\n\n"
																+ "	[NOTE] Please consider posting these results in the Terrafort github issues tab.\n"
																+ "		Thank you!");
				System.exit(0);
			}
		};
		
		listener.start();
		processor.start();
		sender.start();
		commandPrompt.start();
	}
	
	
	byte[] data = null;
	DatagramPacket packet = null;
	private synchronized boolean send(Packets.Packet p, InetAddress address, int port) {
		try {
			data = SerializationUtils.serialize(p);
			if(data.length > OBJECT_BUFFER_SIZE) {
				log("[BUFFER OVERFLOW] " + Integer.toString((data.length - OBJECT_BUFFER_SIZE)) + " bytes needed!\nClass: "
						+ p.getClass().getName() + "\nIncrease the buffer size to send this object.");
				return false;
			}
			if(packet == null) packet = new DatagramPacket(data, data.length, address, port);
			else {
				packet.setData(data);
				packet.setLength(data.length);
				packet.setAddress(address);
				packet.setPort(port);
			}
			data = null;
			synchronized(outgoingPacketQueue) {
				outgoingPacketQueue.add(packet);
			}
			System.gc();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			serverFaults++;
			return false;
		}
	}
	
	private synchronized void sendClientWorldInChunks(final int CHUNK_SIZE, final InetAddress address, final int port) {
		worldShipper = new Thread("Server World Shipper") {
			@SuppressWarnings("static-access")
			public void run() {
				while(true) {
					try {
						for(int chunkX = 0; chunkX < CHUNK_SIZE; chunkX++) {
							for(int chunkY = 0; chunkY < CHUNK_SIZE; chunkY++) {
								// Chunk coords
								int xStart = (serverWorld.SIZE / CHUNK_SIZE) * chunkX,
										xEnd = ((serverWorld.SIZE / CHUNK_SIZE) * chunkX) + (serverWorld.SIZE / CHUNK_SIZE),
										yStart = (serverWorld.SIZE / CHUNK_SIZE) * chunkY,
										yEnd = ((serverWorld.SIZE / CHUNK_SIZE) * chunkY) + (serverWorld.SIZE / CHUNK_SIZE);
								
								// Data chunks
								Packets.ByteInfoChunk tileChunkData = 
										new Packets.ByteInfoChunk(serverWorld.TILES, xStart, xEnd, yStart, yEnd);
								
								// Data chunks in bulk ready to be sent
								Packets.ServerChunkUpdate update = 
										new Packets.ServerChunkUpdate(tileChunkData);
								
								send(update, address, port);
								
								tileChunkData = null; update = null;
								try {
									System.gc();
									sleep(400);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						
						System.gc();
						break;
						
					} catch (Exception e) {
						e.printStackTrace();
						serverFaults++;
						continue;
					}
				}
			}
		};
		worldShipper.start();
	}
	
	private synchronized Connection connection(InetAddress address, int port) {
		for(Connection c : connections) 
			if(c.address.equals(address) && c.postPort == port) return c;
		return null;
	}
	
	private void log(String message) {
		System.out.println("[SERVER] >> " + message);
	}
	
}
