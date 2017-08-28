package de.maltemoeser.benchmark;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.maltemoeser.bcgraph.constants.LabelType;
import de.maltemoeser.bcgraph.database.Database;
import de.maltemoeser.bcgraph.entities.BCOutput;
import de.maltemoeser.bcgraph.entities.BCTransaction;
import de.maltemoeser.bcgraph.injector.AnalysisInjector;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.Iterators;

public class MaxOutputValue extends Benchmark {

    private GraphDatabaseService graphDatabaseService;

    @Inject
    public MaxOutputValue(Database database) {
        this.graphDatabaseService = database.getGraphDatabaseService();
    }

    @Override
    String getName() {
        return "Maximum Output Value";
    }

    @Override
    public long run() {
        long maxOutputValue = 0;
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            for (Node node : Iterators.asIterable(graphDatabaseService.findNodes(LabelType.Transaction))) {
                BCTransaction transaction = new BCTransaction(node);
                for (BCOutput output : transaction.getOutputs()) {
                    long v = output.getValue();
                    if (v > maxOutputValue) {
                        maxOutputValue = v;
                    }
                }
            }
        }
        return maxOutputValue;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AnalysisInjector());
        Benchmark bench = injector.getInstance(MaxOutputValue.class);
        // run to fill caches
        bench.runBenchmark();
        // run second time for measurement
        bench.runBenchmark();
    }
}