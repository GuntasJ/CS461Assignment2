package genetics;

import genetics.math.ProbabilityDistributor;
import genetics.math.SoftMax;
import schedule.Activity;
import schedule.ScheduleConstants;

import java.util.*;

public class Population {
    /*
    "SLA100A": {"enrollment": 50, "preferred_facilitators": ["Glen", "Lock", "Banks", "Zeldin"], "other_facilitators": ["Numen", "Richards"]},

    "SLA100B": {"enrollment": 50, "preferred_facilitators": ["Glen", "Lock", "Banks", "Zeldin"], "other_facilitators": ["Numen", "Richards"]},

    "SLA191A": {"enrollment": 50, "preferred_facilitators": ["Glen", "Lock", "Banks", "Zeldin"], "other_facilitators": ["Numen", "Richards"]},

    "SLA191B": {"enrollment": 50, "preferred_facilitators": ["Glen", "Lock", "Banks", "Zeldin"], "other_facilitators": ["Numen", "Richards"]},

    "SLA201":  {"enrollment": 50, "preferred_facilitators": ["Glen", "Banks", "Zeldin", "Shaw"], "other_facilitators": ["Numen", "Richards", "Singer"]},

    "SLA291":  {"enrollment": 50, "preferred_facilitators": ["Lock", "Banks", "Zeldin", "Singer"], "other_facilitators": ["Numen", "Richards", "Shaw", "Tyler"]},

    "SLA303":  {"enrollment": 60, "preferred_facilitators": ["Glen", "Zeldin", "Banks"], "other_facilitators": ["Numen", "Singer", "Shaw"]},

    "SLA304":  {"enrollment": 25, "preferred_facilitators": ["Glen", "Banks", "Tyler"], "other_facilitators": ["Numen", "Singer", "Shaw", "Richards", "Uther", "Zeldin"]},

    "SLA394":  {"enrollment": 20, "preferred_facilitators": ["Tyler", "Singer"], "other_facilitators": ["Richards", "Zeldin"]},

    "SLA449":  {"enrollment": 60, "preferred_facilitators": ["Tyler", "Singer", "Shaw"], "other_facilitators": ["Zeldin", "Uther"]},

    "SLA451":  {"enrollment": 100, "preferred_facilitators": ["Tyler", "Singer", "Shaw"], "other_facilitators": ["Zeldin", "Uther", "Richards", "Banks"]},
     */


    private int generationCount;
    private List<Chromosome> population;

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

    public List<Chromosome> getPopulation() {
        return population;
    }

    public void runGeneration() {
        if(generationCount == 0) {
            rankInitialPopulation();
        }
        long a = System.currentTimeMillis();
        cullHalfPopulation();
        //System.out.println("Cull Half Population: " + (System.currentTimeMillis() - a));
        a = System.currentTimeMillis();
        reproduceHalfPopulation();
        //System.out.println("Reproduce Half Population: " + (System.currentTimeMillis() - a));
        a = System.currentTimeMillis();
        rankPopulation();
        //System.out.println("Rank Population: " + (System.currentTimeMillis() - a));
        generationCount++;

    }

    private void reproduceHalfPopulation() {
        assignChromosomesMatingProbabilities();
        ProbabilityDistributor<Chromosome> probabilityDistributor = new ProbabilityDistributor<>(population);

        int targetPopulation = population.size() * 2;
        long start = System.currentTimeMillis();
        long x = 0;
        long z = 0;
        long a = 0;
        while(population.size() != targetPopulation) {
            long y = System.currentTimeMillis();
            Chromosome chromosome1 = probabilityDistributor.pick(0);
            Chromosome chromosome2 = probabilityDistributor.pick(1);
            x += System.currentTimeMillis() - y;

            y = System.currentTimeMillis();
            List<Chromosome> offspring = chromosome1.crossoverWith(chromosome2);
            z += System.currentTimeMillis() - y;
            y = System.currentTimeMillis();
            offspring.forEach(Chromosome::attemptMutation);
            population.addAll(offspring);

            a += System.currentTimeMillis() - y;

        }
        System.out.println("Picking Chromosomes: " + x);
        System.out.println("Chromosome Crossover: " + z);
        System.out.println("Adding to population: " + a);
        System.out.println(System.currentTimeMillis() - start);

    }

    private void rankInitialPopulation() {
//        for(Chromosome chromosome : population) {
//            if(chromosome.getFitness() == -1) {
//                chromosome.calculateFitness();
//            }
//        }
        population.parallelStream().forEach(Chromosome::calculateFitness);
        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
    }
    private void rankPopulation() {
        population.subList(population.size() / 2, population.size())
                .parallelStream()
                .forEach(Chromosome::calculateFitness);
        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());
    }

    // a b c d e f
    private void cullHalfPopulation() {
        int cutoffIndex = population.size() / 2;
        //population = population.subList(0, cutoffIndex);

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
