package genetics;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import schedule.Activity;
import schedule.Period;
import schedule.Room;
import schedule.Time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static genetics.GeneticUtils.doActivitiesHaveSameTimeAndFacilitator;
import static genetics.GeneticUtils.isActivitySamePlaceAndTime;
import static org.junit.jupiter.api.Assertions.*;

class GeneticUtilsTest {

    Chromosome emptyChromosome;
    Chromosome increasingChromosome;
    double fitnessScore = 0;

    @BeforeEach
    void setUp() {
        fitnessScore = 0;
        List<Gene> geneList = new ArrayList<>();
        for(int i = 0; i < GeneticConstants.NUMBER_OF_GENES; i++) {
            geneList.add(new Gene(
                    new Activity("", 0, Collections.emptyList(), Collections.emptyList()),
                    new Room("", 0),
                    new Time(0, Period.PM),
                    ""
            ));
        }
        emptyChromosome = new Chromosome(geneList);

        List<Gene> geneList1 = new ArrayList<>();
        for(int i = 0; i < GeneticConstants.NUMBER_OF_GENES; i++) {
            geneList1.add(new Gene(
                    new Activity("A" + i, 10 + i, Collections.emptyList(), Collections.emptyList()),
                    new Room("R" + i, 10 + i),
                    new Time(1 + i, Period.PM),
                    "F" + i
            ));
        }
        increasingChromosome = new Chromosome(geneList1);

    }

    @Test
    //Activity is scheduled at the same time in the same room as another of the activities: -0.5
    void testIfActivityScheduledAtSameTimeInSameRoomAsAnotherActivityCausesFitnessLoss() {
        Gene targetGene = increasingChromosome.geneList().get(0);
        List<Gene> remainingGenes = GeneticUtils.getRemainingGenes(targetGene, increasingChromosome);

        targetGene = new Gene(
                new Activity(null, 0, null, null),
                new Room("R1", 11),
                new Time(2, Period.PM),
                null
        );

        Gene finalTargetGene = targetGene;
        if(remainingGenes.stream().anyMatch(gene -> isActivitySamePlaceAndTime(gene, finalTargetGene))) {
            fitnessScore -= 0.5;
        }

        assertEquals(-0.5, fitnessScore);
    }

    //Activities is in a room too small for its expected enrollment: -0.5
    @Test
    void testIfActivityInRoomSmallerThanExpectedEnrollmentCausesFitnessLoss() {
        Gene targetGene = new Gene(
                new Activity(null, 50, null, null),
                new Room("", 40),
                null,
                null
        );
        if(targetGene.getActivity().expectedEnrollment() > targetGene.getRoom().capacity()) {
            fitnessScore -= 0.5;
        }
        assertEquals(-0.5, fitnessScore);
    }

    //Activities is in a room with capacity > 3 times expected enrollment: -0.2
    @Test
    void testIfActivityIsInRoomWithCapacity3TimesBiggerThanExpectedEnrollmentCausesFitnessLoss() {
        Gene targetGene = new Gene(
                new Activity(null, 50, null, null),
                new Room("", 151),
                null,
                null
        );
        if(targetGene.getRoom().capacity() >  3 * targetGene.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.2;
        }
        assertEquals(-0.2, fitnessScore);
    }

    //Activities is in a room with capacity > 6 times expected enrollment: -0.4
    @Test
    void testIfActivityIsInRoomWithCapacity6TimesBiggerThanExpectedEnrollmentCausesFitnessLoss() {
        Gene targetGene = new Gene(
                new Activity(null, 50, null, null),
                new Room("", 301),
                null,
                null
        );
        if(targetGene.getRoom().capacity() >  6 * targetGene.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.4;
        }
        assertEquals(-0.4, fitnessScore);
    }

    //Otherwise + 0.3
    @Test
    void testIfActivityIsInRoomWithCapacityLessThan3TimesBiggerThanExpectedEnrollmentAndMoreThanExpectedEnrollmentCausesFitnessGain() {
        Gene targetGene = new Gene(
                new Activity(null, 50, null, null),
                new Room("", 100),
                null,
                null
        );
        if(targetGene.getActivity().expectedEnrollment() > targetGene.getRoom().capacity()) {
            fitnessScore -= 0.5;
        }
        else if(targetGene.getRoom().capacity() > 3 * targetGene.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.2;
        }
        else if(targetGene.getRoom().capacity() > 6 * targetGene.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.4;
        }
        else {
            fitnessScore += 0.3;
        }
        assertEquals(0.3, fitnessScore);
    }

     /*
        Activities are overseen by a preferred facilitator: + 0.5
        Activities is overseen by another facilitator listed for that activity: +0.2
        Activities is overseen by some other facilitator: -0.1
     */
    @Test
    void testIfActivityIsOverseenByAPreferredFacilitatorCausesFitnessGain() {
        Gene targetGene = new Gene(
                new Activity(null, 0, List.of("Dave"), null),
                null,
                null,
                "Dave"
        );
        if(targetGene.getActivity().preferredFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.5;
        }
        else if(targetGene.getActivity().otherFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.2;
        }
        else {
            fitnessScore -= 0.1;
        }
        assertEquals(0.5, fitnessScore);
    }

    @Test
    void testIfActivityIsOverseenByAOtherFacilitatorCausesFitnessGain() {
        Gene targetGene = new Gene(
                new Activity(null, 0, Collections.emptyList(), List.of("Dave")),
                null,
                null,
                "Dave"
        );
        if(targetGene.getActivity().preferredFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.5;
        }
        else if(targetGene.getActivity().otherFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.2;
        }
        else {
            fitnessScore -= 0.1;
        }
        assertEquals(0.2, fitnessScore);
    }

