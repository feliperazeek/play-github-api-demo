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
package models;

import java.util.Date;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * The Class Search.
 */
@Entity
public class SearchHistory extends Model {

	/** The search term. */
	@Required
	public String searchTerm;

	/** The date. */
	public Date date = new Date();

	/**
	 * To String
	 * 
	 * @see play.db.jpa.JPABase#toString()
	 */
	@Override
	public String toString() {
		return this.searchTerm;
	}

}
