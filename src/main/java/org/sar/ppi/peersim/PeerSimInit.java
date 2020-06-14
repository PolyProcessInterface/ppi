package org.sar.ppi.peersim;

import org.sar.ppi.PpiException;
import org.sar.ppi.events.Scenario;
import org.sar.ppi.events.ScheduledEvent;
import peersim.config.Configuration;
import peersim.config.MissingParameterException;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.util.ExtendedRandom;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		for(int i=0;i < Network.size();i++) {
			Node node=Network.get(i);
			PeerSimInfrastructure pInfra = (PeerSimInfrastructure) node.getProtocol(infrapid);
			pInfra.initialize(node);
		}
		if(FileName!=null)
			launchSimulation(FileName);
		return false;
	}

	/**
	 *
	 * @param path
	 * path to the json file
	 */
	private void launchSimulation(String path) {
		Scenario scenario;
		ObjectMapper mapper = new ObjectMapper();
		try {
			scenario = mapper.readValue(new File(FileName), Scenario.class);
		} catch (IOException e) {
			throw new PpiException("Invalid scenario file", e);
		}
		for (ScheduledEvent e : scenario.getEvents()) {
			EDSimulator.add(e.getDelay(), e, Network.get(e.getNode()), infrapid);
		}
	}
}
