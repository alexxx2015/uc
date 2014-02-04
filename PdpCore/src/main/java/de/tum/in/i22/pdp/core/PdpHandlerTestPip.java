package de.tum.in.i22.pdp.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import com.google.inject.Inject;

import de.tum.in.i22.cm.pdp.PolicyDecisionPoint;
import de.tum.in.i22.pdp.pipcacher.IPdpCore2PipCacher;
import de.tum.in.i22.pdp.pipcacher.IPdpEngine2PipCacher;
import de.tum.in.i22.pdp.pipcacher.PipCacherImpl;
import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.KeyBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * This contains some tests to run the PIP "inside" the PDP
 * 
 * @author Lovat
 * 
 */
public class PdpHandlerTestPip implements IIncoming {

	private static final Logger _logger = Logger.getLogger(PdpHandlerTestPip.class);
	private static IPdpCore2PipCacher _core2pip;
	private static IPdpEngine2PipCacher _engine2pip;
	private static IPipCacher2Pip _pipHandler;
	private static IMessageFactory _messageFactory;
		
	
	private static PolicyDecisionPoint _lpdp;
	
	
	//TEST ONLY
	private IKey _test_predicate_key = KeyBasic.createNewKey();
	private String _test_predicate="isNotIn|TEST_D|TEST_C|0";
	
//	private IPdpCore2PipCacher _pipCacher = PipCacherImpl.getReference(); 

	public void initializeAll(){
		_pipHandler = new PipHandler();
		_core2pip= new PipCacherImpl(_pipHandler);
		_engine2pip= (PipCacherImpl) _core2pip;
		
		_messageFactory = MessageFactoryCreator.createMessageFactory();
		
		Map <String,IKey> predicates=new HashMap<String,IKey>();

		predicates.put(_test_predicate, _test_predicate_key);
		_logger.debug("TEST: adding predicate via _core2pip");
		_core2pip.addPredicates(predicates);
				
		//Initialize if model with TEST_C --> TEST_D
		_logger.debug("TEST: Initialize if model with TEST_C-->TEST_D");
		IEvent initEvent = _messageFactory.createActualEvent("SchemaInitializer", new HashMap<String, String>());
		_pipHandler.notifyActualEvent(initEvent);
	}
	
	public PdpHandlerTestPip() {
		initializeAll();
	}
	
	@Inject
	public PdpHandlerTestPip(PolicyDecisionPoint lpdp){
		_lpdp = lpdp;
		try {
			//_logger.info("Get instance of native PDP (skipped)...");
			//_lpdp.initialize();
			//_logger.info("Start native PDP (skipped) ..");
			//_lpdp.pdpStart();
			_logger.info("JavaPDP started");
			_lpdp.deployPolicy("/home/uc/pdpNew/pdp/OldCPdp/src/main/xml/examples/testTUM.xml");
			_logger.info("Test policy deployed");
		} catch (Exception e) {
			_logger.fatal("Could not load native PDP library! " + e.getMessage());
		}
		initializeAll();
	}
	
	
	
	@Override
	public IStatus deployMechanism(IMechanism mechanism) {
		// TODO implement
		_logger.debug("Deploy mechanism called");
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO implement
		_logger.debug("Export mechanism called");
		return DummyMessageGen.createMechanism();
	}

	@Override
	public IStatus revokeMechanism(String par) {
		_logger.debug("Revoke mechanism called");
		// TODO implement
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		// TODO implement
		_logger.debug("Notify event called");
		IStatus s=_messageFactory.createStatus(EStatus.OKAY);

		if (event!=null){
			if (event.isActual()){
				_logger.debug("event is Actual. notifying Pip(cacher)");
				s=_pipHandler.notifyActualEvent(event);
				_logger.debug("Pip(cacher) notification response : "+ s);
			} else{
				_logger.debug("event is Desired. Let's do some tests");

				Boolean b=_engine2pip.evaluatePredicateCurrentState(_test_predicate);
				_logger.debug("test evaluation of test predicate in current actual state: "+b);
				
				_logger.debug("Refresh pipCacher");
				_core2pip.refresh(event);
				
				b=_engine2pip.evaluatePredicateCurrentState(_test_predicate);
				_logger.debug("evaluate test predicate in current actual state: "+b);
				
				
				b=_engine2pip.eval(_test_predicate_key);
				_logger.debug("evaluate test predicate on cache state: "+b);
				
			}
		}
  	   
		IResponse r=new ResponseBasic(s, null, null);
		return r;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution) {
		// leave empty
		// this method is never called
		// instead PDP delegates it to PIP
		return null;
	}
}
