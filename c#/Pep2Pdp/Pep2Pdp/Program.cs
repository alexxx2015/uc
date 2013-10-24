using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using pdp;

namespace Pep2Pdp
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello world");
            GpEvent.Builder gpEvent = GpEvent.CreateBuilder();
            gpEvent.SetIsActual(false);
            gpEvent.SetName("print");
            GpEvent.Types.GpMapEntry.Builder gpMapEntry1 = GpEvent.Types.GpMapEntry.CreateBuilder();
            gpMapEntry1.Key = "key1";
            gpMapEntry1.Value = "val1";
            GpEvent.Types.GpMapEntry.Builder gpMapEntry2 = GpEvent.Types.GpMapEntry.CreateBuilder();
            gpMapEntry2.Key = "key2";
            gpMapEntry2.Value = "val2";

            gpEvent.AddMapEntry(gpMapEntry1.Build());
            gpEvent.AddMapEntry(gpMapEntry2.Build());


            IPep2Pdp pdpProxy = new Pep2PdpImpl("127.0.0.1", 50001);

            try
            {
                pdpProxy.Connect();
            }
            catch (Exception e)
            {
                Console.WriteLine("Failed to connect to PDP: " + e.ToString());
                return;
            }

            GpResponse gpResponse = pdpProxy.NotifyEvent(gpEvent.Build());
            Console.WriteLine("Received response: " + gpResponse.ToString());


            pdpProxy.Disconnect();
        }
    }
}
