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
package mashup.fm.github.comparator;

import java.util.Comparator;

import mashup.fm.github.schema.CoderImpact;

/**
 * The Class CoderImpactComparator.
 */
public class CoderImpactComparator implements Comparator<CoderImpact> {

	/**
	 * Sort by No. of Commits
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CoderImpact arg0, CoderImpact arg1) {
		if ((arg0 != null) && (arg1 != null)) {
			return arg0.getCommits().compareTo(arg1.getCommits());
		}
		return 0;
	}

}
