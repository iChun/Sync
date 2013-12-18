package sync.common.network;

import java.net.SocketAddress;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;

public class FakeNetworkManager implements INetworkManager {

	@Override
	public void setNetHandler(NetHandler nethandler){}
	@Override
	public void addToSendQueue(Packet packet) {}
	@Override
	public void wakeThreads() {}
	@Override
	public void processReadPackets() {}
	@Override
	public SocketAddress getSocketAddress() { return null; }
	@Override
	public void serverShutdown() {}
	@Override
	public int packetSize() { return 0; }
	@Override
	public void networkShutdown(String s, Object... var2) {}
	@Override
	public void closeConnections() {}

}
