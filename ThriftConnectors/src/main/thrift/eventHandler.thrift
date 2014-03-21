namespace cpp de.tum.i22.in.uc.cm.thrift
namespace csharp de.tum.i22.in.uc.cm.thrift
namespace java de.tum.i22.in.uc.cm.thrift

typedef i64 long
typedef i32 int

enum StatusType {
	OK = 1,
	ERROR = 2,
	ALLOW = 3,
	INHIBIT = 4,
	MODIFY = 5
}

enum DetectionMode {
	MISUSE = 1,
	ANOMALY = 2,
	COMBINED = 3
}

struct Event {
	1: string name,
	2: map<string,string> parameters,
	3: long timeStamp,
	4: optional string comment
}

struct Response {
	1: StatusType status,
	2: optional list<Event> executeEvents,
	3: optional Event modifiedEvents,
	4: optional string comment
}

service GenericThriftConnector {

	oneway void processEventAsync(1: Event e),

	Response processEventSync(1: Event e)

}

service ExtendedThriftConnector extends GenericThriftConnector {
	
	oneway void processEventAsync(1: Event e, 2: string senderID),

	Response processEventSync(1: Event e, 2: string senderID)
	
}


service TAny2Pdp  {
	
	oneway void processEventAsync(1: Event e, 2: string senderID),

	Response processEventSync(1: Event e, 2: string senderID)
	
}


service MWThriftConnector {

	oneway void dumpGraph(1: string graphName),
	
	oneway void setDetectionMode(1: DetectionMode mode),
	
	string getGraphInfo(1: string graphName),

	string getFullGraph(1: string graphName),
	
	string getPartialGraph(1: string graphName, 2: long startTime, 3: long endTime)
	
}

