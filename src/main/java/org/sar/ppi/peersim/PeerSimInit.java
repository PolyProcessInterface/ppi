package org.sar.ppi.peersim;

import org.sar.ppi.communication.AppEvents.OffEvent;
import org.sar.ppi.communication.AppEvents.OnEvent;
import org.sar.ppi.communication.AppEvents.SchedEvent;
import org.sar.ppi.tools.ProtocolTools;
import peersim.config.Configuration;
import peersim.config.MissingParameterException;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.util.ExtendedRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * PeerSimInit class.
 */
public class PeerSimInit implements Control {

	private static final String PAR_PROTO="infrapid";
	public static final String PAR_SEED = "random.seed";

	private static final String TRANSPORT_SIMULATION="transport";
	private final int infrapid;

	private  int pid_trans;
	private String FileName;

	/**
	 * Constructor for PeerSimInit.
	 *
	 * @param prefix a {@link java.lang.String} object.
	 */
	public PeerSimInit(String prefix) {
		infrapid=Configuration.getPid(prefix+"."+PAR_PROTO);
		try{
		pid_trans=Configuration.getPid(prefix+"."+TRANSPORT_SIMULATION);
		FileName = Configuration.getString("path");
		}catch (MissingParameterException e){
			pid_trans=-1;
			FileName=null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean execute() {
		
		long seed =	Configuration.getLong(PAR_SEED,System.currentTimeMillis());
		System.err.println("SETTING THE RANDOM.SEED TO "+seed);
		CommonState.r=new ExtendedRandom(seed);
		HashMap<Integer,PeerSimInfrastructure>  mapOfInfra = new HashMap<>();
		for(int i=0;i < Network.size();i++) {
			Node node=Network.get(i);
			PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
			pInfra.initialize(node);
			mapOfInfra.put(pInfra.getId(),pInfra);
		}
		if(FileName!=null)
			launchSimulation(FileName,mapOfInfra);
		return false;
	}

	/**
	 *
	 * @param path
	 * path to the json file
	 * @param mapInfra
	 * map of all application node
	 */
	private void launchSimulation(String path, HashMap<Integer,PeerSimInfrastructure> mapInfra) {
		HashMap<String, List<Object[]>> map = ProtocolTools.readProtocolJSON(path);
		List<Object[]> l_call = map.get("events");
		int num_node;
		long delay;
		for(Object[] func : l_call){
			num_node=(int)func[1];
			delay=(long)func[2];
			EDSimulator.add(delay,new SchedEvent((String) func[0], Arrays.copyOfRange(func,3,func.length),mapInfra.get(num_node)),Network.get(num_node),infrapid);
		}
		l_call = map.get("undeploy");
		Node node;
		for(Object[] func : l_call) {
			num_node = (int) func[0];
			delay = (long) func[1];
			EDSimulator.add(delay,new OffEvent(mapInfra.get(num_node)),Network.get(num_node),infrapid);
		}
		l_call = map.get("deploy");
		for(Object[] func : l_call) {
			num_node = (int) func[0];
			delay = (long) func[1];
			EDSimulator.add(delay,new OnEvent(mapInfra.get(num_node)),Network.get(num_node),infrapid);
		}
	}
}
