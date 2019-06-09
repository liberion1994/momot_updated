package at.ac.tuwien.big.moea.search.algorithm;

import java.util.ArrayList;
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
public class MyNSGAII extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

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
   public MyNSGAII(final Problem problem, final NondominatedSortingPopulation population,
         final EpsilonBoxDominanceArchive archive, final Selection selection, final Variation variation,
         final Initialization initialization) {
      super(problem, population, archive, initialization);
      this.selection = selection;
      this.variation = variation;
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

      evaluateAll(offspring);

      if(archive != null) {
         archive.addAll(offspring);
      }

      population.addAll(offspring);
      population.truncate(populationSize);

      // final String res = MySearchContext.evaluate(offspring);
      // // System.out.println(res);
      // try {
      // MySearchContext.fw.write(MySearchContext.log(offspring) + "\n");
      // // MySearchContext.fw.write(res + "\n");
      // } catch(final IOException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
   }

}
