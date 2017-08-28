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

public class MaxComputedFee extends Benchmark {

    private GraphDatabaseService graphDatabaseService;

    @Inject
    public MaxComputedFee(Database database) {
        this.graphDatabaseService = database.getGraphDatabaseService();
    }

    @Override
    String getName() {
        return "Maximum Transaction Fee (Computed)";
    }

    @Override
    public long run() {
        long maxFee = 0;
        try (org.neo4j.graphdb.Transaction ignored = graphDatabaseService.beginTx()) {
            for (Node node : Iterators.asIterable(graphDatabaseService.findNodes(LabelType.Transaction))) {
                BCTransaction transaction = new BCTransaction(node);
                if (!transaction.isCoinbase()) {
                    long fee = 0;
                    for (BCOutput input : transaction.getInputs()) {
                        fee += input.getValue();
                    }
                    for (BCOutput output : transaction.getOutputs()) {
                        fee -= output.getValue();
                    }
                    if (fee > maxFee) {
                        maxFee = fee;
                    }
                }

            }
        }
        return maxFee;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AnalysisInjector());
        Benchmark bench = injector.getInstance(MaxComputedFee.class);
        // run to fill caches
        bench.runBenchmark();
        // run second time for measurement
        bench.runBenchmark();
    }
}
