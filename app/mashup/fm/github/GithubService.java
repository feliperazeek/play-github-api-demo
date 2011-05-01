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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import mashup.fm.github.comparator.CoderImpactComparator;
import mashup.fm.github.schema.CoderImpact;
import mashup.fm.github.schema.Commit;
import mashup.fm.github.schema.Repository;
import mashup.fm.github.schema.User;
import models.SearchHistory;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.cache.Cache;
import util.ExceptionUtil;

/**
 * The Class GithubClient.
 */
@Path("/github")
public class GithubService extends BaseService {

	/** The Constant searchPattern. */
	protected static final String searchPattern = "http://github.com/api/v2/json/repos/search/%s?start_page=%s";

	/** The Constant commitsPattern. */
	protected static final String commitsPattern = "http://github.com/api/v2/json/commits/list/%s/%s/master?page=%s";

	/** The Constant treePattern. */
	protected static final String treePattern = "http://github.com/api/v2/json/tree/full/%s/%s/%s";

	/** The Constant userCheckPattern. */
	protected static final String userCheckPattern = "http://github.com/api/v2/json/user/show/%s";

	/** The Constant repositoryCheckPattern. */
	protected static final String repositoryCheckPattern = "http://github.com/api/v2/json/repos/show/%s/%s";

	/** The Constant contributorsCheckPattern. */
	protected static final String contributorsCheckPattern = "http://github.com/api/v2/json/repos/show/%s/%s/contributors";

	/** The Constant useCache. */
	protected static final boolean useCache = true;

	/** The Constant cacheExpiration. */
	protected static final String cacheExpiration = "2h";

