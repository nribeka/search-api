/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.mclinic.search.api.internal.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class AnalyzerProvider implements Provider<Analyzer> {

    private final Version version;

    // TODO: create a factory that takes a hint of what type of analyzer should be returned here
    // see the example for checked provider
    @Inject
    protected AnalyzerProvider(final Version version) {
        this.version = version;
    }

    @Override
    public Analyzer get() {
        return new StandardAnalyzer(version);
    }
}
