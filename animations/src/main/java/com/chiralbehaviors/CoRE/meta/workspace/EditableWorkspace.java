/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.workspace;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.ChildSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialNetworkRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobChronologyRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.JobRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.MetaProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.NetworkInferenceRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ParentSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.ProtocolRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SelfSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.SiblingSequencingAuthorizationRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.StatusCodeSequencingRecord;

/**
 * @author hhildebrand
 *
 */
public interface EditableWorkspace extends WorkspaceAccessor {

    void add(ChildSequencingAuthorizationRecord ruleform);

    void add(ExistentialAttributeAuthorizationRecord ruleform);

    void add(ExistentialAttributeRecord ruleform);

    void add(ExistentialNetworkAttributeAuthorizationRecord ruleform);

    void add(ExistentialNetworkAttributeRecord ruleform);

    void add(ExistentialNetworkAuthorizationRecord ruleform);

    void add(ExistentialNetworkRecord ruleform);

    void add(ExistentialRecord ruleform);

    void add(FacetRecord ruleform);

    void add(JobChronologyRecord ruleform);

    void add(JobRecord ruleform);

    void add(MetaProtocolRecord ruleform);

    void add(NetworkInferenceRecord ruleform);

    void add(ParentSequencingAuthorizationRecord ruleform);

    void add(ProtocolRecord ruleform);

    void add(SelfSequencingAuthorizationRecord ruleform);

    void add(SiblingSequencingAuthorizationRecord ruleform);

    void add(StatusCodeSequencingRecord ruleform);

    void addImport(String namespace, Product workspace);

    void put(String key, ChildSequencingAuthorizationRecord ruleform);

    void put(String key, ExistentialAttributeAuthorizationRecord ruleform);

    void put(String key, ExistentialAttributeRecord ruleform);

    void put(String key,
             ExistentialNetworkAttributeAuthorizationRecord ruleform);

    void put(String key, ExistentialNetworkAttributeRecord ruleform);

    void put(String key, ExistentialNetworkAuthorizationRecord ruleform);

    void put(String key, ExistentialNetworkRecord ruleform);

    void put(String key, ExistentialRecord ruleform);

    void put(String key, FacetRecord ruleform);

    void put(String key, JobChronologyRecord ruleform);

    void put(String key, JobRecord ruleform);

    void put(String key, MetaProtocolRecord ruleform);

    void put(String key, NetworkInferenceRecord ruleform);

    void put(String key, ParentSequencingAuthorizationRecord ruleform);

    void put(String key, ProtocolRecord ruleform);

    void put(String key, SelfSequencingAuthorizationRecord ruleform);

    void put(String key, SiblingSequencingAuthorizationRecord ruleform);

    void put(String key, StatusCodeSequencingRecord ruleform);

    void removeImport(Product workspace, Agency updatedBy);
}
