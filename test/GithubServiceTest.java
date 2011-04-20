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
import java.util.List;

import junit.framework.Assert;
import mashup.fm.github.GithubFactory;
import mashup.fm.github.schema.CoderImpact;
import mashup.fm.github.schema.Commit;
import mashup.fm.github.schema.Repository;
import mashup.fm.github.schema.User;

import org.junit.Test;

import play.Logger;
import play.test.UnitTest;

/**
 * The Class GithubClientTest.
 */
public class GithubServiceTest extends UnitTest {

	/**
	 * Search test.
	 */
	@Test
	public void searchTest() {
		List<Repository> repositories = GithubFactory.getService().search("play framework", 1);
		Assert.assertNotNull(repositories);
		Assert.assertTrue(repositories.size() > 0);
		Logger.info("Search: %s", repositories);
	}

	/**
	 * Repository test.
	 */
	@Test
	public void repositoryTest() {
		Object repos = GithubFactory.getService().repository("mashup-fm", "playframework-oauthprovider");
		Assert.assertNotNull(repos);
		Logger.info("Repository: %s", repos);
	}

	/**
	 * User test.
	 */
	public void userTest() {
		Object user = GithubFactory.getService().user("feliperazeek");
		Assert.assertNotNull(user);
		Logger.info("User: %s", user);
	}

	/**
	 * Contributors test.
	 */
	@Test
	public void contributorsTest() {
		List<User> contribs = GithubFactory.getService().contributors("feliperazeek", "socialestates");
		Assert.assertNotNull(contribs);
		Assert.assertTrue(contribs.size() > 0);
		Logger.info("Contributors: %s", contribs);
	}

	/**
	 * Commits test.
	 */
	@Test
	public void commitsTest() {
		List<Commit> commits = GithubFactory.getService().commits("mashup-fm", "playframework-oauthprovider");
		Assert.assertNotNull(commits);
		Assert.assertTrue(commits.size() > 0);
		Logger.info("Commits: %s", commits);
	}

	/**
	 * Coder impacts test.
	 */
	@Test
	public void coderImpactsTest() {
		// Define Input
		String user = "playframework";
		String repository = "play";

		// Get Commits
		List<Commit> commits = GithubFactory.getService().commits(user, repository);

		// Make sure commits were retrieved correctly
		Assert.assertNotNull(commits);
		Assert.assertTrue(commits.size() > 0);

		// Get Impacts by Coder
		List<CoderImpact> impacts = GithubFactory.getService().coderImpacts(user, repository);

		// Make sure data was retrieved correctly
		Assert.assertNotNull(impacts);
		Assert.assertTrue(impacts.size() > 0);

		// Check values of each items in the list
		int total = 0;
		for (CoderImpact impact : impacts) {
			// Add total
			total = total + impact.getCommits();

			// Make sure total commits from each item matches total from commits
			// list
			Assert.assertTrue(commits.size() == impact.getTotalCommits());
		}

		// Make sure the sum of commits from each coder matches total number of
		// commits
		Assert.assertTrue(commits.size() == total);
	}

}
