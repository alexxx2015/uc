using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;
using pdp;

namespace Pep2Pdp
{
    class Pep2PdpImpl : IPep2Pdp
    {
        private string address;
        private int port;
        private Socket senderSock;
        private NetworkStream networkStream;

        private enum EPep2PdpMethod { NotifyEvent = 1, UpdateIfFlowSemantics = 2}

        public Pep2PdpImpl(string address, int port)
        {
            this.address = address;
            this.port = port;
        }

        public GpResponse NotifyEvent(GpEvent e)
        {
            // Write one byte (value 1) which denotes method NotifyEvent (see PDP implementation)
            networkStream.WriteByte((byte)EPep2PdpMethod.NotifyEvent);
            e.WriteDelimitedTo(networkStream);
            //networkStream.Flush();

            GpResponse gpResponse = GpResponse.ParseDelimitedFrom(networkStream);

            return gpResponse;
        }

        public void Connect()
        {

            // Create one SocketPermission for socket access restrictions 
            SocketPermission permission = new SocketPermission(
                NetworkAccess.Connect,    // Connection permission 
                TransportType.Tcp,        // Defines transport types 
                "",                       // Gets the IP addresses 
                SocketPermission.AllPorts // All ports 
                );

            // Ensures the code to have permission to access a Socket 
            permission.Demand();

            IPAddress ipAddr = IPAddress.Parse(this.address);

            // Creates a network endpoint 
            IPEndPoint ipEndPoint = new IPEndPoint(ipAddr, this.port);

            // Create one Socket object to setup Tcp connection 
            senderSock = new Socket(
                ipAddr.AddressFamily,// Specifies the addressing scheme 
                SocketType.Stream,   // The type of socket  
                ProtocolType.Tcp     // Specifies the protocols  
                );

            senderSock.NoDelay = false;   // Using the Nagle algorithm 

            // Establishes a connection to a remote host 
            senderSock.Connect(ipEndPoint);
            Console.WriteLine("Socket connected to " + senderSock.RemoteEndPoint.ToString());

            networkStream = new NetworkStream(this.senderSock);

        }

        public void Disconnect()
        {
            try
            {
                // Disables sends and receives on a Socket. 
                senderSock.Shutdown(SocketShutdown.Both);

                //Closes the Socket connection and releases all resources 
                senderSock.Close();
            }
            catch (Exception exc)
            {
                Console.WriteLine("Error when disconnecting:" + exc.ToString());
            }
        }

    }
}
