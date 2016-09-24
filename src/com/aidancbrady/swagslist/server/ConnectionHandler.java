package com.aidancbrady.swagslist.server;

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
					
					new ActiveConnection(connection).start();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
