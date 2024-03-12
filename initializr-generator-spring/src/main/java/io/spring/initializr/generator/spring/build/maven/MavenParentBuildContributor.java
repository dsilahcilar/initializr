package io.spring.initializr.generator.spring.build.maven;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.ModulesMavenBuild;
import io.spring.initializr.generator.buildsystem.maven.ModulesMavenBuildWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MavenParentBuildContributor extends MavenBuildProjectContributor {

    private final ModulesMavenBuildWriter parentBuildWriter = new ModulesMavenBuildWriter();
    private final ModulesMavenBuild parentMavenBuild = new ModulesMavenBuild(List.of("web"));

    public MavenParentBuildContributor(MavenBuild build, IndentingWriterFactory indentingWriterFactory) {
        super(build, indentingWriterFactory);
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path childPomPath = projectRoot.resolve("pom.xml");
        Files.deleteIfExists(childPomPath);
        Path childPom = Files.createFile(childPomPath);
        configureParentPom();
        removeUnnecessaryFieldsFromChild();
        writeBuild(Files.newBufferedWriter(childPom));
    }

    private void configureParentPom() {
        parentMavenBuild.plugins().add("org.springframework.boot", "spring-boot-maven-plugin");
        parentMavenBuild.settings()
                .group(build.getSettings().getGroup())
                .version(build.getSettings().getVersion())
                .artifact(build.getSettings().getArtifact())
                .packaging("pom")
                .parent(
                        "com.ing.apisdk",
                        "merak-spring-boot-starter-parent_2.13",
                        "26.1.0",
                        null
                );
    }

    private void removeUnnecessaryFieldsFromChild() {
        build.settings()
                .artifact(build.getSettings().getArtifact().concat("Web"))
                .version(null)
                .group(null)
                .name(null)
                .description(null);
    }

    @Override
    public void writeBuild(Writer out) throws IOException {
        try (IndentingWriter writer = indentingWriterFactory.createIndentingWriter("maven", out)) {
            parentBuildWriter.writeTo(writer, parentMavenBuild);
        }
    }
}
