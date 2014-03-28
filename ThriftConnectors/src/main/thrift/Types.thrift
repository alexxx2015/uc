namespace cpp de.tum.i22.in.uc.cm.thrift
namespace csharp de.tum.i22.in.uc.cm.thrift
namespace java de.tum.i22.in.uc.cm.thrift
/*
 * Typedef for platform-dependent definitions.
 *
 * Change the values for the platform and language you are compiling for
 */

typedef i64 long
typedef i32 int

enum StatusType {
	OKAY = 1,
	ERROR = 2,
	ALLOW = 3,
	INHIBIT = 4,
	MODIFY = 5,
	ERROR_EVENT_PARAMETER_MISSING = 6
}

enum DetectionMode {
	MISUSE = 1,
	ANOMALY = 2,
	COMBINED = 3
}

enum ConflictResolution {
	OVERWRITE = 1,
	IGNORE_UPDATES = 2, //currently not used
	KEEP_ALL = 3  // currently not used
}

struct Event {
	1: required string name,
	2: map<string,string> parameters,
	3: long timeStamp,
	4: bool isActual
	5: optional string comment	
}

struct Response {
	1: StatusType status,
	2: optional list<Event> executeEvents,
	3: optional Event modifiedEvents,
	4: optional string comment
}

struct Pxp{
	1: string ip,
	2: int port,
	3: string description,
	4: string id
}

struct Container {
	1: string classValue,
	2: required string id
}

struct Data {
	1: required string id
}

struct Name {
	1: required string name
}
