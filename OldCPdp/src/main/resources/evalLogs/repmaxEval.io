Successfully created mutex [pefLogger]
[    threadUtils][DEBUG] Successfully created mutex [pdpMutex]
pdpConstructor successfully finished
[  testOperators][WARN ] Starting testpdp application for operator-tests
[    socketWin32][DEBUG] Winsock initialized!
[    socketWin32][INFO ] Successfully created win-TCP-socket for port=[9983]
[    socketUtils][INFO ] Opened TCP-socket (fd=[1848], port=[9983])
[    threadUtils][DEBUG] Created new thread with handler=[00417da6] => threadID=[0]
[    socketUtils][DEBUG] pefSocket successfully allocated for port=[9983] with handler=[00407422]
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
[            pdp][TRACE] pdpDeployPolicy - c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testRepmax.xml
[    pdpInternal][DEBUG] Loading mechanisms from file: c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testRepmax.xml (for mechanism=[NULL])
[       xmlUtils][INFO ] XML file [c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testRepmax.xml] is valid according to pef-schema
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
[      mechanism][TRACE] MechanismFullName=[c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testRepmax.xml#testRepmax]
[    threadUtils][DEBUG] Successfully created mutex [testRepmax]
[      mechanism][TRACE] Successfully allocated mechanism "testRepmax" in namespace "c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testRepmax.xml" (@ 1338043723531250)
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
[   oslOperators][TRACE] Operator type: REPMAX
[   oslOperators][TRACE]           op1: 3
[   oslOperators][TRACE]           op2: EVENTMATCH
[      condition][DEBUG] Condition formula with 2 nodes
[      condition][TRACE]  REPMAX [limit=3]
[      condition][TRACE]   EVENTMATCH [actionName=action1]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 0, delay: 0)
[      mechanism][TRACE] Successfully parsed mechanism testRepmax (size: 131 bytes)
[      mechanism][TRACE] Mechanism: 
[      mechanism][TRACE]          name: testRepmax
[      mechanism][TRACE]  timestepSize: 3000000
[      mechanism][TRACE]         start: 1338043723531250
[      mechanism][TRACE]   last update: 1338043723531250
[      mechanism][TRACE]  cur timestep: 0
[    pdpInternal][INFO ] Successfully added mechanism "testRepmax" to hashtable
[    pdpInternal][DEBUG] Starting update thread for mechanisms=[testRepmax] with usleep=[3000000]
[    threadUtils][DEBUG] Created new thread with handler=[0040c250] => threadID=[1868770928]
[    threadUtils][DEBUG] Thread successfully started
[      mechanism][INFO ] Started mechanism update thread usleep=300000 us
[    pdpInternal][INFO ] Successfully loaded mechanisms from file: c:\local\rd\usr\home\raindrop\workspace\pef\src\main\xml\examples\testRepmax.xml
[            pdp][TRACE] pdpDeployPolicy - loading policy returned [SUCCESS]
[  testOperators][TRACE] deploy returned=[0]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 0. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 0
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[0], op2->state->cntTrue=[0]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[0] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 1. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 0
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[0], op2->state->cntTrue=[0]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[1] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 2. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 0
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[0], op2->state->cntTrue=[0]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[2] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[            pdp][TRACE] receiving event (0): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[0] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [0]
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
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testRepmax] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [0]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testRepmax]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[00e60688]
[  pefEventMatch][DEBUG] Matching event [0]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[1], op2->state->cntTrue=[0]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testRepmax] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[0] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 3. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[1], op2->state->cntTrue=[0]
[        oslEval][TRACE] op2 was true this timestep; increment counters to [1]...
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[3] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[            pdp][TRACE] receiving event (1): [<event isTry="true" action="action1" index="ALL"><parameter name="val1" value="value1"/></event>]
[actionDescStore][TRACE] Action description [action1] found in store
[       pefEvent][TRACE] Creating event action=[action1] index=[ongoing] is_try=[true]
[       pefEvent][TRACE] Event id=[1] [action1][ongoing][true] created
[       pefEvent][DEBUG] no parameter type given for event; defaulting to PARAM_SRING!
[         action][TRACE] Param description [val1] found
[       pefParam][TRACE] Adding parameter [val1][value1][type:0] to event
[       pefEvent][DEBUG] Successfully parsed XML and created event referencing action "action1"
[            pdp][DEBUG] Searching for subscribed condition nodes for event=[action1]; subscribed nodes=[1]
[            pdp][DEBUG] conditionTriggerEvent - checking condition eventMatch=[action1] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [1]
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
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testRepmax] for levent=[action1]
[  pefEventMatch][DEBUG] Matching event [1]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testRepmax]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[00e60688]
[  pefEventMatch][DEBUG] Matching event [1]
[  pefEventMatch][TRACE] Event index matches operator [ongoing]~=[ongoing]
[  pefEventMatch][TRACE] Event is_try matches operator [true]=[true]
[  pefEventMatch][TRACE] Event action matches [action1]=[action1]
[  pefParamMatch][DEBUG] matchEventParameters - comparing trigger event (nParam=[1]) with desired event(nParam=[1])
[  pefParamMatch][DEBUG] param [val1]: param value for matching, calling specific matching function...
[  pefParamMatch][DEBUG] Matching parameter using common compare
[       pefParam][DEBUG] Event parameter [val1] found
[  pefParamMatch][DEBUG] Parameter [val1] match
[  pefEventMatch][DEBUG] Event matches operator
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[1], op2->state->cntTrue=[1]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testRepmax] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[1] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 4. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[1], op2->state->cntTrue=[1]
[        oslEval][TRACE] op2 was true this timestep; increment counters to [2]...
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[4] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
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
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testRepmax] for levent=[action1]
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
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testRepmax]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[00e60688]
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
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[1], op2->state->cntTrue=[2]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[    pdpInternal][DEBUG] Adding mechanism=[testRepmax] to response
[    pdpInternal][TRACE] updating ACTION_ALLOW to ACTION_INHIBIT
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [INHIBIT]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[2] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <inhibit/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 5. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] ok, set current state value to TRUE (op2->state->value=[1], op2->state->cntTrue=[2]
[        oslEval][TRACE] op2 was true this timestep; increment counters to [3]...
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 1 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[true]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[5] value=[true]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
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
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testRepmax] for levent=[action1]
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
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testRepmax]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[00e60688]
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
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] set current state value to FALSE (op2->state->value=[1], op2->state->cntTrue=[3]
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 0 (immutable=[false])
[      condition][DEBUG]  [testRepmax] - condition value=[false]
[            pdp][TRACE] condition NOT satisfied, doing nothing...
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [ALLOW]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[3] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <allow/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 6. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE] set current state value to FALSE (op2->state->value=[1], op2->state->cntTrue=[3]
[        oslEval][TRACE] op2 was true this timestep; increment counters to [4]...
[        oslEval][TRACE] [REPMAX] Activating immutability
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 0 (immutable=[true])
[      condition][DEBUG]  [testRepmax] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[6] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
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
[            pdp][DEBUG] mechanismTriggerEvent - checking mech=[testRepmax] for levent=[action1]
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
[            pdp][INFO ] mechanismTriggerEvent - Found matching mechanism -> mechanism_name=[testRepmax]
[authorizationAc][DEBUG] Successfully allocated new mechanism_action (response: 1, delay: 0)
[      condition][TRACE] conditionUpdate - updating with event=[00e60688]
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
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 0 (immutable=[true])
[      condition][DEBUG]  [testRepmax] - condition value=[false]
[            pdp][TRACE] condition NOT satisfied, doing nothing...
[    pdpInternal][DEBUG] notifyEvent response: 
[    pdpInternal][DEBUG]    response: [ALLOW]
[    pdpInternal][DEBUG]    delay:    [0]
[    pdpInternal][DEBUG]    modifiers:[0]
[       pefEvent][TRACE] Freeing event referencing action=[action1]
[       pefParam][TRACE] Freeing event parameter [val1]
[       pefParam][TRACE] Freeing parameter [val1][value1][type:0]
[       pefEvent][DEBUG] Event id=[4] deallocated
[  testOperators][TRACE] response=[<notifyEventResponse>
    <authorizationAction>
      <allow/>
    </authorizationAction>
  </notifyEventResponse>]
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 7. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 1
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 0 (immutable=[true])
[      condition][DEBUG]  [testRepmax] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[7] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 8. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 0
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 0 (immutable=[true])
[      condition][DEBUG]  [testRepmax] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[8] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[      mechanism][WARN ] ////////////////////////////////////////////////////////////////////////////////////////////////////////////
[      mechanism][DEBUG] mechanismUpdate - [testRepmax] Null-Event updating 9. timestep at interval of 3000000 us
[      condition][TRACE] conditionUpdate - updating with event=[00000000]
[        oslEval][TRACE]  [testRepmax] - evaluating EVENT    node => 0
[        oslEval][TRACE]  [testRepmax] - evaluating REPMAX   node => 0 (immutable=[true])
[      condition][DEBUG]  [testRepmax] - condition value=[false]
[      mechanism][INFO ] mechanismUpdate - [testRepmax] event=[0] timestep=[9] value=[false]
[      mechanism][TRACE] mechanismUpdate - [testRepmax] -----------------------------------------------------------
[            pdp][DEBUG] trying to deallocate pdpMutex
[    threadUtils][TRACE] Deallocating mechanism mutex [pdpMutex]
[    threadUtils][DEBUG] Mechanism mutex successfully deallocated.
Logger mutex successfully deallocated.
Cleaning up libxml2 related memory
pdpDestructor finished