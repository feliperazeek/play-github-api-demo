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
import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

/**
 * Functional Test for REST Interface. See GithubService.java for more details.
 */
public class RESTfulTest extends FunctionalTest {

	/**
	 * Repository.
	 */
	@Test
	public void repository() {
		Response response = GET("/rest/github/repository/feliperazeek/socialestates");
		this.basicTest(response);
	}

	/**
	 * User.
	 */
	@Test
	public void user() {
		Response response = GET("/rest/github/user/feliperazeek");
		this.basicTest(response);
	}

	/**
	 * Repository search.
	 */
	@Test
	public void repositorySearch() {
		Response response = GET("/rest/github/search/socialestates/1");
		this.basicTest(response);
	}

	/**
	 * Coders impact.
	 */
	@Test
	public void codersImpact() {
		Response response = GET("/rest/github/repository/feliperazeek/socialestates/coderImpacts");
		this.basicTest(response);
	}

	/**
	 * Contributors.
	 */
	@Test
	public void contributors() {
		Response response = GET("/rest/github/repository/feliperazeek/socialestates/contributors");
		this.basicTest(response);
	}

	/**
	 * Commits.
	 */
	@Test
	public void commits() {
		Response response = GET("/rest/github/repository/feliperazeek/socialestates/commits");
		this.basicTest(response);
	}

	/**
	 * Basic test.
	 * 
	 * @param response
	 *            the response
	 */
	private void basicTest(Response response) {
		assertIsOk(response);
		assertContentType("application/json", response);
		assertCharset("utf-8", response);
	}

}
