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

enum TAttributeName {
	WILDCARD = 1,
	TYPE = 2,
	OWNER = 3,
	CLASS = 4,
	CREATION_TIME = 5,
	MODIFICATION_TIME = 6,
	SIZE = 7
}

struct TAttribute {
	1: required TAttributeName name,
	2: required string value
}

struct TEvent {
	1: required string name,
	2: map<string,string> parameters,
	3: long timeStamp,
	4: optional bool isActual = false
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
	1: required string id,
	2: required list<TAttribute> attributes
}

struct TData {
	1: required string id
}

struct TName {
	1: required string name
}

struct TXmlPolicy {
	1: required string name,
	2: required string xml
}

/*
* Tobias old stuff
* (required for WinPep communication)
*/
enum TobiasStatusType {
	OK = 1,
	ERROR = 2,
	ALLOW = 3,
	INHIBIT = 4,
	MODIFY = 5
}

struct TobiasEvent {
	1: string name,
	2: map<string,string> parameters,
	3: long timeStamp,
	4: optional string comment
}

struct TobiasResponse {
	1: TobiasStatusType status,
	2: optional list<TobiasEvent> executeEvents,
	3: optional TobiasEvent modifiedEvents,
	4: optional string comment
}







