package genetics;

import genetics.math.ProbabilityDistributor;
import genetics.math.SoftMax;
import schedule.Activity;
import schedule.ScheduleConstants;

import java.util.*;

public class Population {

    private int generationCount;
    private final List<Chromosome> population;

    public Population() {
        population = new ArrayList<>(GeneticConstants.INITIAL_POPULATION_SIZE + 10);
        generationCount = 0;
        generateInitialPopulation();
    }

    public void printBestIndividualInformation() {
        System.out.println("_______________________________________________________");
        System.out.println("Best Individual from generation: " + generationCount);
        System.out.println(population.get(0));
        System.out.println("Fitness Score: " + population.get(0).getFitness());
        System.out.println("_______________________________________________________");
    }

    public void runGeneration() {
        if(generationCount == 0) {
            rankInitialPopulation();
        }
        cullHalfPopulation();

        reproduceHalfPopulation();

        rankPopulation();

        generationCount++;
    }

    private void reproduceHalfPopulation() {
        assignChromosomesMatingProbabilities();
        ProbabilityDistributor<Chromosome> probabilityDistributor = new ProbabilityDistributor<>(population);

        int targetPopulation = population.size() * 2;
        while(population.size() != targetPopulation) {
            Chromosome chromosome1 = probabilityDistributor.pick(0);
            Chromosome chromosome2 = probabilityDistributor.pick(1);

            List<Chromosome> offspring = chromosome1.crossoverWith(chromosome2);

            offspring.forEach(Chromosome::attemptMutation);
            population.addAll(offspring);

        }
    }

    private void rankInitialPopulation() {
        population.parallelStream().forEach(Chromosome::calculateFitness);
        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
    }
    private void rankPopulation() {
        population.subList(population.size() / 2, population.size())
                .parallelStream()
                .forEach(Chromosome::calculateFitness);
        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
    }

    private void cullHalfPopulation() {
        int cutoffIndex = population.size() / 2;
        population.subList(cutoffIndex, population.size()).clear();
    }

    private void assignChromosomesMatingProbabilities() {
        List<Double> normalizedFitnessScores = SoftMax.normalize(
                population.stream().map(Chromosome::getFitness).toList()
        );

        for(int i = 0; i < normalizedFitnessScores.size(); i++) {
            population.get(i).setMatingProbability(normalizedFitnessScores.get(i));
        }
    }

    private void generateInitialPopulation() {
        for(int i = 0; i < GeneticConstants.INITIAL_POPULATION_SIZE; i++) {
            List<Gene> geneList = new ArrayList<>();
            for(Activity activity : ScheduleConstants.ACTIVITIES) {
                geneList.add(GeneticUtils.createRandomGene(activity));
            }
            population.add(new Chromosome(geneList));
        }
    }

}
