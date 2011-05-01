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
package controllers;

import play.Logger;
import play.mvc.WebSocketController;
import util.ExceptionUtil;

/**
 * The Class LiveSearches.
 */
public class LiveSearches {

	/** The live stream. */
	public static play.libs.F.EventStream<String> liveStream = new play.libs.F.EventStream<String>();

	/**
	 * The Class WebSocket.
	 */
	public static class WebSocket extends WebSocketController {

		/**
		 * Live searches.
		 */
		public static void stream() {
			while (inbound.isOpen()) {
				try {
					Logger.info("Waiting for next search...");
					String search = await(liveStream.nextEvent());
					if (search != null) {
						Logger.info("Publishing Live Search %s to Outbound Subscribers", search);
						outbound.send(search);
					}

				} catch (Throwable t) {
					Logger.error(ExceptionUtil.getStackTrace(t));
				}
			}
		}
	}

}
