using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using pdp;

namespace Pep2Pdp
{
    interface IPep2Pdp
    {
        GpResponse NotifyEvent(GpEvent e);
        void Connect();
        void Disconnect();
    }
}
