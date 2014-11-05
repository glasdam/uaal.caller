package dk.medware.rehab;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.ontology.rehabontology.ExerciseAnalyser;
import org.universAAL.ontology.rehabontology.ExerciseResults;
import org.universAAL.ontology.rehabontology.SuggestionResult;

public class SCaller extends ServiceCaller {

	private static final String CLIENT_OWN_NAMESPACE = "http://uaal.medware.dk/rehab/caller.owl#";
	private static final String EXPECT_OUTPUT = CLIENT_OWN_NAMESPACE + "output";

	protected SCaller(ModuleContext context) {
		super(context);
	}

	public void communicationChannelBroken() {
	}

	public void handleResponse(String reqID, ServiceResponse response) {
	}

	protected Boolean callGetExerciseSuggestions() throws ClientProtocolException, IOException{
		List<Long> ids = JsonServer.list_needs_evaluation(); 
		for(long id : ids){
			Map<String, List<Double>> coordinates = JsonServer.coordinates(id);
			if(coordinates.get("ys").size() <= 0) {
				continue;
			}
			System.out.println(coordinates);
			ServiceRequest setExerciseResults = new ServiceRequest(new ExerciseAnalyser(), null);
			ArrayList<ExerciseResults> results = new ArrayList<ExerciseResults>();
			ExerciseResults result = new ExerciseResults();
			System.out.println(coordinates.get("ys"));
			result.setResults(coordinates.get("ys").toArray(new Double[0]));
			result.setTime(coordinates.get("xs").toArray(new Double[0]));
			results.add(result);
			setExerciseResults.addAddEffect( new String []{ ExerciseAnalyser.PROP_EXERCISE_RESULTS }, results);
			setExerciseResults.addRequiredOutput(EXPECT_OUTPUT, new String[] {
					ExerciseAnalyser.PROP_SUGGESTION_RESULT });
			ServiceResponse sr = this.call(setExerciseResults);
			if(sr.getCallStatus() == CallStatus.succeeded){
				SuggestionResult suggestion = (SuggestionResult) sr.getOutput(EXPECT_OUTPUT).get(0);
				JsonServer.send_to_server(id, suggestion.getEstimate() , suggestion.getSlope(), suggestion.getRecommendation());
			} else {
				System.out.println("UAAL Call status: " + sr.getCallStatus());
			}
		}
		return true;
	}

}
