package nl.thedutchmc.SkinFixer.apis;

import java.io.IOException;

import com.google.gson.Gson;

import nl.thedutchmc.SkinFixer.SkinFixer;
import nl.thedutchmc.SkinFixer.gson.MojangAuthResponse;
import nl.thedutchmc.SkinFixer.util.Triple;
import nl.thedutchmc.SkinFixer.util.Utils;
import nl.thedutchmc.httplib.Http;
import nl.thedutchmc.httplib.Http.MediaFormat;
import nl.thedutchmc.httplib.Http.RequestMethod;
import nl.thedutchmc.httplib.Http.ResponseObject;

public class MojangApi {

	public Triple<Boolean, MojangAuthResponse, String> getUuidFromMojang(String playername) {		
		String requestBody = "[\"" + playername + "\"]";
		
		ResponseObject apiResponse;
		try {
			apiResponse = new Http().makeRequest(RequestMethod.POST, "https://api.mojang.com/profiles/minecraft", null, MediaFormat.JSON, requestBody, null);
		} catch(IOException e) {
			SkinFixer.logWarn("An IOException occured while fetching a UUID from Mojang.");
			//TODO logDebug
			
			return new Triple<Boolean, MojangAuthResponse, String>(false, null, Utils.getStackTrace(e));
		}
		
		if(apiResponse.getResponseCode() != 200) {
			SkinFixer.logWarn("The Mojang API returned an unexpected result: " + apiResponse.getConnectionMessage());
			return new Triple<Boolean, MojangAuthResponse, String>(false, null, apiResponse.getConnectionMessage());
		}

				
		final Gson gson = new Gson();
		MojangAuthResponse[] authResponse = gson.fromJson(apiResponse.getMessage(), MojangAuthResponse[].class);
		
		if(authResponse.length == 0) {
			return new Triple<Boolean, MojangAuthResponse, String>(true, null, null);
		}
		
		return new Triple<Boolean, MojangAuthResponse, String>(true, authResponse[0], null);
	}
}