namespace cpp de.tum.in.i22.uc.thrift.types
namespace csharp de.tum.in.i22.uc.thrift.types
namespace java de.tum.in.i22.uc.thrift.types
/*
 * Typedef for platform-dependent definitions.
 * 
 * Change the values for the platform and language you are compiling for
 */

typedef i64 long
typedef i32 int

enum TStatus {
	OKAY = 1,
	ERROR = 2,
	ALLOW = 3,
	INHIBIT = 4,
	MODIFY = 5,
	ERROR_EVENT_PARAMETER_MISSING = 6
}

enum TDetectionMode {
	MISUSE = 1,
	ANOMALY = 2,
	COMBINED = 3
}

enum TConflictResolution {
	OVERWRITE = 1,
	IGNORE_UPDATES = 2, //currently not used
	KEEP_ALL = 3  // currently not used
}

struct TEvent {
	1: required string name,
	2: map<string,string> parameters,
	3: long timeStamp,
	4: bool isActual
	5: optional string comment	
}

struct TResponse {
	1: TStatus status,
	2: optional list<TEvent> executeEvents,
	3: optional TEvent modifiedEvents,
	4: optional string comment
}

struct TPxpSpec{
	1: string ip,
	2: int port,
	3: string description,
	4: string id
}

struct TContainer {
	1: string classValue,
	2: required string id
}

struct TData {
	1: required string id
}

struct TName {
	1: required string name
}
