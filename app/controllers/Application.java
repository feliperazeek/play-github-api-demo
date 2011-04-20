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

import java.util.ArrayList;
import java.util.List;

import mashup.fm.github.GithubFactory;
import mashup.fm.github.GithubService;
import mashup.fm.github.schema.CoderImpact;
import mashup.fm.github.schema.Commit;
import mashup.fm.github.schema.Repository;
import mashup.fm.github.schema.User;
import models.SearchHistory;
import play.Logger;
import play.mvc.Controller;
import util.ExceptionUtil;

/**
 * The Class Application.
 */
public class Application extends Controller {

	/**
	 * Index.
	 */
	public static void index() {
		render();
	}

	/**
	 * Search form.
	 */
	public static void searchForm() {
		List<SearchHistory> latestSearches = getService().latestSearches();
		render(latestSearches);
	}

	/**
	 * Repository.
	 * 
	 * @param userName
	 *            the user name
	 * @param repositoryName
	 *            the repository name
	 */
	public static void repository(String userName, String repositoryName) {
		// Repository
		Repository repository = new Repository();
		try {
			// Hit Service
			Repository data = getService().repository(userName, repositoryName);

			// Only set value to variable that will be bound to view if not null
			// (billion dollar mistake)
			if (data != null) {
				repository = data;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Contributors
		List<User> contributors = new ArrayList<User>();
		try {
			// Hit Service
			List<User> data = getService().contributors(userName, repositoryName);

			// Only set value to variable that will be bound to view if not null
			// (billion dollar mistake)
			if (data != null) {
				contributors = data;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Commits
		List<Commit> commits = new ArrayList<Commit>();
		try {
			// Hit Service
			List<Commit> data = getService().commits(userName, repositoryName);

			// Only set value to variable that will be bound to view if not null
			// (billion dollar mistake)
			if (data != null) {
				commits = data;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// User
		User user = new User();
		try {
			// Hit Service
			User data = getService().user(userName);

			// Only set value to variable that will be bound to view if not null
			// (billion dollar mistake)
			if (data != null) {
				user = data;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Coder Impacts
		List<CoderImpact> coderImpacts = new ArrayList<CoderImpact>();
		try {
			// Hit Service
			List<CoderImpact> data = getService().coderImpacts(userName, repositoryName);

			// Only set value to variable that will be bound to view if not null
			// (billion dollar mistake)
			if (data != null) {
				coderImpacts = data;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Render View
		render(repository, contributors, commits, user, coderImpacts, userName, repositoryName);
	}

	/**
	 * Search.
	 * 
	 * @param q
	 *            the q
	 * @param startPage
	 *            the start page
	 */
	public static void search(String q, int startPage) {
		// Cleanup Input
		q = cleanupInput(q);

		// Make sure it starts with page 1
		if (startPage < 1) {
			startPage = 1;
		}

		// Get Data
		List<Repository> repositories = new ArrayList<Repository>();
		try {
			// Hit Remote Service
			List<Repository> list = getService().search(q, startPage);

			// Only set to variable that will be bound if it's not null
			if (list != null) {
				repositories = list;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Log Debug
		Logger.info("Results Found: %s", repositories.size());

		// Previous Page
		int previousPage = startPage - 1;
		boolean hasPreviousPage = false;
		if ((startPage > 1) && (repositories.size() > 0)) {
			hasPreviousPage = true;
		}

		// Next Page
		int nextPage = startPage + 1;
		boolean hasNextPage = false;
		if (repositories.size() >= 100) {
			hasNextPage = true;
		}

		// Render View
		render(q, repositories, startPage, previousPage, hasPreviousPage, nextPage, hasNextPage);
	}

	/**
	 * User.
	 * 
	 * @param userName
	 *            the user name
	 */
	public static void user(String userName) {
		// Cleanup Input
		userName = cleanupInput(userName);

		// Init User
		User user = new User();

		// Get Data
		try {
			// Hit Service
			User data = getService().user(userName);

			// Only set to variable that will be bound if not null - I hate null
			// hell (the famous billion dollar mistake)
			if (data != null) {
				user = data;
			}

		} catch (Throwable t) {
			// Log Exception
			Logger.error(ExceptionUtil.getStackTrace(t));
		}
		render(user, userName);
	}

	/**
	 * Cleanup input.
	 * 
	 * @param value
	 *            the value
	 * @return the string
	 */
	private static String cleanupInput(String value) {
		if (value == null) {
			value = "";
		}
		if (value.startsWith("Example: ")) {
			value = value.replaceAll("Example: ", "");
		}
		return value;
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	private static GithubService getService() {
		return GithubFactory.getService();
	}

}