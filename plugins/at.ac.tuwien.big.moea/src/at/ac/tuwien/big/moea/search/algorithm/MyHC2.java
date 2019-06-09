package at.ac.tuwien.big.moea.search.algorithm;

import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhood;
import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhoodFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.TournamentSelection;

public class MyHC2<S extends Solution> extends AbstractEvolutionaryAlgorithm
      implements EpsilonBoxEvolutionaryAlgorithm {

   /**
    * The selection operator. If {@code null}, this algorithm uses binary
    * tournament selection without replacement, replicating the behavior of the
    * original NSGA-II implementation.
    */
   private final Selection selection;

   /**
    * The variation operator.
    */
   private final Variation variation;

   /**
    * Constructs the NSGA-II algorithm with the specified components.
    *
    * @param problem
    *           the problem being solved
    * @param population
    *           the population used to store solutions
    * @param archive
    *           the archive used to store the result; can be {@code null}
    * @param selection
    *           the selection operator
    * @param variation
    *           the variation operator
    * @param initialization
    *           the initialization method
    */

   protected INeighborhoodFunction<S> neighborhoodFunction;
   protected IFitnessComparator<?, S> fitnessComparator;

   public MyHC2(final Problem problem, final NondominatedSortingPopulation population,
         final EpsilonBoxDominanceArchive archive, final Selection selection, final Variation variation,
         final Initialization initialization, final INeighborhoodFunction<S> neighborhoodFunction,
         final IFitnessComparator<?, S> fitnessComparator) {
      super(problem, population, archive, initialization);
      this.selection = selection;
      this.variation = variation;
      this.neighborhoodFunction = neighborhoodFunction;
      this.fitnessComparator = fitnessComparator;
   }

   protected int compare(final S solution, final S otherSolution) {
      if(fitnessComparator == null) {
         throw new IllegalArgumentException("Local Search needs FitnessComparator to compare solutions.");
      }
      return fitnessComparator.compare(solution, otherSolution);
   }

   protected INeighborhood<S> generateCurrentNeighbors(final S solution) {
      return neighborhoodFunction.generateNeighbors(solution);
   }

   @Override
   public EpsilonBoxDominanceArchive getArchive() {
      return (EpsilonBoxDominanceArchive) super.getArchive();
   }

   protected final S getBest(final List<S> solutions) {
      if(solutions == null || solutions.isEmpty()) {
         return null;
      }
      return sortSolutions(solutions).get(0);
   }

   @Override
   public NondominatedSortingPopulation getPopulation() {
      return (NondominatedSortingPopulation) super.getPopulation();
   }

   @Override
   public void iterate() {
      final NondominatedSortingPopulation population = getPopulation();
      final EpsilonBoxDominanceArchive archive = getArchive();
      final Population offspring = new Population();
      final int populationSize = population.size();

      if(selection == null) {
         // recreate the original NSGA-II implementation using binary
         // tournament selection without replacement; this version works by
         // maintaining a pool of candidate parents.
         final LinkedList<Solution> pool = new LinkedList<>();

         final DominanceComparator comparator = new ChainedComparator(new ParetoDominanceComparator(),
               new CrowdingComparator());

         while(offspring.size() < populationSize) {
            // ensure the pool has enough solutions
            while(pool.size() < 2 * variation.getArity()) {
               final List<Solution> poolAdditions = new ArrayList<>();

               for(final Solution solution : population) {
                  poolAdditions.add(solution);
               }

               PRNG.shuffle(poolAdditions);
               pool.addAll(poolAdditions);
            }

            // select the parents using a binary tournament
            final Solution[] parents = new Solution[variation.getArity()];

            for(int i = 0; i < parents.length; i++) {
               parents[i] = TournamentSelection.binaryTournament(pool.removeFirst(), pool.removeFirst(), comparator);
            }

            // evolve the children
            offspring.addAll(variation.evolve(parents));
         }
      } else {
         // run NSGA-II using selection with replacement; this version allows
         // using custom selection operators
         while(offspring.size() < populationSize) {
            final Solution[] parents = selection.select(variation.getArity(), population);

            offspring.addAll(variation.evolve(parents));
         }
      }
      final Population offspring2 = new Population();

      for(final Solution s : offspring) {
         final S solution = (S) s;
         final List<S> neighbors = new ArrayList<>();
         for(final S neighbor : generateCurrentNeighbors(solution)) {
            evaluate(neighbor);
            neighbors.add(neighbor);
         }
         if(neighbors.isEmpty()) {
            terminate();
            return;
         }
         final S bestNeighbor = getBest(neighbors);
         if(compare(solution, bestNeighbor) < 0) {
            offspring2.add(bestNeighbor);
         } else {
            offspring2.add(solution);
         }
      }

      evaluateAll(offspring2);

      if(archive != null) {
         archive.addAll(offspring2);
      }

      population.addAll(offspring2);
      population.truncate(populationSize);
   }

   protected final List<S> sortSolutions(final List<S> solutions) {
      Collections.sort(solutions, this.fitnessComparator);
      return solutions;
   }

}
