package de.oppermann.pomutils.rules;

import de.oppermann.pomutils.select.SelectionStrategy;
import de.oppermann.pomutils.util.POM;
import de.oppermann.pomutils.util.VersionFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 *
 * @author Sébastien Latre
 * 
 */

public class ScmTagRule extends AbstractRule {

	private final Logger logger = LoggerFactory.getLogger(ScmTagRule.class);

	public ScmTagRule() {
		// for creating this instance via snakeyaml
	}

	public ScmTagRule(SelectionStrategy strategy) {
		super(strategy);
		logger.debug("Using ScmTagRule with strategy [{}]", strategy.toString());
	}

	@Override
	public void evaluate(POM basePom, POM ourPom, POM theirPom) {
		String baseScmTag = basePom.getScmTag();
		String ourScmTag = ourPom.getScmTag();
		String theirScmTag = theirPom.getScmTag();
		if (baseScmTag != null && ourScmTag != null && theirScmTag != null && !ourScmTag.equals(theirScmTag)) {
			String newScmTag;
			if (baseScmTag.equals(ourScmTag)) {
				/*
				 * Our ScmTag hasn't changed, so no conflict. Just use theirScmTag.
				 */
				newScmTag = theirScmTag;
			} else if (baseScmTag.equals(theirScmTag)) {
				/*
				 * Their scmTag hasn't changed, so no conflict. Just use ourScmTag.
				 */
				newScmTag = ourScmTag;
			} else {
				/*
				 * Both our scmTag and their scmTag have changed from the base, so conflict.
				 */
				switch (getStrategy()) {
					case OUR:
						newScmTag = ourScmTag;
						break;
					case THEIR:
						newScmTag = theirScmTag;
						break;
					default:
						throw new IllegalArgumentException("Strategy [" + getStrategy().toString() + "] not implemented.");
				}
			}

			if (newScmTag != null) {
				/*
				 * newScmTag can be null if the user wants to skip resolution.
				 */

				POM pomToAdjust = newScmTag.equals(ourScmTag)
						? theirPom
						: ourPom;

				pomToAdjust.setScmTag(newScmTag);
			}
		}
	}
}
