package dk.medware.rehab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import static java.util.Arrays.asList;

public class JsonServer {

	static String API_KEY = "45603837-8D85-4F27-B73C-2EC481007F36";
	static String BASE_URL = "http://fys.medware.dk/uaal_api/evaluations";

	public static void main(String[] args) throws ClientProtocolException, IOException {
		List<Long> ids = list_needs_evaluation(); 
		puts(ids);
		for(long id : ids){
			Map<String, List<Double>> result = coordinates(id);
			//			RegressionFormula rf = ProgressionCalculator.create_weighted(result.get("xs"), result.get("ys"));
			//			puts(rf.to_string());
			//			puts(rf.y(result.get("xs").size()));
			//List<Double> results = ProgressionCalculator.calculate(result.get("xs"), result.get("ys"));
			puts(result);
			//send_to_server(id, results);
		}
	}
	
	public static void service_server() throws ClientProtocolException, IOException{
		List<Long> ids = list_needs_evaluation(); 
		//puts(ids);
		for(long id : ids){
			Map<String, List<Double>> result = coordinates(id);
			puts("Got list");
			puts(result);
/*			List<Double> results = ProgressionCalculator.calculate(result.get("xs"), result.get("ys"));
			puts(results);
			send_to_server(id, results);
			*/
		}
	}
	
	public static void puts(Object arg){
		System.out.println(arg);
	}

	public static List<Long> list_needs_evaluation() throws ClientProtocolException, IOException{
		Response response = Request.Get(BASE_URL+"?api_key="+API_KEY).execute();
		String json_string = response.returnContent().asString();
		//puts(json_string);
		Object obj = JSONValue.parse(json_string);
		JSONArray array = (JSONArray)obj;
		List<Long> ids = new ArrayList<Long>();
		for(Object obj2 : array){
			JSONObject json = (JSONObject)obj2;
			ids.add((Long) json.get("id"));
		}
		return ids;
	}

	public static Map<String, List<Double>> coordinates(long id) throws ClientProtocolException, IOException{
		/*Response response = Request.Get(BASE_URL+"/"+id+"?api_key="+API_KEY).execute();
		String json_string = response.returnContent().asString();
		JSONObject obj = (JSONObject)JSONValue.parse(json_string);
		List<Double> ys = new ArrayList<Double>();
		List<Double> xs = new ArrayList<Double>();
		JSONArray json = (JSONArray)obj.get("ys");
		for(Object value : json){
			ys.add(((Long)value).doubleValue());
		}
		json = (JSONArray)obj.get("xs");
		for(Object value : json){
			xs.add(((Long)value).doubleValue());
		}
		*/
		//JSONArray array = (JSONArray)obj;
		List<Double> ys = asList(3d, 3d, 3d, 2d, 1d, 4d, 2d, 1d, 4d, 2d, 1d, 4d, 2d, 1d, 3d, 5d, 5d, 5d, 1d, 1d, 1d, 1d, 1d, 1d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 5d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d);
		List<Double> xs = asList(1402923820d, 1402923820d, 1402923820d, 1402927643d, 1402927643d, 1402927643d, 1402927930d, 1402927930d, 1402927930d, 1402928536d, 1402928536d, 1402928536d, 1402929357d, 1402929357d, 1402929357d, 1403011392d, 1403011392d, 1403011392d, 1403011470d, 1403011470d, 1403011470d, 1403011500d, 1403011500d, 1403011500d, 1403012929d, 1403012929d, 1403012929d, 1403012988d, 1403012988d, 1403012988d, 1403013452d, 1403013452d, 1403013452d, 1403013474d, 1403013474d, 1403013474d, 1403013534d, 1403013534d, 1403013534d, 1403013573d, 1403013573d, 1403013573d, 1403013596d, 1403013596d, 1403013596d, 1403013625d, 1403013625d, 1403013625d);
		Map<String, List<Double>> result = new HashMap<String, List<Double>>();
		result.put("xs", xs);
		result.put("ys", ys);
		return result;
	}

	public static void send_to_server(long id, List<Double> results) throws ClientProtocolException, IOException{
		String url = BASE_URL+"/"+id+"/result?api_key="+API_KEY+"&value="+results.get(0)+"&slope="+results.get(1)+"&recommendation="+results.get(2);
		String status = Request.Get(url).execute().returnResponse().toString();
		puts("Done "+id+" with "+status);
	}

}
