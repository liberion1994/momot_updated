package at.ac.tuwien.big.moea.search.algorithm;

import org.moeaframework.algorithm.AbstractAlgorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;

/**
 * Random search implementation. An {@link Initialization} instance is used
 * to generate random solutions, which are evaluated and all non-dominated
 * solutions retained. The result is the set of all non-dominated solutions.
 */
public class MyRandom extends AbstractAlgorithm {

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
   public MyRandom(final Problem problem, final Initialization generator, final NondominatedPopulation archive) {
      super(problem);
      this.generator = generator;
      this.archive = archive;
   }

   @Override
   public NondominatedPopulation getResult() {
      return archive;
   }

   @Override
   protected void initialize() {
      super.initialize();
      iterate();
   }

   @Override
   protected void iterate() {
      final Population solutions = new Population(generator.initialize());
      evaluateAll(solutions);
      archive.addAll(solutions);
   }

}
