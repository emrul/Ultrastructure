/*
 * This file was automatically generated by EvoSuite
 * Fri Apr 08 20:11:47 GMT 2016
 */

package com.chiralbehaviors.CoRE.utils;

import static org.evosuite.runtime.EvoAssertions.assertThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.PSQLException;

@RunWith(EvoRunner.class)
@EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true)
public class DbaConfiguration_ESTest
        extends DbaConfiguration_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void test0() throws Throwable {
        DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
        dbaConfiguration0.coreUsername = "G?[*C>l}";
        String string0 = dbaConfiguration0.toString();
        assertEquals("DbaConfiguration [contexts=local, coreDb=core, corePassword=:: undefined :: , corePort=0, coreServer=null, coreUsername=G?[*C>l}, dbaDb=postgres, dbaPassword=:: undefined :: , dbaPort=0, dbaServer=null, dbaUsername=null, dropDatabase=false]",
                     string0);
    }

    @Test(timeout = 4000)
    public void test1() throws Throwable {
        DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
        dbaConfiguration0.dbaServer = "UU#Mt4ls,\"";
        String string0 = dbaConfiguration0.toString();
        assertEquals("DbaConfiguration [contexts=local, coreDb=core, corePassword=:: undefined :: , corePort=0, coreServer=null, coreUsername=null, dbaDb=postgres, dbaPassword=:: undefined :: , dbaPort=0, dbaServer=UU#Mt4ls,\", dbaUsername=null, dropDatabase=false]",
                     string0);
    }

    @Test(timeout = 4000)
    public void test2() throws Throwable {
        DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
        dbaConfiguration0.coreServer = "!lVw+3&Q{bB";
        String string0 = dbaConfiguration0.toString();
        assertEquals("DbaConfiguration [contexts=local, coreDb=core, corePassword=:: undefined :: , corePort=0, coreServer=!lVw+3&Q{bB, coreUsername=null, dbaDb=postgres, dbaPassword=:: undefined :: , dbaPort=0, dbaServer=null, dbaUsername=null, dropDatabase=false]",
                     string0);
    }

    @Test(timeout = 4000)
    public void test3() throws Throwable {
        DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
        dbaConfiguration0.dbaPort = -2056;
        try {
            dbaConfiguration0.getDbaConnection();
            fail("Expecting exception: PSQLException");

        } catch (PSQLException e) {
            //
            // Something unusual has occurred to cause the driver to fail. Please report this exception.
            //
            assertThrownBy("org.postgresql.Driver", e);
        }
    }

    @Test(timeout = 4000)
    public void test4() throws Throwable {
        Future<?> future = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
                    dbaConfiguration0.dbaServer = "UU#Mt4ls,\"";
                    try {
                        dbaConfiguration0.getDbaConnection();
                        fail("Expecting exception: PSQLException");

                    } catch (PSQLException e) {
                        //
                        // Something unusual has occurred to cause the driver to fail. Please report this exception.
                        //
                        assertThrownBy("org.postgresql.Driver", e);
                    }
                } catch (Throwable t) {
                    // Need to catch declared exceptions
                }
            }
        });
        future.get(4000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 4000)
    public void test5() throws Throwable {
        DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
        dbaConfiguration0.dbaPassword = "postgres";
        String string0 = dbaConfiguration0.toString();
        assertEquals("DbaConfiguration [contexts=local, coreDb=core, corePassword=:: undefined :: , corePort=0, coreServer=null, coreUsername=null, dbaDb=postgres, dbaPassword=**********, dbaPort=0, dbaServer=null, dbaUsername=null, dropDatabase=false]",
                     string0);
    }

    @Test(timeout = 4000)
    public void test6() throws Throwable {
        DbaConfiguration dbaConfiguration0 = new DbaConfiguration();
        dbaConfiguration0.corePassword = "(%.8X6*C";
        String string0 = dbaConfiguration0.toString();
        assertEquals("DbaConfiguration [contexts=local, coreDb=core, corePassword=**********, corePort=0, coreServer=null, coreUsername=null, dbaDb=postgres, dbaPassword=:: undefined :: , dbaPort=0, dbaServer=null, dbaUsername=null, dropDatabase=false]",
                     string0);
    }
}
