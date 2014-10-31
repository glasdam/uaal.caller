package dk.medware.rehab;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.ontology.phThing.DeviceService;
import org.universAAL.ontology.rehabontology.ExerciseAnalyser;
import org.universAAL.ontology.rehabontology.ExerciseResults;
import org.universAAL.ontology.rehabontology.RehabontologyOntology;
import org.universAAL.ontology.device.SwitchController;

public class SCaller extends ServiceCaller {

	/* -Example- This namespace should only be used here */
	private static final String CLIENT_OWN_NAMESPACE = "http://uaal.medware.dk/rehab/callee.owl#"; //  "http://your.ontology.URL.com/YourClientDomainOntology.owl#";
	// TODO: Change Namespace
	/* -Example- URI Constants for handling and identifying outputs */
	private static final String EXPECT_OUTPUT = CLIENT_OWN_NAMESPACE + "output";

	protected SCaller(ModuleContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

	public void handleResponse(String reqID, ServiceResponse response) {
		// TODO Auto-generated method stub

	}

	/*
	 * -Example- These shortcut methods allow other classes to quickly invoke
	 * service calls and get the result. This example can call its own callee.
	 */
	protected Boolean callSetStatus(boolean status) {
		ServiceResponse sr = this.call(getSetStatus(status));
		return new Boolean(sr.getCallStatus() == CallStatus.succeeded);
	}

	protected Boolean callGetStatus() {
		ServiceResponse sr = this.call(getGetStatus());
		if (sr.getCallStatus() == CallStatus.succeeded) {
			List<Object> outs = sr.getOutput(EXPECT_OUTPUT);
			if (outs == null || outs.isEmpty()) {
				return null;
			} else {
				return (Boolean) outs.get(0);
			}
		} else {
			return null;
		}
	}

	protected Boolean callGetExerciseSuggestions() throws ClientProtocolException, IOException{
		JsonServer.service_server();
		List<Long> ids = JsonServer.list_needs_evaluation(); 
		//System.out.println(ids);
		ids.add(1l);
		for(long id : ids){
			Map<String, List<Double>> coordinates = JsonServer.coordinates(id);
			
			ServiceRequest setExerciseResults = new ServiceRequest(new ExerciseAnalyser(), null);
			ArrayList<ExerciseResults> results = new ArrayList<ExerciseResults>();
			ExerciseResults result = new ExerciseResults();
			result.setResults((Double[])coordinates.get("ys").toArray());
			result.setTime((Double[])coordinates.get("xs").toArray());
			results.add(result);
			setExerciseResults.addAddEffect( new String []{ ExerciseAnalyser.PROP_EXERCISE_RESULTS }, results);
			setExerciseResults.addRequiredOutput(EXPECT_OUTPUT, new String[] {
					ExerciseAnalyser.PROP_SUGGESTION_RESULT });
			ServiceResponse sr = this.call(setExerciseResults);
			//System.out.println("Call status: " + sr.getCallStatus());
			System.out.println("Call response: "+ sr.getOutput(EXPECT_OUTPUT));
		}
		return true;
	}


	/*
	 * -Example- These "get" methods elaborate the Service Request for each
	 * appropriate call
	 */
	private ServiceRequest getSetStatus(boolean status) {
		// This ServiceRequest matches the first profile provided by the callee
		ServiceRequest setStatus = new ServiceRequest(new DeviceService(), null);
		setStatus.addValueFilter(new String[] { DeviceService.PROP_CONTROLS },
				new SwitchController(CPublisher.DEVICE_OWN_URI));
		setStatus.addChangeEffect(new String[] { DeviceService.PROP_CONTROLS,
				SwitchController.PROP_HAS_VALUE }, new Boolean(status));
		return setStatus;
	}

	private ServiceRequest getGetStatus() {
		// This ServiceRequest matches the second profile provided by the callee
		ServiceRequest setStatus = new ServiceRequest(new DeviceService(), null);
		setStatus.addValueFilter(new String[] { DeviceService.PROP_CONTROLS },
				new SwitchController(CPublisher.DEVICE_OWN_URI));
		setStatus.addRequiredOutput(EXPECT_OUTPUT, new String[] {
				DeviceService.PROP_CONTROLS, SwitchController.PROP_HAS_VALUE });
		return setStatus;
	}

}
