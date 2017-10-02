/**
 * 
 */
package comp6231.a1.campus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;


/**
 * @author saman
 *
 */
public class UdpServer extends Thread {
	private Campus campus;
	private DatagramSocket socket;	//https://stackoverflow.com/questions/6265731/do-java-sockets-support-full-duplex
	private Object write_socket_lock = new Object();
	public final static int datagram_send_size = 256;
	
	public UdpServer(Campus campus) throws SocketException, RemoteException
	{
		this.campus = campus;
		socket = new DatagramSocket(this.campus.getPort());
	}
	
	private void processRequest(byte[] message, InetAddress address, int port)
	{
		System.out.println("Received message from " + address + ":" + port);
		MessageProtocol protocol = new MessageProtocol();
		MessageProtocol.MessageType type = protocol.decodeMessage(message);
		switch(type)
		{
		case Book_Room:
			try {
				String booking_id = campus.bookRoom(protocol.getUserId(), campus.getName(), protocol.getRoomNumer(), protocol.getDate(), protocol.getTimeSlot());
				byte[] reply = MessageProtocol.encodeBookRoomResponseMessage(protocol.getMessageId(), booking_id);
				sendDatagram(reply, address, port);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case Book_Room_Response:
			System.out.println("Booked_id received: " + protocol.getBookingId());
			break;
		}
	}
	
	public void sendDatagram(byte[] message, InetAddress address, int port) throws IOException
	{
		System.out.println("Send message to " + address + ":" + port);
		DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
		synchronized (write_socket_lock) {
			socket.send(packet);
		}		
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			byte[] buffer = new byte[datagram_send_size];
			DatagramPacket packet = new DatagramPacket(buffer, datagram_send_size);
			try {
				socket.receive(packet);
				
				processRequest(packet.getData(), packet.getAddress(), packet.getPort());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
