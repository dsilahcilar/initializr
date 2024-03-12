package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;

import java.util.ArrayList;
import java.util.List;

public class ModulesMavenBuild extends MavenBuild {

    private List<String> submodules = new ArrayList<>();

    public ModulesMavenBuild(BuildItemResolver buildItemResolver, List<String> submodules) {
        super(buildItemResolver);
        this.submodules = submodules;
    }

    public ModulesMavenBuild(List<String> submodules) {
        super();
        this.submodules = submodules;
    }

    public ModulesMavenBuild(){}

    public List<String> getSubmodules() {
        return submodules;
    }
}
