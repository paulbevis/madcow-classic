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

/**
 * This plugin checks that the mapped field contains specified value. Like checkValue, but
 * it doesn't have to match the whole value.
 */
class CheckValueContains extends CheckValue {

    def invoke(AntBuilder antBuilder, Map pluginParameters) {
        if (!(pluginParameters.name && pluginParameters.type == "radio")) {
            pluginParameters.regex = 'true'
            pluginParameters.value = ".*${pluginParameters.value}.*"
        }
        super.invoke(antBuilder, pluginParameters)
    }
}
