/*
 * Copyright The Sett Ltd, 2005 to 2014.
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
package com.thesett.aima.learning.decisiontree;

import com.thesett.aima.learning.LearningMethod;

/**
 * DecisionTreeLearningMethod specifies the interface that all decision tree training classes implement. At the moment
 * it is empty but all methods common to all decision tree trainers but not learning methods in general can be added in
 * here.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th> Responsibilities
 * <tr><td>
 * </table></pre>
 *
 * @author Rupert Smith
 */
public interface DecisionTreeLearningMethod extends LearningMethod
{
}
