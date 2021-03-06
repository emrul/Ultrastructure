/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.meta.workspace.dsl;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceLexer;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributeRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.AttributedExistentialRuleformContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ChildSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ConstraintContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.EdgeContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.FacetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ImportedWorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.MetaProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ParentSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.ProtocolContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.RelationshipPairContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SelfSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.SiblingSequencingsContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.StatusCodeSequencingSetContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceContext;
import com.chiralbehaviors.CoRE.workspace.dsl.WorkspaceParser.WorkspaceDefinitionContext;

/**
 * @author hhildebrand
 *
 */
public class WorkspacePresentation {

    public static WorkspaceContext getWorkspaceContext(InputStream source) throws IOException {
        WorkspaceLexer l = new WorkspaceLexer(new ANTLRInputStream(source));
        WorkspaceParser p = new WorkspaceParser(new CommonTokenStream(l));
        p.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer,
                                    Object offendingSymbol, int line,
                                    int charPositionInLine, String msg,
                                    RecognitionException e) {
                throw new IllegalStateException("failed to parse at line "
                                                + line + " due to " + msg, e);
            }
        });
        WorkspaceContext ctx = p.workspace();
        return ctx;
    }

    private final WorkspaceContext context;

    public WorkspacePresentation(InputStream workspaceResource) throws IOException {
        this(getWorkspaceContext(workspaceResource));
    }

    public WorkspacePresentation(WorkspaceContext context) {
        this.context = context;
    }

    public List<AttributedExistentialRuleformContext> getAgencies() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.agencies.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<FacetContext> getAgencyFacets() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        if (context.agencies.facets() == null) {
            return Collections.emptyList();
        }
        return context.agencies.facets()
                               .facet();
    }

    public List<EdgeContext> getAgencyNetworks() {
        if (context.agencies == null) {
            return Collections.emptyList();
        }
        if (context.agencies.edges() == null) {
            return Collections.emptyList();
        }
        return context.agencies.edges()
                               .edge();
    }

    public List<FacetContext> getAttributeFacets() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        if (context.attributes.facets() == null) {
            return Collections.emptyList();
        }
        return context.attributes.facets()
                                 .facet();
    }

    public List<EdgeContext> getAttributeNetworks() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        if (context.attributes.edges() == null) {
            return Collections.emptyList();
        }
        return context.attributes.edges()
                                 .edge();
    }

    public List<AttributeRuleformContext> getAttributes() {
        if (context.attributes == null) {
            return Collections.emptyList();
        }
        List<AttributeRuleformContext> ruleforms = context.attributes.attributeRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ChildSequencingContext> getChildSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        ChildSequencingsContext children = context.sequencingAuthorizations.childSequencings();
        return children == null ? Collections.emptyList()
                                : children.childSequencing();
    }

    public List<ImportedWorkspaceContext> getImports() {
        if (context.imports == null) {
            return Collections.emptyList();
        }
        return context.imports.importedWorkspace();
    }

    public List<EdgeContext> getInferences() {
        if (context.inferences == null) {
            return Collections.emptyList();
        }
        if (context.inferences.edge() == null) {
            return Collections.emptyList();
        }
        return context.inferences.edge();
    }

    public List<FacetContext> getIntervalFacets() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        if (context.intervals.facets() == null) {
            return Collections.emptyList();
        }
        return context.intervals.facets()
                                .facet();
    }

    public List<EdgeContext> getIntervalNetworks() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        if (context.intervals.edges() == null) {
            return Collections.emptyList();
        }
        return context.intervals.edges()
                                .edge();
    }

    public List<AttributedExistentialRuleformContext> getIntervals() {
        if (context.intervals == null) {
            return Collections.emptyList();
        }
        return context.intervals.attributedExistentialRuleform();
    }

    public List<FacetContext> getLocationFacets() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        if (context.locations.facets() == null) {
            return Collections.emptyList();
        }
        return context.locations.facets()
                                .facet();
    }

    public List<EdgeContext> getLocationNetworks() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        if (context.locations.edges() == null) {
            return Collections.emptyList();
        }
        return context.locations.edges()
                                .edge();
    }

    public List<AttributedExistentialRuleformContext> getLocations() {
        if (context.locations == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.locations.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<MetaProtocolContext> getMetaProtocols() {
        if (context.metaProtocols == null) {
            return Collections.emptyList();
        }
        return context.metaProtocols.metaProtocol();
    }

    public List<ParentSequencingContext> getParentSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        return context.sequencingAuthorizations.parentSequencings()
                                               .parentSequencing();
    }

    public List<FacetContext> getProductFacets() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        if (context.products.facets() == null) {
            return Collections.emptyList();
        }
        return context.products.facets()
                               .facet();
    }

    public List<EdgeContext> getProductNetworks() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        if (context.products.edges() == null) {
            return Collections.emptyList();
        }
        return context.products.edges()
                               .edge();
    }

    public List<AttributedExistentialRuleformContext> getProducts() {
        if (context.products == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.products.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<ProtocolContext> getProtocols() {
        if (context.protocols == null) {
            return Collections.emptyList();
        }
        return context.protocols.protocol();
    }

    public List<FacetContext> getRelationshipFacets() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
        if (context.relationships.facets() == null) {
            return Collections.emptyList();
        }
        return context.relationships.facets()
                                    .facet();
    }

    public List<EdgeContext> getRelationshipNetworks() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
        if (context.relationships.edges() == null) {
            return Collections.emptyList();
        }
        return context.relationships.edges()
                                    .edge();
    }

    public List<RelationshipPairContext> getRelationships() {
        if (context.relationships == null) {
            return Collections.emptyList();
        }
        return context.relationships.relationshipPair();
    }

    public List<SelfSequencingContext> getSelfSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        SelfSequencingsContext selfSequencings = context.sequencingAuthorizations.selfSequencings();
        return selfSequencings == null ? Collections.emptyList()
                                       : selfSequencings.selfSequencing();
    }

    public List<SiblingSequencingContext> getSiblingSequencings() {
        if (context.sequencingAuthorizations == null) {
            return Collections.emptyList();
        }
        SiblingSequencingsContext siblings = context.sequencingAuthorizations.siblingSequencings();
        return siblings == null ? Collections.emptyList()
                                : siblings.siblingSequencing();
    }

    public List<FacetContext> getStatusCodeFacets() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        if (context.statusCodes.facets() == null) {
            return Collections.emptyList();
        }
        return context.statusCodes.facets()
                                  .facet();
    }

    public List<EdgeContext> getStatusCodeNetworks() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        if (context.statusCodes.edges() == null) {
            return Collections.emptyList();
        }
        return context.statusCodes.edges()
                                  .edge();
    }

    public List<AttributedExistentialRuleformContext> getStatusCodes() {
        if (context.statusCodes == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.statusCodes.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public List<StatusCodeSequencingSetContext> getStatusCodeSequencings() {
        if (context.statusCodeSequencings == null) {
            return Collections.emptyList();
        }
        return context.statusCodeSequencings.statusCodeSequencingSet();
    }

    public List<FacetContext> getUnitFacets() {
        if (context.units == null) {
            return Collections.emptyList();
        }
        if (context.units.facets() == null) {
            return Collections.emptyList();
        }
        return context.units.facets()
                            .facet();
    }

    public List<EdgeContext> getUnitNetworks() {
        if (context.units == null) {
            return Collections.emptyList();
        }
        if (context.units.edges() == null) {
            return Collections.emptyList();
        }
        return context.units.edges()
                            .edge();
    }

    public List<AttributedExistentialRuleformContext> getUnits() {
        if (context.units == null) {
            return Collections.emptyList();
        }
        List<AttributedExistentialRuleformContext> ruleforms = context.units.attributedExistentialRuleform();
        return ruleforms == null ? Collections.emptyList() : ruleforms;
    }

    public WorkspaceDefinitionContext getWorkspaceDefinition() {
        return context.definition;
    }

    public static String toFieldName(String name) {
        return Introspector.decapitalize(toValidName(name));
    }

    public static String toTypeName(String name) {
        char chars[] = toValidName(name).toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String toValidName(String name) {
        name = name.replaceAll("\\s", "");
        StringBuilder sb = new StringBuilder();
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            sb.append("_");
        }
        for (char c : name.toCharArray()) {
            if (!Character.isJavaIdentifierPart(c)) {
                sb.append("_");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String networkAuthNameOf(ConstraintContext constraint) {
        String name;
        if (constraint.name != null) {
            name = constraint.name.getText();
        } else if (constraint.anyType != null) {
            name = constraint.childRelationship.member.getText();
        } else if (constraint.methodType != null) {
            switch (constraint.methodType.getText()) {
                case "named by relationship":
                    name = constraint.childRelationship.member.getText();
                    break;
                case "named by entity":
                    name = constraint.authorizedParent.member.getText();
                    break;
                default:
                    throw new IllegalStateException(String.format("Invalid syntax for network authorization name: %s",
                                                                  constraint.methodType.getText()));
            }
        } else {
            name = constraint.authorizedParent.member.getText();
        }
        return name;
    }

    public static String stripQuotes(String original) {
        return original.substring(1, original.length() - 1);
    }
}
