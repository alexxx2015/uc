Successfully created mutex [pefLogger]
[    threadUtils][DEBUG] Successfully created mutex [pdpMutex]
pdpConstructor successfully finished
[  testOperators][WARN ] Starting testpdp application for operator-tests
[    socketWin32][DEBUG] Winsock initialized!
[    socketWin32][INFO ] Successfully created win-TCP-socket for port=[9983]
[    socketUtils][INFO ] Opened TCP-socket (fd=[1848], port=[9983])
[    threadUtils][DEBUG] Created new thread with handler=[00417e72] => threadID=[0]
[    socketUtils][DEBUG] pefSocket successfully allocated for port=[9983] with handler=[0040746e]
[            pdp][DEBUG] PDP interfaces initialized.
[            pdp][INFO ] PDP initialized
[  testOperators][TRACE] PDP started with result=[SUCCESS]
[  testOperators][TRACE] PEP registration => [SUCCESS]
[            pdp][DEBUG] Found a registered PEP for this ID
[actionDescStore][TRACE] Action description [action1] not found, inserting description
[         action][TRACE] Created action description [action1]
[actionDescStore][TRACE] Action description [action1] not found, returning NULL
[actionDescStore][TRACE] Adding action description [action1] to store
[            pdp][DEBUG] ActionDescription [action1] successfully found/added to store
[            pdp][DEBUG] Action [action1] successfully associated to PEP [testpdp]
[  testOperators][TRACE] Action registration => [SUCCESS]
[            pdp][DEBUG] Found a registered PEP for this ID
[actionDescStore][TRACE] Action description [action2] not found, inserting description
[         action][TRACE] Created action description [action2]
[actionDescStore][TRACE] Action description [action2] not found, returning NULL
[actionDescStore][TRACE] Adding action description [action2] to store
[            pdp][DEBUG] ActionDescription [action2] successfully found/added to store
[            pdp][DEBUG] Action [action2] successfully associated to PEP [testpdp]
[  testOperators][TRACE] Action registration => [SUCCESS]
[  testOperators][TRACE] PXP registration => [SUCCESS]
[            pdp][DEBUG] Found a registered PXP for this ID
[actionDescStore][TRACE] Action description [notify] not found, inserting description
[         action][TRACE] Created action description [notify]
[actionDescStore][TRACE] Action description [notify] not found, returning NULL
[actionDescStore][TRACE] Adding action description [notify] to store
[            pdp][DEBUG] ActionDescription [notify] successfully found/added to store
[            pdp][DEBUG] Execution action [notify] successfully associated to PXP [testpdp]
[  testOperators][TRACE] Executor registration => [SUCCESS]
[            pdp][TRACE] pdpDeployPolicy - c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testSince.xml
[    pdpInternal][DEBUG] Loading mechanisms from file: c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testSince.xml (for mechanism=[NULL])
[       xmlUtils][INFO ] XML file [c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testSince.xml] is valid according to pef-schema
[actionDescStore][TRACE] Action description [event.homeapp.controller.backend] not found, inserting description
[         action][TRACE] Created action description [event.homeapp.controller.backend]
[actionDescStore][TRACE] Action description [event.homeapp.controller.backend] not found, returning NULL
[actionDescStore][TRACE] Adding action description [event.homeapp.controller.backend] to store
[    pdpInternal][DEBUG] ActionDescription [event.homeapp.controller.backend] successfully added to store
[    pdpInternal][TRACE] NO PEP interface specified for action [event.homeapp.controller.backend]...
[actionDescStore][TRACE] === Action description store:
[         action][TRACE] === Action description: [notify]
[         action][TRACE]                  Class: [usage]
[         action][TRACE] ================================
[         action][TRACE] === Action description: [event.homeapp.controller.backend]
[         action][TRACE]                  Class: [usage]
[         action][TRACE] ================================
[         action][TRACE] === Action description: [action1]
[         action][TRACE]                  Class: [usage]
[         action][TRACE] ================================
[         action][TRACE] === Action description: [action2]
[         action][TRACE]                  Class: [usage]
[         action][TRACE] ================================
[actionDescStore][TRACE] ================================
[    pdpInternal][DEBUG] Parsing actionDescriptions... [SUCCESS]
[      mechanism][TRACE] Mechanism Type: preventiveMechanism
[      mechanism][TRACE] MechanismFullName=[c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testSince.xml#testSince]
[    threadUtils][DEBUG] Successfully created mutex [testSince]
[      mechanism][TRACE] Successfully allocated mechanism "testSince" in namespace "c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testSince.xml" (@ 1338047318609375)
[      mechanism][TRACE] xmlParseTimestepSize - timestepSize: 3000000 us
[actionDescStore][TRACE] Action description [action1] found in store
[  pefEventMatch][INFO ] Creating event matching operator action=[action1] match_index=[ongoing] match_try=[true]
[         action][TRACE] Param description [val1] not found, returning NULL
[         action][TRACE] Successfully added parameter description [val1] of type [string] to action [action1]
[      paramDesc][TRACE] Param value [value1] not found, returning NULL
[      paramDesc][TRACE] Param value [value1] not found, returning NULL
[      paramDesc][TRACE] Adding value [value1] to parameter description [val1] of action [action1]
[  pefEventMatch][INFO ] no parameter type given in policy; defaulting to PARAM_SRING!
[  pefEventMatch][DEBUG] Adding param [val1] to eventMatch
[  pefEventMatch][TRACE] Adding parameter matching [val1][value1][string][false] to event
[  pefEventMatch][INFO ] Successfully parsed XML and created eventMatch operator referencing action "action1"
[  testOperators][INFO ] pepSubscribeNative invoked for name=[action1] and unsubscribe=[0]...
[      mechanism][DEBUG] PEP subscription for event name=[action1]=>[0]
[actionDescStore][TRACE] Action description [action1] found in store
[  pefEventMatch][INFO ] Creating event matching operator action=[action1] match_index=[ongoing] match_try=[true]
[         action][TRACE] Param description [val1] found
[      paramDesc][TRACE] Param value [value1] found
[  pefEventMatch][INFO ] no parameter type given in policy; defaulting to PARAM_SRING!
[  pefEventMatch][DEBUG] Adding param [val1] to eventMatch
[  pefEventMatch][TRACE] Adding parameter matching [val1][value1][string][false] to event
[  pefEventMatch][INFO ] Successfully parsed XML and created eventMatch operator referencing action "action1"
[  testOperators][INFO ] pepSubscribeNative invoked for name=[action1] and unsubscribe=[0]...
[      condition][DEBUG] PEP subscription for event name=[action1]=>[0]
[      condition][TRACE] Subscribing this eventMatch condition operand to the list of condition nodes
[   oslOperators][TRACE] Operator type: EVENTMATCH
[   oslOperators][TRACE]           op1: action1
[actionDescStore][TRACE] Action description [action2] found in store
[  pefEventMatch][INFO ] Creating event matching operator action=[action2] match_index=[ongoing] match_try=[true]
[         action][TRACE] Param description [val2] not found, returning NULL
[         action][TRACE] Successfully added parameter description [val2] of type [string] to action [action2]
[      paramDesc][TRACE] Param value [value2] not found, returning NULL
[      paramDesc][TRACE] Param value [value2] not found, returning NULL
[      paramDesc][TRACE] Adding value [value2] to parameter description [val2] of action [action2]
[  pefEventMatch][INFO ] no parameter type given in policy; defaulting to PARAM_SRING!
[  pefEventMatch][DEBUG] Adding param [val2] to eventMatch
[  pefEventMatch][TRACE] Adding parameter matching [val2][value2][string][false] to event
[  pefEventMatch][INFO ] Successfully parsed XML and created eventMatch operator referencing action "action2"
[  testOperators][INFO ] pepSubscribeNative invoked for name=[action2] and unsubscribe=[0]...
[      condition][DEBUG] PEP subscription for event name=[action2]=>[0]
[      condition][TRACE] Subscribing this eventMatch condition operand to the list of condition nodes
[   oslOperators][TRACE] Operator type: EVENTMATCH
[   oslOperators][TRACE]           op1: action2
[   oslOperators][TRACE] Operator type: SINCE
[   oslOperators][TRACE]           op1: EVENTMATCH
[   oslOperators][TRACE]           op2: EVENTMATCH
[      condition][DEBUG] Condition formula with 3 nodes
[      condition][TRACE]  SINCE
[      condition][TRACE]   EVENTMATCH [actionName=action2]
[      condition][TRACE]   EVENTMATCH [actionName=action1]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 0, delay: 0)
[      mechanism][TRACE] Successfully parsed mechanism testSince (size: 154 bytes)
[      mechanism][TRACE] Mechanism: 
[      mechanism][TRACE]          name: testSince
[      mechanism][TRACE]  timestepSize: 3000000
[      mechanism][TRACE]         start: 1338047318609375
[      mechanism][TRACE]   last update: 1338047318609375
[      mechanism][TRACE]  cur timestep: 0
[    pdpInternal][INFO ] Successfully added mechanism "testSince" to hashtable
[    pdpInternal][DEBUG] Starting update thread for mechanisms=[testSince] with usleep=[3000000]
[    threadUtils][DEBUG] Created new thread with handler=[0040c29c] => threadID=[1868770928]
[    threadUtils][DEBUG] Thread successfully started
[    pdpInternal][INFO ] Successfully loaded mechanisms from file: c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testSince.xml
[            pdp][TRACE] pdpDeployPolicy - loading policy returned [SUCCESS]
[  testOperators][TRACE] deploy returned=[0]
[      mechanism][INFO ] Started mechanism update thread usleep=300000 us
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 0. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] B did not happen this timestep and was not yet intercepted!
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[0] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 1. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] B did not happen this timestep and was not yet intercepted!
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[1] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (0): [<event isTry="true" action="action2" index="ALL"><parameter name="val2" value="value2"/></event>]
[actionDescStore][TRACE] Action description [action2] found in store
[       pefEvent][TRACE] Creating event action=[action2] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[0] [action2][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val2] found
[       pefParam][TRACE] Adding parameter [val2][value2][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action2"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action2]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action2] for levent=[action2]
[  pefEventMatch][DEBUG] Matching event [0]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action2]=[action2]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val2]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val2] found
[  pefParamMatch][DEBUG] Parameter [val2] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action2]; subscribed mechanisms=[0]
[       pefEvent][TRACE] Freeing event referencing action=[action2]
[       pefParam][TRACE] Freeing event parameter [val2]
[       pefParam][TRACE] Freeing parameter [val2][value2][type:0]
[       pefEvent][DEBUG] Event id=[0] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse/>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 2. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] first occurence of B (op2)
[        oslEval][TRACE] updating states...
[        oslEval][TRACE] found an event B (op2), setting wasEverTrue and deactivating immutability
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[2] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 3. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] DID NOT found event A (op1))
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[3] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 4. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[4] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 5. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[5] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 6. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[6] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 7. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[7] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 8. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[8] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (1): [<event isTry="true" action="action2" index="ALL"><parameter name="val2" value="value2"/></event>]
[actionDescStore][TRACE] Action description [action2] found in store
[       pefEvent][TRACE] Creating event action=[action2] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[1] [action2][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val2] found
[       pefParam][TRACE] Adding parameter [val2][value2][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action2"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action2]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action2] for levent=[action2]
[  pefEventMatch][DEBUG] Matching event [1]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action2]=[action2]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val2]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val2] found
[  pefParamMatch][DEBUG] Parameter [val2] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action2]; subscribed mechanisms=[0]
[       pefEvent][TRACE] Freeing event referencing action=[action2]
[       pefParam][TRACE] Freeing event parameter [val2]
[       pefParam][TRACE] Freeing parameter [val2][value2][type:0]
[       pefEvent][DEBUG] Event id=[1] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse/>]
[            pdp][TRACE] receiving event (2): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[2] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [2]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [2]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [2]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [2]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[            pdp][TRACE] condition NOT satisfied, doing nothing...
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [ALLOW]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[2] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <allow/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 9. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][TRACE] found an event B (op2), setting wasEverTrue and deactivating immutability
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[9] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (3): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[3] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [3]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [3]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [3]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [3]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[3] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 10. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[10] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (4): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[4] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [4]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [4]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [4]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [4]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[4] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 11. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[11] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (5): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[5] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [5]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [5]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [5]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [5]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[5] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 12. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[12] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (6): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[6] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [6]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [6]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [6]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [6]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[6] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 13. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[13] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (7): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[7] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [7]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [7]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [7]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [7]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[7] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 14. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[14] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 15. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] DID NOT found event A (op1))
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[15] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 16. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[16] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 17. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[17] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 18. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[18] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 19. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[19] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (8): [<event isTry="true" action="action2" index="ALL"><parameter name="val2" value="value2"/></event>]
[actionDescStore][TRACE] Action description [action2] found in store
[       pefEvent][TRACE] Creating event action=[action2] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[8] [action2][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val2] found
[       pefParam][TRACE] Adding parameter [val2][value2][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action2"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action2]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action2] for levent=[action2]
[  pefEventMatch][DEBUG] Matching event [8]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action2]=[action2]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val2]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val2] found
[  pefParamMatch][DEBUG] Parameter [val2] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action2]; subscribed mechanisms=[0]
[       pefEvent][TRACE] Freeing event referencing action=[action2]
[       pefParam][TRACE] Freeing event parameter [val2]
[       pefParam][TRACE] Freeing parameter [val2][value2][type:0]
[       pefEvent][DEBUG] Event id=[8] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse/>]
[            pdp][TRACE] receiving event (9): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[9] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [9]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [9]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [9]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [9]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[            pdp][TRACE] condition NOT satisfied, doing nothing...
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [ALLOW]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[9] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <allow/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 20. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][TRACE] found an event B (op2), setting wasEverTrue and deactivating immutability
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[20] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (10): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[10] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [10]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [10]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [10]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [10]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[10] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 21. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[21] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][TRACE] receiving event (11): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[11] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [11]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][TRACE]  evaluated EVENT    node => 1
[            pdp][DEBUG] Searching for triggered mechanism for event=[action1]; subscribed mechanisms=[1]
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testSince] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [11]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testSince]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[003ecc30]
[  pefEventMatch][DEBUG] Matching event [11]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[  pefEventMatch][DEBUG] Matching event [11]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action does not match [action1]!=[action2]
[  pefEventMatch][DEBUG] Event does not match operator
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testSince] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[11] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 22. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 1
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] Ok, intercepted A (op1)
[        oslEval][TRACE] updating states...
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 1 (immutable=[false])
[      condition][DEBUG]  [testSince] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[22] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 23. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [0]
[        oslEval][TRACE] not yet immutable
[        oslEval][TRACE] op2 is true or was already true [0][1]
[        oslEval][TRACE] DID NOT found event A (op1))
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[23] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 24. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[24] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testSince] Null-Event updating 25. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][DEBUG]  [testSince] - evaluating EVENT    node => 0
[        oslEval][TRACE]           op1: EVENTMATCH (action1)
[        oslEval][TRACE]           op2: EVENTMATCH (action2)
[        oslEval][TRACE] state immutability: [1]
[        oslEval][TRACE] already immutable...
[        oslEval][TRACE] updating states...
[        oslEval][TRACE]  [testSince] - evaluating SINCE    activating IMMUTABILITY
[        oslEval][DEBUG]  [testSince] - evaluating SINCE    node => 0 (immutable=[true])
[      condition][DEBUG]  [testSince] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testSince] event=[0] timestep=[25] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testSince] -----------------------------------------------------------
[            pdp][DEBUG] trying to deallocate pdpMutex
[    threadUtils][TRACE] Deallocating mechanism mutex [pdpMutex]
[    threadUtils][DEBUG] Mechanism mutex successfully deallocated.
Logger mutex successfully deallocated.
Cleaning up libxml2 related memory
pdpDestructor finished
