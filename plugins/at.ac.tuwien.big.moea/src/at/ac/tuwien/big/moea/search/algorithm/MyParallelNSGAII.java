package at.ac.tuwien.big.moea.search.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

/**
 * Implementation of NSGA-II, with the ability to attach an optional
 * &epsilon;-dominance archive.
 * <p>
 * References:
 * <ol>
 * <li>Deb, K. et al. "A Fast Elitist Multi-Objective Genetic Algorithm:
 * NSGA-II." IEEE Transactions on Evolutionary Computation, 6:182-197,
 * 2000.
 * <li>Kollat, J. B., and Reed, P. M. "Comparison of Multi-Objective
 * Evolutionary Algorithms for Long-Term Monitoring Design." Advances in
 * Water Resources, 29(6):792-807, 2006.
 * </ol>
 */
public class MyParallelNSGAII extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

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

   private final ExecutorService executor;
   private final int threadNum = 12;

   public MyParallelNSGAII(final Problem problem, final NondominatedSortingPopulation population,
         final EpsilonBoxDominanceArchive archive, final Selection selection, final Variation variation,
         final Initialization initialization) {
      super(problem, population, archive, initialization);
      this.selection = selection;
      this.variation = variation;
      this.executor = Executors.newFixedThreadPool(threadNum);
   }

   @Override
   public void evaluateAll(final Iterable<Solution> solutions) {
      // TODO Auto-generated method stub
      final List<List<Solution>> partitions = new ArrayList<>();
      final ArrayList<Callable<Object>> callers = new ArrayList<>();
      int idx = 0;
      for(int i = 0; i < threadNum; i++) {
         partitions.add(new ArrayList<Solution>());
      }
      for(final Solution solution : solutions) {
         partitions.get(idx).add(solution);
         idx = (idx + 1) % threadNum;
      }
      for(int i = 0; i < threadNum; i++) {
         partitions.add(new ArrayList<Solution>());
         final int tmp = i;
         callers.add(Executors.callable(() -> {
            // TODO Auto-generated method stub
            for(final Solution solution : partitions.get(tmp)) {
               evaluate(solution);
            }
         }));
      }
      try {
         executor.invokeAll(callers);
      } catch(final InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   @Override
   public EpsilonBoxDominanceArchive getArchive() {
      return (EpsilonBoxDominanceArchive) super.getArchive();
   }

   @Override
   public NondominatedSortingPopulation getPopulation() {
      return (NondominatedSortingPopulation) super.getPopulation();
   }

   @Override
   public void iterate() {
      // final long startTime = System.currentTimeMillis();
      // long variationTime = 0;
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
            final long st = System.currentTimeMillis();
            offspring.addAll(variation.evolve(parents));
            // variationTime += System.currentTimeMillis() - st;
         }
      } else {
         // run NSGA-II using selection with replacement; this version allows
         // using custom selection operators
         while(offspring.size() < populationSize) {
            final long st = System.currentTimeMillis();
            final Solution[] parents = selection.select(variation.getArity(), population);
            // variationTime += System.currentTimeMillis() - st;

            offspring.addAll(variation.evolve(parents));
         }
      }
      // long st = System.currentTimeMillis();
      // final String res = MySearchContext.my_evaluate(offspring);
      // System.out.println("My evaluation cost: " + (double) (System.currentTimeMillis() - st) / (double) 1000);
      // System.out.println(res);
      // try {
      // MySearchContext.fw.write(res + "\n");
      // } catch(final IOException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      // final long st = System.currentTimeMillis();
      evaluateAll(offspring);
      // System.out.println("Evaluation cost: " + (double) (System.currentTimeMillis() - st) / (double) 1000);
      if(archive != null) {
         archive.addAll(offspring);
      }

      population.addAll(offspring);
      population.truncate(populationSize);
      // System.out.println("Variation cost: " + (double) variationTime / (double) 1000);
      // System.out.println("Total cost: " + (double) (System.currentTimeMillis() - startTime) / (double) 1000);
   }

}
