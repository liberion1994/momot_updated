package at.ac.tuwien.big.moea.search.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.moeaframework.algorithm.AbstractEvolutionaryAlgorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.EpsilonBoxEvolutionaryAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Implementation of the &epsilon;-MOEA algorithm. The &epsilon;-MOEA is a
 * steady-state algorithm, meaning only one individual in the population is
 * evolved per step, and uses an &epsilon;-dominance archive to maintain a
 * well-spread set of Pareto-optimal solutions.
 * <p>
 * References:
 * <ol>
 * <li>Deb et al. "A Fast Multi-Objective Evolutionary Algorithm for Finding
 * Well-Spread Pareto-Optimal Solutions." KanGAL Report No 2003002. Feb 2003.
 * </ol>
 */
public class MyEMOEA extends AbstractEvolutionaryAlgorithm implements EpsilonBoxEvolutionaryAlgorithm {

   /**
    * The dominance comparator used for updating the population.
    */
   private final DominanceComparator dominanceComparator;

   /**
    * The selection operator.
    */
   private final Selection selection;

   /**
    * The variation operator.
    */
   private final Variation variation;

   /**
    * Constructs the &epsilon;-MOEA algorithm with the specified components.
    *
    * @param problem
    *           the problem being solved
    * @param population
    *           the population used to store solutions
    * @param archive
    *           the archive used to store the result
    * @param selection
    *           the selection operator
    * @param variation
    *           the variation operator
    * @param initialization
    *           the initialization method
    */
   public MyEMOEA(final Problem problem, final Population population, final EpsilonBoxDominanceArchive archive,
         final Selection selection, final Variation variation, final Initialization initialization) {
      this(problem, population, archive, selection, variation, initialization, new ParetoDominanceComparator());
   }

   /**
    * Constructs the &epsilon;-MOEA algorithm with the specified components.
    *
    * @param problem
    *           the problem being solved
    * @param population
    *           the population used to store solutions
    * @param archive
    *           the archive used to store the result
    * @param selection
    *           the selection operator
    * @param variation
    *           the variation operator
    * @param initialization
    *           the initialization method
    * @param dominanceComparator
    *           the dominance comparator used by the
    *           {@link #addToPopulation} method
    */
   public MyEMOEA(final Problem problem, final Population population, final EpsilonBoxDominanceArchive archive,
         final Selection selection, final Variation variation, final Initialization initialization,
         final DominanceComparator dominanceComparator) {
      super(problem, population, archive, initialization);
      this.variation = variation;
      this.selection = selection;
      this.dominanceComparator = dominanceComparator;
   }

   /**
    * Adds the new solution to the population if is non-dominated with the
    * current population, removing either a randomly-selected dominated
    * solution or a non-dominated solution.
    *
    * @param newSolution
    *           the new solution being added to the population
    */
   protected void addToPopulation(final Solution newSolution) {
      final List<Integer> dominates = new ArrayList<>();
      boolean dominated = false;

      for(int i = 0; i < population.size(); i++) {
         final int flag = dominanceComparator.compare(newSolution, population.get(i));

         if(flag < 0) {
            dominates.add(i);
         } else if(flag > 0) {
            dominated = true;
         }
      }

      if(!dominates.isEmpty()) {
         population.remove(dominates.get(PRNG.nextInt(dominates.size())));
         population.add(newSolution);
      } else if(!dominated) {
         population.remove(PRNG.nextInt(population.size()));
         population.add(newSolution);
      }
   }

   @Override
   public EpsilonBoxDominanceArchive getArchive() {
      return (EpsilonBoxDominanceArchive) super.getArchive();
   }

   @Override
   public void iterate() {
      Solution[] parents = null;

      if(archive.size() <= 1) {
         parents = selection.select(variation.getArity(), population);
      } else {
         parents = ArrayUtils.add(selection.select(variation.getArity() - 1, population),
               archive.get(PRNG.nextInt(archive.size())));
      }

      PRNG.shuffle(parents);

      final Solution[] children = variation.evolve(parents);
      System.out.println(children.length);
   }

}
