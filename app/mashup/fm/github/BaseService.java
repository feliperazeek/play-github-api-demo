/** 
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Felipe Oliveira (http://mashup.fm)
 * 
 */
package mashup.fm.github;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.restlet.resource.ClientResource;

import play.Logger;
import util.ExceptionUtil;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * The Class BaseGithub.
 */
public abstract class BaseService {

	/**
	 * Check input param.
	 * 
	 * @param name
	 *            the name
	 * @param s
	 *            the s
	 */
	protected void checkInputParam(String name, String s) {
		if (StringUtils.isBlank(s)) {
			throw new RuntimeException("Invalid Input Parameter: " + name);
		}
	}

	/**
	 * Invoke the remote service and returns the response as a single object.
	 * 
	 * @param arrayName
	 *            the array name
	 * @param urlPattern
	 *            the url pattern
	 * @param args
	 *            the args
	 * @return the json object
	 */
	protected static <T> T asObject(Class<T> clazz, String arrayName, String urlPattern, String... args) {
		// Define Remote Service Url
		String url = url(urlPattern, args);

		// Wrap Into Object
		TypeToken<T> type = TypeToken.get(clazz);
		T o = unmarshall(type, ((JsonObject) new JsonParser().parse(getStringData(url))).get(arrayName));

		// Return Results
		return o;
	}

	/**
	 * Invoke the remote service and return the response as a list of objects.
	 * 
	 * @param arrayName
	 *            the array name
	 * @param urlPattern
	 *            the url pattern
	 * @param args
	 *            the args
	 * @return the list of objects - it will return an empty list to avoid
	 *         checks for null on the client side
	 */
	protected static <T> List<T> asArray(Class<T> clazz, String arrayName, String urlPattern, String... args) {
		// Define Remote Service Url
		String url = url(urlPattern, args);

		// Init Results List
		List<T> list = new ArrayList<T>();

		// Wrap response into list of objects
		TypeToken<T> type = TypeToken.get(clazz);
		JsonArray array = (JsonArray) ((JsonObject) new JsonParser().parse(getStringData(url))).get(arrayName);
		if ((array != null) && (array.size() > 0)) {
			for (JsonElement element : array) {
				T o = unmarshall(type, element);
				list.add(o);
			}
		}

		// Return List
		return list;
	}

	/**
	 * Unmarshall.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param typeToken
	 *            the type token
	 * @param response
	 *            the response
	 * @return the t
	 */
	private static <T> T unmarshall(TypeToken<T> typeToken, JsonElement response) {
		Gson gson = getGsonBuilder().create();
		return (T) gson.fromJson(response, typeToken.getType());
	}

	/**
	 * Gets the gson builder.
	 * 
	 * @return the gson builder
	 */
	private static GsonBuilder getGsonBuilder() {
		// Init Builder
		GsonBuilder builder = new GsonBuilder();

		// This is the code that will try to parse dates
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			@Override
			public Date deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
				try {
					if (arg0.getAsString().indexOf(' ') == 0) {
						// 2011-03-23T05:14:20-07:00
						DateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss-mm:mm");
						return df.parse(arg0.getAsString());
					}
					DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
					return df.parse(arg0.getAsString());

				} catch (Throwable t) {
					Logger.warn(ExceptionUtil.getStackTrace(t));
					return null;
				}
			}
		});

		// We use camel case and the api returns lower case with underscore -
		// fieldId versus field_id
		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

		// Return Builder
		return builder;
	}

	/**
	 * Define the url that will be inquired on the remote API service
	 * 
	 * @param url
	 *            the url
	 * @param args
	 *            the args
	 * @return the string
	 */
	private static String url(String url, String... args) {
		try {
			String u = String.format(url, args);
			String empty = "";
			u = StringUtils.replace(u, String.valueOf('"'), empty);
			Logger.info("Github Url: %s", u);
			return u;
		} catch (Throwable t) {
			Logger.error(ExceptionUtil.getStackTrace(t));
			throw new RuntimeException(t.fillInStackTrace());
		}
	}

	/**
	 * Gets the response string
	 * 
	 * @param req
	 *            the req
	 * @return the string data
	 */
	private static String getStringData(String req) {
		try {
			return new ClientResource(req).get().getText();
		} catch (Exception e) {
			if (!(e instanceof RuntimeException)) {
				throw new RuntimeException("Error getting data from github", e);
			} else {
				throw (RuntimeException) e;
			}
		}
	}

}
