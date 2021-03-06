/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.signature.validator;

import org.opensaml.xml.signature.X509Digest;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

/**
 * Checks {@link X509Digest} for Schema compliance. 
 */
public class X509DigestSchemaValidator implements Validator<X509Digest> {

    /** {@inheritDoc} */
    public void validate(X509Digest xmlObject) throws ValidationException {
        validateContent(xmlObject);
    }

    /**
     * Validate the digest content.
     * 
     * @param xmlObject the object to validate
     * @throws ValidationException  thrown if the object is invalid
     */
    protected void validateContent(X509Digest xmlObject) throws ValidationException {
        if (xmlObject.getValue() == null) {
            throw new ValidationException("X509Digest did not contain a value");
        } else if (DatatypeHelper.isEmpty(xmlObject.getAlgorithm())) {
            throw new ValidationException("X509Digest did not contain Algorithm attribute");
        }
    }

}