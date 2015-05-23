/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.agency;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.network.XDomainNetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;

/**
 * @author hhildebrand
 *
 */
@Table(name = "agency_location_authorization", schema = "ruleform")
@Entity
public class AgencyLocationAuthorization extends
        XDomainNetworkAuthorization<Agency, Location> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "from_parent")
    private Agency            fromParent;

    // bi-directional many-to-one association to AgencyProduct
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "to_parent")
    private Location          toParent;

    @Override
    public Agency getFromParent() {
        return fromParent;
    }

    @Override
    public Location getToParent() {
        return toParent;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFromParent(Agency fromParent) {
        this.fromParent = fromParent;
    }

    @Override
    public void setToParent(Location toParent) {
        this.toParent = toParent;
    }

}