	/**
	 * Repository.
	 * 
	 * @param user
	 *            the user
	 * @param repository
	 *            the repository
	 * @return the json object
	 */
	@GET
	@Path("/repository/{user}/{repository}")
	@Produces("application/json")
	public Repository repository(@PathParam("user") String user, @PathParam("repository") String repository) {
		// Check Input
		this.checkInputParam("user", user);
		this.checkInputParam("repository", repository);

		// Check Cache If Needed
		String cacheKey = "repository_" + user + "_" + repository;
		try {
			if (useCache) {
				Repository cachedObject = Cache.get(cacheKey, Repository.class);
				if (cachedObject != null) {
					return cachedObject;
				}
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Cache Not Found - Hit Remote Service
		Repository results = asObject(Repository.class, "repository", repositoryCheckPattern, user, repository);

		// Set Cache
		try {
			Cache.set(cacheKey, results, cacheExpiration);
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Return Results
		return results;
	}

	/**
	 * User.
	 * 
	 * @param user
	 *            the user
	 * @return the json object
	 */
	@GET
	@Path("/user/{user}")
	@Produces("application/json")
	public User user(@PathParam("user") String user) {
		// Check Input
		this.checkInputParam("user", user);

		// Check Cache If Needed
		String cacheKey = "user_" + user;
		try {
			if (useCache) {
				User cachedObject = Cache.get(cacheKey, User.class);
				if (cachedObject != null) {
					return cachedObject;
				}
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Cache Not Found - Hit Remote Service
		User results = asObject(User.class, "user", userCheckPattern, user);

		// Set Cache
		try {
			Cache.set(cacheKey, results, cacheExpiration);
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Return Results
		return results;
	}

	/**
	 * Search.
	 * 
	 * @param q
	 *            the q
	 * @return the json array
	 */
	@GET
	@Path("/search/{q}/{startPage}")
	@Produces("application/json")
	public List<Repository> search(@PathParam("q") String q, @PathParam("startPage") int startPage) {
		// Check Input
		this.checkInputParam("q", q);

		// Make sure page index starts with 1 (required by Github API)
		if (startPage < 1) {
			startPage = 1;
		}

		// Check Cache If Needed
		String cacheKey = "search_" + q + "_" + startPage;
		try {
			if (useCache) {
				List<Repository> cachedObject = Cache.get(cacheKey, List.class);
				if (cachedObject != null) {
					return cachedObject;
				}
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Cache Not Found - Hit Remote Service
		List<Repository> results = asArray(Repository.class, "repositories", searchPattern, q, String.valueOf(startPage));

		// Set Cache
		try {
			Cache.set(cacheKey, results, cacheExpiration);
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Save Search
		try {
			this.saveSearchHistory(q);
		} catch (Throwable t) {
			// We don't want application failing because search history doesn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Return Results
		return results;
	}

	/**
	 * Coder impact.
	 * 
	 * @param user
	 *            the user
	 * @param repository
	 *            the repository
	 * @return the map
	 */
	@GET
	@Path("/repository/{user}/{repository}/coderImpacts")
	@Produces("application/json")
	public List<CoderImpact> coderImpacts(@PathParam("user") String user, @PathParam("repository") String repository) {
		// Check Input
		this.checkInputParam("user", user);
		this.checkInputParam("repository", repository);

		// Check Cache If Needed
		String cacheKey = "coderImpacts_" + user + "_" + repository;
		try {
			if (useCache) {
				List<CoderImpact> cachedObject = Cache.get(cacheKey, List.class);
				if (cachedObject != null) {
					return cachedObject;
				}
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Init map that will hold temporary values
		Map<String, Integer> map = new HashMap<String, Integer>();

		// Get last 100 commits
		List<Commit> commits = this.commits(user, repository);

		// Check list
		int max = 100;
		int count = 0;
		if ((commits != null) && (commits.size() > 0)) {
			// Loop on each commit
			for (Commit c : commits) {
				// Add Count
				count++;

				// If the map doesn't have an entry for the author, create one
				// with a zero value
				if (map.containsKey(c.getAuthor().getLogin()) == false) {
					map.put(c.getAuthor().getLogin(), 0);
				}

				// Add commit entry for the author
				Integer i = map.get(c.getAuthor().getLogin());
				i++;

				// Store value on temporary map
				map.put(c.getAuthor().getLogin(), i);

				// Check Max
				if (count == max) {
					break;
				}
			}
		}

		// Wrap values in list of impact
		List<CoderImpact> list = new ArrayList<CoderImpact>();
		for (Map.Entry<String, Integer> e : map.entrySet()) {
			list.add(new CoderImpact(e.getKey(), e.getValue(), count));
		}

		// Sort
		Collections.sort(list, new CoderImpactComparator());

		// Set Cache
		try {
			Cache.set(cacheKey, list, cacheExpiration);
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Return list
		return list;
	}

	/**
	 * Commits.
	 * 
	 * @param user
	 *            the user
	 * @param repository
	 *            the repository
	 * @return the json array
	 */
	@GET
	@Path("/repository/{user}/{repository}/commits")
	@Produces("application/json")
	public List<Commit> commits(@PathParam("user") String user, @PathParam("repository") String repository) {
		// Check Input
		this.checkInputParam("user", user);
		this.checkInputParam("repository", repository);

		// Check Cache If Needed
		String cacheKey = "repository_commits_" + user + "_" + repository;
		try {
			if (useCache) {
				List<Commit> cachedObject = Cache.get(cacheKey, List.class);
				if (cachedObject != null) {
					return cachedObject;
				}
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Cache Not Found - Hit Remote Service
		List<Commit> results = new ArrayList<Commit>();
		Integer page = 1;
		while (results.size() < 100) {
			try {
				List<Commit> cs = asArray(Commit.class, "commits", commitsPattern, user, repository, String.valueOf(page));
				if ((cs != null) && (cs.size() > 0)) {
					results.addAll(cs);
				} else {
					break;
				}
			} catch (Throwable t) {
				Logger.warn(ExceptionUtil.getStackTrace(t));
				break;
			}
			page++;
		}

		// Set Cache
		try {
			Cache.set(cacheKey, results, cacheExpiration);
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Return Results
		return results;
	}

	/**
	 * Contributors.
	 * 
	 * @param user
	 *            the user
	 * @param repository
	 *            the repository
	 * @return the json array
	 */
	@GET
	@Path("/repository/{user}/{repository}/contributors")
	@Produces("application/json")
	public List<User> contributors(@PathParam("user") String user, @PathParam("repository") String repository) {
		// Check Cache If Needed
		String cacheKey = "contributors_" + user + "_" + repository;
		try {
			if (useCache) {
				List<User> cachedObject = Cache.get(cacheKey, List.class);
				if (cachedObject != null) {
					return cachedObject;
				}
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Cache Remote Service - Hit Remote Services
		List<User> results = asArray(User.class, "contributors", contributorsCheckPattern, user, repository);

		// Set Cache
		try {
			Cache.set(cacheKey, results, cacheExpiration);
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}

		// Return Results
		return results;
	}

	/**
	 * Latest searches.
	 * 
	 * @return the list
	 */
	public List<SearchHistory> latestSearches() {
		List<SearchHistory> list = new ArrayList<SearchHistory>();
		try {
			List<SearchHistory> data = SearchHistory.find("order by date desc").fetch(20);
			if (data != null) {
				list = data;
			}
		} catch (Throwable t) {
			// We don't want application failing because cache lookup didn't
			// work
			Logger.error(ExceptionUtil.getStackTrace(t));
		}
		return list;
	}

	/**
	 * Save search history.
	 * 
	 * @param searchTerm
	 *            the search term
	 */
	protected void saveSearchHistory(String searchTerm) {
		if (StringUtils.isNotBlank(searchTerm)) {
			SearchHistory s = new SearchHistory();
			s.date = new Date();
			s.searchTerm = searchTerm;
			s.save();
		} else {
			Logger.warn("Cannot save search history for invalid term: %s", searchTerm);
		}
	}
}