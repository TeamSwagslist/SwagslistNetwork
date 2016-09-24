package com.aidancbrady.swagslist.network;

import java.net.Socket;

public class ConnectionHandler extends Thread
{
	@Override
	public void run()
	{
		try {
			while(ServerCore.instance().isRunning())
			{
				Socket connection = ServerCore.instance().getSocket().accept();
				
				if(ServerCore.instance().isRunning())
				{
					System.out.println("Initiating connection with " + connection.getInetAddress());
					
					new Connection(connection).start();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
