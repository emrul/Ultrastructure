/** 
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
 */

package com.chiralbehaviors.CoRE.phantasm.authentication;

import static com.chiralbehaviors.CoRE.jooq.Tables.FACET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.chiralbehaviors.CoRE.jooq.enums.ExistentialDomain;
import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialAttributeRecord;
import com.chiralbehaviors.CoRE.jooq.tables.records.FacetRecord;
import com.chiralbehaviors.CoRE.kernel.phantasm.agency.CoreUser;
import com.chiralbehaviors.CoRE.meta.models.AbstractModelTest;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource;
import com.chiralbehaviors.CoRE.phantasm.resources.AuthxResource.CapabilityRequest;
import com.chiralbehaviors.CoRE.security.AuthorizedPrincipal;
import com.chiralbehaviors.CoRE.security.Credential;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import io.dropwizard.auth.basic.BasicCredentials;

/**
 * @author hhildebrand
 * 
 */
public class AuthenticatorsTest extends AbstractModelTest {
    @Test
    public void testBasic() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);

        model.flush();
        AgencyBasicAuthenticator authenticator = new AgencyBasicAuthenticator(model);
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(new BasicCredentials(username,
                                                                                                      password));
        assertTrue(authenticated.isPresent());
        assertEquals(bob.getRuleform()
                        .getId(),
                     authenticated.get()
                                  .getPrincipal()
                                  .getId());
    }

    @Test
    public void testBearerToken() throws Exception {
        String username = "bob@slack.com";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        bob.setLogin(username);
        Credential credential = new Credential();
        credential.ip = "No place like 127.00.1";
        List<UUID> capabilities = Arrays.asList(model.getPhantasmModel()
                                                     .getFacetDeclaration(model.getKernel()
                                                                               .getIsA(),
                                                                          model.getKernel()
                                                                               .getCoreUser())
                                                     .getId());
        credential.capabilities = capabilities;
        ExistentialAttributeRecord accessToken = model.records()
                                                      .newExistentialAttribute();
        accessToken.setAttribute(model.getKernel()
                                      .getAccessToken()
                                      .getId());
        accessToken.setExistential(bob.getRuleform()
                                      .getId());
        accessToken.setJsonValue(new ObjectMapper().valueToTree(credential));
        accessToken.insert();
        model.flush();

        AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator(model);
        RequestCredentials requestCredentials = new RequestCredentials(credential.ip,
                                                                       accessToken.getId()
                                                                                  .toString());
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(requestCredentials);
        assertTrue(authenticated.isPresent());
        AuthorizedPrincipal authBob = authenticated.get();
        assertEquals(bob, authBob.getPrincipal());
        assertEquals(1, authBob.getAsserted()
                               .size());
        FacetRecord asserted = model.create()
                                    .selectFrom(FACET)
                                    .where(FACET.ID.equal(authBob.getAsserted()
                                                                 .get(0)))
                                    .fetchOne();
        assertEquals(model.getKernel()
                          .getIsA(),
                     asserted.getClassifier());
        assertEquals(model.getKernel()
                          .getCoreUser(),
                     asserted.getClassification());

        requestCredentials = new RequestCredentials("No place like HOME",
                                                    accessToken.getId()
                                                               .toString());
        authenticated = authenticator.authenticate(requestCredentials);
        assertFalse(authenticated.isPresent());

        requestCredentials = new RequestCredentials(credential.ip,
                                                    accessToken.getId()
                                                               .toString());
        authenticated = authenticator.authenticate(requestCredentials);
        assertFalse(authenticated.isPresent());

        requestCredentials = new RequestCredentials(credential.ip,
                                                    "No place like HOME");
        authenticated = authenticator.authenticate(requestCredentials);
        assertFalse(authenticated.isPresent());
    }

    @Test
    public void testAuthRoundTrip() throws Exception {
        String username = "bob@slack.com";
        String password = "give me food or give me slack or kill me";
        CoreUser bob = (CoreUser) model.construct(CoreUser.class,
                                                  ExistentialDomain.Agency,
                                                  "Bob", "Test Dummy");
        bob.setLogin(username);
        bob.setPasswordRounds(10);
        AgencyBasicAuthenticator.resetPassword(bob, password);

        model.flush();
        AuthxResource authx = new AuthxResource(model.create());
        HttpServletRequest request = mock(HttpServletRequest.class);
        String ip = "There's no place like 127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(ip);
        UUID authToken = authx.loginForToken(username, password, request,
                                             model.create());
        assertNotNull(authToken);

        AgencyBearerTokenAuthenticator authenticator = new AgencyBearerTokenAuthenticator(model);
        RequestCredentials credential = new RequestCredentials(ip,
                                                               authToken.toString());
        Optional<AuthorizedPrincipal> authenticated = authenticator.authenticate(credential);
        assertTrue(authenticated.isPresent());
        AuthorizedPrincipal authBob = authenticated.get();
        assertEquals(bob, authBob.getPrincipal());
        FacetRecord asserted = model.getPhantasmModel()
                                    .getFacetDeclaration(model.getKernel()
                                                              .getIsA(),
                                                         model.getKernel()
                                                              .getCoreUser());

        CapabilityRequest capRequest = new CapabilityRequest();
        capRequest.username = username;
        capRequest.password = password;
        capRequest.capabilities = Arrays.asList(asserted.getId());
        authToken = authx.requestCapability(capRequest, request,
                                            model.create());

        credential = new RequestCredentials(ip, authToken.toString());
        authenticated = authenticator.authenticate(credential);
        assertTrue(authenticated.isPresent());
        authBob = authenticated.get();

        assertEquals(bob, authBob.getPrincipal());
        assertEquals(1, authBob.getCapabilities()
                               .size());
        assertEquals(asserted, authBob.getCapabilities()
                                      .get(0));

        assertEquals(2, authBob.getCapabilities()
                               .size());
        assertEquals(bob.getRuleform(), authBob.getCapabilities()
                                               .get(0));
        assertEquals(model.getKernel()
                          .getCoreUser(),
                     authBob.getCapabilities()
                            .get(1));
    }
}
