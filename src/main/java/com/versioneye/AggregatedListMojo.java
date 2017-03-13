package com.versioneye;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import static com.versioneye.dependency.DependencyResolver.asSortedList;
import static com.versioneye.utils.log.LogUtil.logArtifactsList;
import static com.versioneye.utils.log.LogUtil.logDependencySummary;

@Mojo(name = "aggregated-list", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class AggregatedListMojo extends AbstractAggregatedMojo {

    @Override
    public void doFinalExecute() throws Exception {
        logArtifactsList("Direct", asSortedList(directDependencies));
        logArtifactsList("Transitive", asSortedList(transitiveDependencies));
        logDependencySummary(directDependencies.size(), transitiveDependencies.size());
    }
}
