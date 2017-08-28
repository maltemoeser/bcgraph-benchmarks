package de.maltemoeser.benchmark;

abstract class Benchmark {

    void runBenchmark() {
        System.out.println("Running Benchmark \"" + getName() + "\"");
        long startTime = System.currentTimeMillis();
        System.out.println("Result: " + run());
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time elapsed: " + (totalTime / 1000) + " seconds");
        System.out.println();
    }

    abstract String getName();

    abstract long run();

}
