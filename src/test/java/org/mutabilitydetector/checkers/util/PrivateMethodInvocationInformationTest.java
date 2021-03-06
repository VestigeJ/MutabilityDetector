package org.mutabilitydetector.checkers.util;

/*
 * #%L
 * MutabilityDetector
 * %%
 * Copyright (C) 2008 - 2014 Graham Allan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mutabilitydetector.checkers.CheckerRunner.ExceptionPolicy.FAIL_FAST;
import static org.mutabilitydetector.checkers.info.MethodIdentifier.forMethod;
import static org.mutabilitydetector.locations.Dotted.dotted;

import org.junit.Test;
import org.mutabilitydetector.TestUtil;
import org.mutabilitydetector.benchmarks.settermethod.ImmutableUsingPrivateFieldSettingMethod;
import org.mutabilitydetector.checkers.CheckerRunner;
import org.mutabilitydetector.checkers.info.InformationRetrievalRunner;
import org.mutabilitydetector.checkers.info.PrivateMethodInvocationInformation;

public class PrivateMethodInvocationInformationTest {

    private CheckerRunner checkerRunner = CheckerRunner.createWithCurrentClasspath(FAIL_FAST);

    private PrivateMethodInvocationInformation createInfo() {
        InformationRetrievalRunner sessionRunner = new InformationRetrievalRunner(TestUtil.testAnalysisSession(), checkerRunner);
        return new PrivateMethodInvocationInformation(sessionRunner);
    }

    @Test
    public void returnsTrueForPrivateMethodCalledOnlyFromConstructor() throws Exception {
        String className = ImmutableUsingPrivateFieldSettingMethod.class.getName();
        String methodDescriptor = "setFields:()V";
        PrivateMethodInvocationInformation info = createInfo();
        boolean result = info.isOnlyCalledFromConstructor(forMethod(dotted(className), methodDescriptor));
        assertTrue("Result should be true for private method called only from constructor.", result);
    }

    @Test
    public void returnsFalseForPublicMethod() throws Exception {
        String className = ImmutableUsingPrivateFieldSettingMethod.class.getName();
        String methodDescriptor = "getField1:()I";
        PrivateMethodInvocationInformation info = createInfo();
        boolean result = info.isOnlyCalledFromConstructor(forMethod(dotted(className), methodDescriptor));
        assertFalse("Cannot guarantee a public method is called only from constructor.", result);
    }

}
