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

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Element

/**
 * Test class for the Table plugin.
 */
public class TableTest extends AbstractPluginTestCase {

    Table tablePlugin
    String html
    Element htmlAsDocumentElement
    XPath xpath

    void setUp() {
        super.setUp()

        html = """<html><body>
                        <table id="searchResults">
                            <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>Address Line 1</th>
                                    <th>Address Line 2</th>
                                    <th>Suburb</th>
                                    <th>State</th>
                                    <th>Postcode</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><a href="/madcow-test-site/address/show/1">1</a></td>
                                    <td>one</td>
                                    <td>1</td>
                                    <td>TENERIFFE</td>
                                    <td>Queensland</td>
                                    <td>4005</td>
                                </tr>
                                <tr>
                                    <td><a href="/madcow-test-site/address/show/2">2</a></td>
                                    <td>320 Adelaide St</td>
                                    <td></td>
                                    <td>BRISBANE</td>
                                    <td>Queensland</td>
                                    <td>4000</td>
                                </tr>
                            </tbody>
                        </table>
                       </body></html>"""
        contextStub.setDefaultResponse(html)

        def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        htmlAsDocumentElement = builder.parse(new ByteArrayInputStream(html.bytes)).documentElement
        xpath = XPathFactory.newInstance().newXPath()

        tablePlugin = (new Table()).invoke(antBuilder, [htmlId : 'searchResults'])
        tablePlugin.callingProperty = 'searchResults'
    }

    void testColumnPositionXPath() {
        Closure checkPosition = { String column, String position ->
            String columnPositionXPath = tablePlugin.getColumnPositionXPath(column)
            assert xpath.evaluate(columnPositionXPath, htmlAsDocumentElement, XPathConstants.STRING)[0] == position
        }

        checkPosition.call('Id', '1')
        checkPosition.call('Address Line 1', '2')
        checkPosition.call('Address Line 2', '3')
        checkPosition.call('Suburb', '4')
        checkPosition.call('State', '5')
        checkPosition.call('Postcode', '6')

        checkPosition.call('firstColumn', '1')
        checkPosition.call('lastColumn', '6')
    }

    void testRowPositionXPath() {
        Closure checkPosition = { Map attributeMap, String position ->
            String rowPositionXPath = tablePlugin.getRowPositionXPath(attributeMap)
            assert xpath.evaluate(rowPositionXPath, htmlAsDocumentElement, XPathConstants.STRING)[0] == position
        }

        checkPosition.call(['Id' : '1'], '1')
        checkPosition.call(['Id' : '2'], '2')
        checkPosition.call(['Suburb' : 'BRISBANE'], '2')
        checkPosition.call(['Postcode' : '4005'], '1')

        checkPosition.call(['State' : 'Queensland', 'Suburb' : 'BRISBANE'], '2')

        checkPosition.call(['firstColumn' : '1'], '1')
        checkPosition.call(['lastColumn' : '4000'], '2')
    }

    void testRowPositionXPathFirst() {
        String rowPositionXPath = tablePlugin.getFirstRowPositionXPath()
        assert xpath.evaluate(rowPositionXPath, htmlAsDocumentElement, XPathConstants.STRING)[0] == '1'
    }

    void testRowPositionXPathLast() {
        String rowPositionXPath = tablePlugin.getLastRowPositionXPath()
        assert xpath.evaluate(rowPositionXPath, htmlAsDocumentElement, XPathConstants.STRING)[0] == '2'
    }

    void testSelectRow() {
        tablePlugin.selectRow = ['Suburb' : 'BRISBANE']
        assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '2'

        tablePlugin.selectRow = ['Suburb' : 'BRISBANE', 'State' : 'Queensland']
        assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '2'

        tablePlugin.selectRow = ['firstColumn' : '1']
        assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '1'

        tablePlugin.selectRow = ['lastColumn' : '4000']
        assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '2'

        tablePlugin.selectRow = 'first'
        assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '1'

        tablePlugin.selectRow = 'last'
        assert contextStub.webtest.dynamicProperties.get('madcow.table.searchResults') == '2'
    }
}