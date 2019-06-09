package at.ac.tuwien.big.moea.search.algorithm;

import at.ac.tuwien.big.moea.search.algorithm.local.IFitnessComparator;
import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhood;
import at.ac.tuwien.big.moea.search.algorithm.local.INeighborhoodFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public class MyHC<S extends Solution> extends AbstractAlgorithm {

   /**
    * The initialization routine used to generate random solutions.
    */
   private final Initialization generator;

   /**
    * The archive of non-dominated solutions.
    */
   private final NondominatedPopulation archive;

   /**
    * Constructs a new random search procedure for the given problem.
    *
    * @param problem
    *           the problem being solved
    * @param generator
    *           the initialization routine used to generate random
    *           solutions
    * @param archive
    *           the archive of non-dominated solutions
    */

   protected INeighborhoodFunction<S> neighborhoodFunction;
   protected IFitnessComparator<?, S> fitnessComparator;

   public MyHC(final Problem problem, final Initialization generator, final NondominatedPopulation archive,
         final INeighborhoodFunction<S> neighborhoodFunction, final IFitnessComparator<?, S> fitnessComparator) {
      super(problem);
      this.generator = generator;
      this.archive = archive;
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

   protected final S getBest(final List<S> solutions) {
      if(solutions == null || solutions.isEmpty()) {
         return null;
      }
      return sortSolutions(solutions).get(0);
   }

   @Override
   public NondominatedPopulation getResult() {
      return archive;
   }

   @Override
   protected void initialize() {
      super.initialize();
      final Population solutions = new Population(generator.initialize());
      evaluateAll(solutions);
      archive.addAll(solutions);
   }

   @Override
   protected void iterate() {

      for(final Solution s : archive) {
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
            archive.remove(solution);
            archive.add(bestNeighbor);
         }
      }

   }

   protected final List<S> sortSolutions(final List<S> solutions) {
      Collections.sort(solutions, this.fitnessComparator);
      return solutions;
   }

}
