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
package mashup.fm.github.schema;

/**
 * The Class CoderImpact.
 */
public class CoderImpact extends BaseEntity {

	/** The user. */
	private String user;

	/** The commits. */
	private Integer commits;

	/** The total commits. */
	private Integer totalCommits;

	/**
	 * Instantiates a new coder impact.
	 * 
	 * @param user
	 *            the user
	 * @param commits
	 *            the commits
	 * @param totalCommits
	 *            the total commits
	 */
	public CoderImpact(String user, Integer commits, Integer totalCommits) {
		super();
		this.user = user;
		this.commits = commits;
		this.totalCommits = totalCommits;
	}

	/**
	 * Instantiates a new coder impact.
	 */
	public CoderImpact() {

	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *            the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the commits.
	 * 
	 * @return the commits
	 */
	public Integer getCommits() {
		return this.commits;
	}

	/**
	 * Sets the commits.
	 * 
	 * @param commits
	 *            the new commits
	 */
	public void setCommits(Integer commits) {
		this.commits = commits;
	}

	/**
	 * Gets the total commits.
	 * 
	 * @return the total commits
	 */
	public Integer getTotalCommits() {
		return this.totalCommits;
	}

	/**
	 * Sets the total commits.
	 * 
	 * @param totalCommits
	 *            the new total commits
	 */
	public void setTotalCommits(Integer totalCommits) {
		this.totalCommits = totalCommits;
	}

}
