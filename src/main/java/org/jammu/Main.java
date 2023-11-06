package org.jammu;

import genetics.*;
import schedule.ScheduleConstants;
import schedule.ScheduleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Population population = new Population();
        for(int i = 0; i < GeneticConstants.NUMBER_OF_GENERATIONS; i++) {
            population.runGeneration();
            population.printBestIndividualInformation();
        }

        System.out.println("Total: " + (System.currentTimeMillis() - start));

    }
}