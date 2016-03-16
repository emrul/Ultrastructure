/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta;

import com.chiralbehaviors.CoRE.jooq.tables.records.ExistentialRecord;

/**
 * An Aspect is the classifier of an networked ruleform within a network. The
 * network relation is A relationship B, where "relationship" is the aspect's
 * classifier and "B" is the aspect's classification.
 *
 * @author hhildebrand
 *
 */
public class Aspect {
    private final ExistentialRecord classification;
    private final ExistentialRecord classifier;

    /**
     * @param classifier
     * @param classification
     */
    public Aspect(ExistentialRecord classifier,
                  ExistentialRecord classification) {
        this.classification = classification;
        this.classifier = classifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Aspect)) {
            return false;
        }
        Aspect other = (Aspect) obj;
        if (classification == null) {
            if (other.classification != null) {
                return false;
            }
        } else if (!classification.getId()
                                  .equals(other.classification.getId())) {
            return false;
        }
        if (classifier == null) {
            if (other.classifier != null) {
                return false;
            }
        } else if (!classifier.getId()
                              .equals(other.classifier.getId())) {
            return false;
        }
        return true;
    }

    public ExistentialRecord getClassification() {
        return classification;
    }

    public ExistentialRecord getClassifier() {
        return classifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((classification == null) ? 0 : classification.getId()
                                                                 .hashCode());
        result = prime * result + ((classifier == null) ? 0 : classifier.getId()
                                                                        .hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Aspect [classifier=" + classifier + ", classification="
               + classification + "]";
    }
}
