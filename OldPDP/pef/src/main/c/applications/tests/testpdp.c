/**
 * @file  testpdp.c
 * @brief Exemplary execution test for native PDP
 *
 * @author cornelius moucha
**/

#include <sys/stat.h>
#include <unistd.h>
#include "pdp.h"
#include "log_testpdp_pef.h"

unsigned int pepSubscribeNative(pdpInterface_ptr linterface, char *name, unsigned int unsubscribe)
{
  log_info("pepSubscribeNative invoked for name=[%s] and unsubscribe=[%d]...", name, unsubscribe);
  return R_SUCCESS;
}

unsigned int pxpExecuteNative(pdpInterface_ptr linterface, char *name, unsigned int cntParams, parameterInstance_ptr *params)
{
  log_info("pxpExecuteNative invoked for name=[%s] with [%d] params...", name, cntParams);
  int a;
  for(a=0; a<cntParams; a++)
  {
    if(strncasecmp(params[a]->value, "abort", 5)==0) return R_ERROR;
    log_info("\tparam [%d]: [%s] => [%s]", a, params[a]->paramDesc->name, params[a]->value);
  }
  return R_SUCCESS;
}

char *memEvent4="<event isTry=\"true\" action=\"action1\"><parameter name=\"val1\" value=\"value1\"/></event>";
char *memEvent4b="<event isTry=\"true\" action=\"action2\"><parameter name=\"val2\" value=\"value2\"/></event>";

extern pdp_ptr pdp;

int main(int argc, char **argv)
{
  log_warn("Starting native Test-PDP");
  int ret=pdpStart();
  log_trace("PDP started with result=[%s]",returnStr[ret]);

  // register this 'PEP'
  //pdpInterface_ptr linterface=pdpInterfaceNew("testPEP", PDP_INTERFACE_NATIVE);
  //linterface->pepSubscribe=pepSubscribeNative;


  //ret=pdpRegisterPEP("testpdpPEP", linterface);
  //log_trace("PEP registration => [%s]",returnStr[ret]);

  //ret=pdpRegisterAction("action1", "testpdpPEP");
  //log_trace("Action registration => [%s]",returnStr[ret]);
  //ret=pdpRegisterAction("action2", "testpdpPEP");
  //log_trace("Action registration => [%s]",returnStr[ret]);

  // registering PXP (requires separate interface!!)
  pdpInterface_ptr linterface2=pdpInterfaceNew("testPXP", PDP_INTERFACE_NATIVE);
  linterface2->pxpExecute=pxpExecuteNative;
  ret=pdpRegisterPXP("testPXP", linterface2);
  log_trace("PXP registration => [%s]",returnStr[ret]);

  ret=pdpRegisterExecutor("notify", "testPXP");
  log_trace("Executor registration => [%s]",returnStr[ret]);

  //char *policyFileName="c:\\local\\rd\\usr\\home\\moucha\\workspace\\pef\\src\\main\\xml\\examples\\test.xml";
  char *policyFileName="/home/uc/workspace/pef/src/main/xml/examples/test.xml";

  ret=pdpDeployPolicy(policyFileName);
  log_trace("deploy returned=[%d]",ret);

  getchar();

  //char *actionResponse=pdpNotifyEventXML(memEvent4b);
  //log_trace("response=[%s]", actionResponse);
  //free(actionResponse);

  /*
  actionDescription_ptr actionDesc=actionDescriptionFind(pdp->actionDescStore, "event.homeapp.controller.backend", TRUE);
  event_ptr levent=eventNew(actionDesc, TRUE);
  eventAddParamString(levent, "payload", "&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&lt;message expires=&quot;0&quot; timestamp=&quot;0&quot; type=&quot;&quot; xsi:schemaLocation=&quot;http://iese.fhg.de/ami/homeapp/messageshomeui_messages.xsd&quot; xmlns:ns2=&quot;http://iese.fhg.de/ami/information/model&quot; xmlns=&quot;http://iese.fhg.de/ami/homeapp/messages&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;&lt;startSession sessionId=&quot;GRqBthz4kjX8ClB6&quot;/&gt;&lt;/message&gt;");
  notifyResponse_ptr response=pdpNotifyEvent(levent);
  */

  actionDescription_ptr actionDesc=actionDescriptionFind(pdp->actionDescStore, "action2", TRUE);
  event_ptr levent=eventNew(actionDesc, TRUE);
  eventAddParamString(levent, "val2", "value2");
  notifyResponse_ptr response=pdpNotifyEvent(levent);

  log_warn("stopping PDP=[%d]\n", pdpStop());

  // this may result in SEGFAULT as it is already deallocated with stopping PDP!
  // there is a check for double dellocation, but it sometimes fails in windows
  //pdpInterfaceFree(linterface);
  return 0;
}






