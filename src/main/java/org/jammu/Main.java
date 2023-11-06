package org.jammu;

import genetics.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {
    public static void main(String[] args) throws IOException {
        Population population = new Population();

        for(int i = 0; i < GeneticConstants.NUMBER_OF_GENERATIONS; i++) {
            population.runGeneration();
            population.printBestIndividualInformation();
        }

        double previousAverageFitnessScore;
        do {
            previousAverageFitnessScore = population.getAverageFitness();
            population.runGeneration();
            population.printBestIndividualInformation();
        } while (population.getAverageFitness() > 1.01 * previousAverageFitnessScore);


        System.out.println(population.getBestIndividual().toPrettyString());
        Files.writeString(Paths.get("output.txt"), population.getBestIndividual().toPrettyString());


    }
}