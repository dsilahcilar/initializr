/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.InitializrMetadata;

/**
 * The default {@link Maven} {@link BuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
public class DefaultMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

    private final ProjectDescription description;

    private final InitializrMetadata metadata;

    public DefaultMavenBuildCustomizer(ProjectDescription description, InitializrMetadata metadata) {
        this.description = description;
        this.metadata = metadata;
    }

    @Override
    public void customize(MavenBuild build) {
        build.settings().name(this.description.getName()).description(this.description.getDescription());
        build.properties().property("java.version", this.description.getLanguage().jvmVersion());
        build.plugins().add("org.springframework.boot", "spring-boot-maven-plugin");

        Maven maven = this.metadata.getConfiguration().getEnv().getMaven();
        ParentPom parentPom = new ParentPom(this.description.getGroupId(), this.description.getArtifactId(), this.description.getVersion(), null);
        if (!maven.isSpringBootStarterParent(parentPom)) {
            build.properties()
                    .property("project.build.sourceEncoding", "UTF-8")
                    .property("project.reporting.outputEncoding", "UTF-8");
        }
        build.settings()
                .parent(parentPom.getGroupId(), parentPom.getArtifactId(), parentPom.getVersion(),
                        parentPom.getRelativePath());
        addRepositories(build);
    }

    private void addRepositories(MavenBuild build) {
        final String feedName = String.format("%s-incoming-%s", this.description.pCode(), this.description.ciName());
        final String url = String.format("https://pkgs.dev.azure.com/INGCDaaS/IngOne/_packaging/%s/maven/v1", feedName);
        build.repositories()
                .add(new MavenRepository.Builder(feedName, url)
                        .releasesEnabled(true)
                        .snapshotsEnabled(true));
        build.pluginRepositories()
                .add(new MavenRepository.Builder(feedName, url)
                        .snapshotsEnabled(false));
    }

}
