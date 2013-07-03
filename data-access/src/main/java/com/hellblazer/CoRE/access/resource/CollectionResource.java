/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.hellblazer.CoRE.access.resource;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.hellblazer.CoRE.Ruleform;

/**
 * A REST resource for processing atomic transactions full of multiple objects of many
 * types. 
 * @author hparry
 *
 */
@Path("/v{version : \\d+}/services/data/collection")
public class CollectionResource {
	
	/**
	 * @param emf
	 */
	public CollectionResource(EntityManagerFactory emf) {
	}

	@GET
	@Path("/")
	public Response get() {
		return Response.status(403).build();
	}
	
	@POST
	@Path("/")
	public Response post(Ruleform[] ruleforms) {
		return null;
	}

}
