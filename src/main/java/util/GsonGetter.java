package util;

import com.google.gson.Gson;


public class GsonGetter{
	private static Gson gson = new Gson();

	public static Gson get(){

		return gson;
		
	}
}