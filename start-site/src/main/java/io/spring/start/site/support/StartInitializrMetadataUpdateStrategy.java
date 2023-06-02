/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import io.spring.initializr.web.support.SpringIoInitializrMetadataUpdateStrategy;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link InitializrMetadataUpdateStrategy} that performs additional filtering of
 * versions available on spring.io.
 * <p>修改成 current bootVersion + updated bootVersion</p>
 *
 * @author Stephane Nicoll
 * @author <a href="https://github.com/studeyang">studeyang</a>
 */
public class StartInitializrMetadataUpdateStrategy extends SpringIoInitializrMetadataUpdateStrategy {

    private static final Comparator<DefaultMetadataElement> VERSION_METADATA_ELEMENT_COMPARATOR = new VersionMetadataElementComparator();

    public StartInitializrMetadataUpdateStrategy(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper);
    }

    @Override
    public InitializrMetadata update(InitializrMetadata current) {
        String url = current.getConfiguration().getEnv().getSpringBootMetadataUrl();
        List<DefaultMetadataElement> bootVersions = fetchSpringBootVersions(url);

        if (bootVersions != null && !bootVersions.isEmpty()) {
            bootVersions.addAll(current.getBootVersions().getContent());
            if (bootVersions.stream().noneMatch(DefaultMetadataElement::isDefault)) {
                // No default specified
                bootVersions.get(0).setDefault(true);
            }
            bootVersions.sort(VERSION_METADATA_ELEMENT_COMPARATOR);
            current.updateSpringBootVersions(bootVersions);
        }
        return current;
    }

    @Override
    protected List<DefaultMetadataElement> fetchSpringBootVersions(String url) {
        List<DefaultMetadataElement> versions = super.fetchSpringBootVersions(url);
        return (versions != null) ? versions.stream().filter(this::isCompatibleVersion).collect(Collectors.toList())
                : null;
    }

    private boolean isCompatibleVersion(DefaultMetadataElement versionMetadata) {
        Version version = Version.parse(versionMetadata.getId());
        return (version.getMajor() == 2 && version.getMinor() > 4) || (version.getMajor() >= 3);
    }

    private static class VersionMetadataElementComparator implements Comparator<DefaultMetadataElement> {

        private static final VersionParser versionParser = VersionParser.DEFAULT;

        @Override
        public int compare(DefaultMetadataElement o1, DefaultMetadataElement o2) {
            Version o1Version = versionParser.parse(o1.getId());
            Version o2Version = versionParser.parse(o2.getId());
            return o1Version.compareTo(o2Version);
        }

    }

}
