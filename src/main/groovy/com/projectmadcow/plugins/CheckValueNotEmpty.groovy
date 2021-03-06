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

package com.projectmadcow.plugins

import com.projectmadcow.engine.plugin.Plugin

/**
 * CheckValueNotEmpty - i.e. a non-blank field. Attribute/field needs to exist and be non-blank.
 *
 * @author mcallon
 */
public class CheckValueNotEmpty extends Plugin {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {

        // regex to check value not empty (including blank)
        pluginParameters.text = "(.+)" // works as is for blank fields (maybe WebTest automatically does a trim)
        pluginParameters.regex = true

        LOG.debug("-- CHECK VALUE NOT EMPTY ------------- pluginParameters : $pluginParameters")

        if (pluginParameters.htmlId != null) {
            antBuilder.verifyElementText(pluginParameters)
        } else if (pluginParameters.xpath != null) {
            antBuilder.verifyXPath(pluginParameters)
        } else if (pluginParameters.name != null) {
            if (pluginParameters.type == "radio") {
                pluginParameters.remove 'type'
                antBuilder.verifyRadioButton(pluginParameters)
            } else if (pluginParameters.type == "select") {
                pluginParameters.remove 'type'
                antBuilder.verifySelectField(pluginParameters)
            } else if (pluginParameters.type == "input") {
                antBuilder.verifyElementText(pluginParameters)
            } else if (pluginParameters.type == null) {
                // assume implicit type of input
                pluginParameters.type = "input"
                antBuilder.verifyElementText(pluginParameters)
            } else {
                LOG.error("invoke() name reference - UNIMPLEMENTED type pluginParameter: " + pluginParameters)
                assert false: "CheckValueNotEmpty name reference - UNIMPLEMENTED type pluginParameter: " + pluginParameters
            }
        } else if (pluginParameters.forLabel) {
            pluginParameters.xpath = '//input[@label=\'' + pluginParameters.forLabel + '\']/@value'
            pluginParameters.remove 'forLabel'
            antBuilder.verifyXPath(pluginParameters)
        } else {
            LOG.error("invoke() - UNKNOWN pluginParameter: " + pluginParameters)
            assert false: "CheckValueNotEmpty - UNKNOWN pluginParameter: " + pluginParameters
        }
    }

}
