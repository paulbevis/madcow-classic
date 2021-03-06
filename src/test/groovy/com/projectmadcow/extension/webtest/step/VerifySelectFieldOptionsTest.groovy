/*
 * Copyright 2008-2011 4impact Technology Services, Brisbane, Australia
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.projectmadcow.extension.webtest.step;


import com.projectmadcow.engine.grass.ParseUtil
import com.canoo.webtest.engine.StepFailedException
import com.canoo.webtest.steps.Step

public class VerifySelectFieldOptionsTest extends AbstractXpathHtmlIdStepTest {

    final List options = [
        'Australan Captial Territory',
        'New South Wales',
        'Northern Territory',
        'Queensland',
        'South Australia',
        'Victoria',
        'Western Australia',
        'Western Australia'
    ].collect{it -> "'${it}'"}

    String html =
    """<html><body>
            <form><select id="state" name="state" label="State">
                <option>Australan Captial Territory</option>
                <option>New South Wales</option>
                <option>Northern Territory</option>
                <option selected>Queensland</option>
                <option>South Australia</option>
                <option>Victoria</option>
                <option>Western Australia</option>
                <option>Western Australia</option>
            </select></form>
       </body></html>"""

    VerifySelectFieldOptions fStep

    protected Step createStep() {
        return new VerifySelectFieldOptions()
    }

    protected void setUp() throws Exception {
        super.setUp()
        fStep = (VerifySelectFieldOptions) getStep()
        setCurrentPageHtml html
    }

    void setCurrentPageHtml(String html){
        getContext().setDefaultResponse(html)
    }

    private void testExpectStepFailedException(def htmlId, def options, def errorMessage){
        try {
            fStep.htmlId = htmlId
            fStep.options = options
            fStep.execute()
            fail 'expected StepFailedException'
        } catch (StepFailedException e){
            // success
            assert e.message.contains(errorMessage)
        }
    }

    void testMissingInTest(){
        testExpectStepFailedException "state", "['Queensland']",
        'Missing in Test: [Australan Captial Territory, New South Wales, Northern Territory, South Australia, Victoria, Western Australia, Western Australia]'
    }

    void testThatDuplicateOptionsCauseFailureIfNotSpecified() {
        testExpectStepFailedException "state", "['Australan Captial Territory','New South Wales','Northern Territory','Queensland','South Australia','Victoria','Western Australia']",
        'Missing in Test: [Western Australia]'
    }

    void testMissingInPage(){
        testExpectStepFailedException "state", "['QLD']", 'Missing in Page: [QLD]'
    }

    void testVerifySelectFieldOptions(){
        fStep.htmlId = 'state'
        fStep.options = ParseUtil.convertListToString(options)
        fStep.execute()
    }

    void testUnableToFindSelectElement(){
        try {
            fStep.htmlId = 'notTheSelectElementId'
            fStep.options = ParseUtil.convertListToString(options)
            fStep.execute()
        } catch (StepFailedException e){
            // success
            assert e.message.contains("No element found with id \"${fStep.htmlId}\"")
        }
    }
}