    @Test
    void testIfActivityIsOverseenByANotIncludedFacilitatorCausesFitnessLoss() {
        Gene targetGene = new Gene(
                new Activity(null, 0, Collections.emptyList(), Collections.emptyList()),
                null,
                null,
                "Dave"
        );
        if(targetGene.getActivity().preferredFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.5;
        }
        else if(targetGene.getActivity().otherFacilitators().contains(targetGene.getFacilitator())) {
            fitnessScore += 0.2;
        }
        else {
            fitnessScore -= 0.1;
        }
        assertEquals(-0.1, fitnessScore);
    }

    //Activity facilitator is scheduled for only 1 activity in this time slot: + 0.2
    //Activity facilitator is scheduled for more than one activity at the same time: - 0.2
    @Test
    void testIfActivityFacilitatorIsScheduledForOnly1ActivityInTimeSlotCausesFitnessGain() {
        Gene targetGene = increasingChromosome.geneList().get(0);
        List<Gene> remainingGenes = GeneticUtils.getRemainingGenes(targetGene, increasingChromosome);

        // Activity facilitator is scheduled for only 1 activity in this time slot: + 0.2
        if(remainingGenes.stream().noneMatch(gene -> doActivitiesHaveSameTimeAndFacilitator(gene, targetGene))) {
            fitnessScore += 0.2;
        }
        // Activity facilitator is scheduled for more than one activity at the same time: - 0.2
        else  {
            fitnessScore -= 0.2;
        }
        assertEquals(0.2, fitnessScore);
    }

    @Test
    void testIfActivityFacilitatorIsScheduledForMoreThan1ActivityInTimeSlotCausesFitnessLoss() {
        Gene targetGene = increasingChromosome.geneList().get(0);
        List<Gene> remainingGenes = GeneticUtils.getRemainingGenes(targetGene, increasingChromosome);

        targetGene = new Gene(
                null,
                null,
                new Time(2, Period.PM),
                "F1"
        );

        // Activity facilitator is scheduled for only 1 activity in this time slot: + 0.2
        Gene finalTargetGene = targetGene;
        if(remainingGenes.stream().noneMatch(gene -> doActivitiesHaveSameTimeAndFacilitator(gene, finalTargetGene))) {
            fitnessScore += 0.2;
        }
        // Activity facilitator is scheduled for more than one activity at the same time: - 0.2
        else  {
            fitnessScore -= 0.2;
        }
        assertEquals(-0.2, fitnessScore);
    }


    // Facilitator is scheduled to oversee more than 4 activities total: -0.5
    @Test
    void testIfFacilitatorIsScheduledToOverseeMoreThan4ActivitiesTotalCausesFitnessLoss() {
        Gene targetGene = increasingChromosome.geneList().get(0);
        List<Gene> remainingGenes = GeneticUtils.getRemainingGenes(targetGene, increasingChromosome);

        remainingGenes.set(1, new Gene(
                null,
                null,
                null,
                "F0"
        ));
        remainingGenes.set(2, new Gene(
                null,
                null,
                null,
                "F0"
        ));
        remainingGenes.set(3, new Gene(
                null,
                null,
                null,
                "F0"
        ));
        remainingGenes.set(4, new Gene(
                null,
                null,
                null,
                "F0"
        ));
        long scheduledActivityCountForFacilitator = remainingGenes.stream()
                .filter(gene -> gene.getFacilitator().equals(targetGene.getFacilitator()))
                .count() + 1;

        if(scheduledActivityCountForFacilitator > 4) {
            fitnessScore -= 0.5;
        }
        assertEquals(-0.5, fitnessScore);
    }

    // Facilitator is scheduled to oversee 1 or 2 activities*: -0.4
    // Exception: Dr. Tyler is committee chair and has other demands on his time.
    // No penalty if he’s only required to oversee < 2 activities.
    @Test
    void testIfFacilitatorIsScheduledToOversee1Or2ActivitiesTotalCausesFitnessLoss() {
        Gene targetGene = increasingChromosome.geneList().get(0);
        List<Gene> remainingGenes = GeneticUtils.getRemainingGenes(targetGene, increasingChromosome);

        remainingGenes.set(1, new Gene(
                null,
                null,
                null,
                "F0"
        ));

        long scheduledActivityCountForFacilitator = remainingGenes.stream()
                .filter(gene -> gene.getFacilitator().equals(targetGene.getFacilitator()))
                .count() + 1;

        if(scheduledActivityCountForFacilitator < 3 && !targetGene.getFacilitator().equals("Tyler")) {
            fitnessScore -= 0.4;
        }
        assertEquals(-0.4, fitnessScore);
    }

    @Test
    void testIfFacilitatorTylerIsScheduledToOversee1Or2ActivitiesTotalCausesNoFitnessLoss() {
        Gene targetGene = increasingChromosome.geneList().get(0);
        List<Gene> remainingGenes = GeneticUtils.getRemainingGenes(targetGene, increasingChromosome);

        targetGene = new Gene(null, null,null, "Tyler");

        remainingGenes.set(1, new Gene(
                null,
                null,
                null,
                "Tyler"
        ));

        Gene finalTargetGene = targetGene;
        long scheduledActivityCountForFacilitator = remainingGenes.stream()
                .filter(gene -> gene.getFacilitator().equals(finalTargetGene.getFacilitator()))
                .count() + 1;

        if(scheduledActivityCountForFacilitator < 3 && !targetGene.getFacilitator().equals("Tyler")) {
            fitnessScore -= 0.4;
        }
        assertEquals(0, fitnessScore);
    }


}