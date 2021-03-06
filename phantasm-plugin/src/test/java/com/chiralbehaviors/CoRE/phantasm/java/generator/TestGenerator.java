/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
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

package com.chiralbehaviors.CoRE.phantasm.java.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

import com.chiralbehaviors.CoRE.phantasm.generator.plugin.Generator;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 *
 */
public class TestGenerator {
    private static final String COM_CHIRALBEHAVIORS_CO_RE_PHANTASM_TEST_GENERATED = "com.chiralbehaviors.CoRE.phantasm.test.generated";
    private static final String TARGET_PHANTASM_TEST_GENERATION                   = "target/phantasm-test-generation";
    private static final String THING_WSP                                         = "/thing.wsp";

    @Test
    public void testFile() throws IOException, MojoExecutionException,
                           MojoFailureException {
        Configuration configuration = new Configuration();
        configuration.resource = "target/thing.wsp.tst";
        FileOutputStream fos = new FileOutputStream(configuration.resource);
        Utils.copy(getClass().getResourceAsStream(THING_WSP), fos);
        fos.close();
        configuration.appendTypeToPackage = true;
        configuration.outputDirectory = new File(TARGET_PHANTASM_TEST_GENERATION);
        configuration.packageName = COM_CHIRALBEHAVIORS_CO_RE_PHANTASM_TEST_GENERATED;
        new Generator(configuration).execute();
    }

    @Test
    public void testResource() throws IOException {
        Configuration configuration = new Configuration();
        configuration.resource = THING_WSP;
        configuration.appendTypeToPackage = true;
        configuration.outputDirectory = new File(TARGET_PHANTASM_TEST_GENERATION);
        configuration.packageName = COM_CHIRALBEHAVIORS_CO_RE_PHANTASM_TEST_GENERATED;
        PhantasmGenerator generator = new PhantasmGenerator(configuration);
        generator.generate();
    }
}
