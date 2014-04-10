A basic genetic algorithm framework, written in Java.
Individual fitnesses are calculated in SMP parallel, and the framework assumes that such calculations are thread safe.

## Usage ##

The framework works via subclassing.
The classes that must be subclassed are as follows, along with the specific methods.

### Genotype ###
- `getPhenotype`: gets the underlying genotype representation, be it an array, matrix, tree, bit string, etc.
  The framework itself does not call this, but it will almost certainly be needed in subclasses.
- `toString`: gets a string representation of the genotype.
  This is not very important for the framework itself, but is is  needed in order to see the results of the GA.
  For example, with the included large parsimony example, phylogenetic trees are printed out via this method.
- `crossover`: Given another genotype, returns a new genotype resulting from crossover between this genotype and the other genotype.
  `getPhenotype()` almost assuredly must be called  within in order to perform this operation.
- `mutate`: Returns a new mutated copy of this genotype.
  Note that it is passed the mutation rate as a parameter.
  This mutation rate can be used in arbitrary ways by this method.
  For example, on a bit string, it can be the probability that any given bit be manipulated.
  One convention is that if no mutations occurred, then `null` is returned.

### Individual ###
- Empty constructor: Generates a new individual with a random genotype.
  Should be called via `this( makeRandomGenotype() )`
- Constructor that takes a genotype as a parameter: creates a new individual with the given genotype.
  Should merely call `super( genotype )`
- Constructor that takes a single string as a parameter: this allows for a parameter to be passed to individuals at runtime.
  In the large parsimony problem solver, it is used to pass in a multiple sequence alignment file.
- `calculateFitness`: Generates a fitness value that corresponds to this individual's genotype.
  Note that this is the specific method that is called in parallel between different individuals.
  Also note that low fitness values could be interpreted as good or bad; this behavior is determined by a runtime parameter.

Once subclasses are made, the framework can be invoked like so:

```console
java RunGA 0.5 0.5 0.5 100 TruncationSelection TruncationSelection ConvergenceTerminator:1000 true PhylogeneticTreeIndividual:parsimony/msa/fasta/YCF1/input.aln
```

These are the framework parameters:

1. Crossover rate (between 0-1)
2. Mutation rate (between 0-1)
3. Elitism rate (between 0-1)
4. Max population size (>0)
5. Parent selection mechanism (class name)
6. Survival selection mechanism (class name)
7. Termination condition (class name:number of generations)
8. `true` if lower fitness values are better than higher ones, else `false`.
9. Individual class `name:string` parameter to individual constructor

### Included Selection Mechanisms ###
1. Truncation Selection: merely selects the most fit individuals in the population.
   Class name: `TruncationSelection`.
2. Roulette Wheel Selection: individuals with better fitness have a proportionally better chance of being chosen than individuals with worse fitness.
   Class name: `RouletteWheelSelection`.
3. Binary Tournament Selection: Two individuals are randomly chosen, and the more fit of the two is selected.
   This is repeated until a specified amount have been selected. 
   Class name: `BinaryTournamentSelection`.

### Adding Selection Mechanisms ###
The `Selection` class must be subclassed.
The neccessary methods are as follows:
- Boolean constructor: Gets whether or not low fitness values correspond to more fit individuals.
  Should merely call `super( boolean )`
- `performSelection`: Takes a list holding the parent population, the number of individuals to choose, and whether or not low fitness values correspond to more fit individuals.
  It is expected that the returned list will be of the same size as the number of individuals specified.
  Note that it is perfectly acceptable to select the same individuals multiple times while completely skipping over others.
  For that matter, both the included Roulette Wheel Selection and Binary Tournament Selection mechanisms exploit this.

### Included Termination Conditions ###
1. Number of Generations Terminator: Stops the GA once the given number of generations have passed. 
   Class name: `NumGenerationsTerminator`.
2. Convergence Detection Terminator: Takes the average fitness over a given number of generations.
   Stops the GA once the average fitness stops improving.  

### Adding Termination Conditions ###
The `Terminator` class must be subclassed.
The methods that must be overridden are as follows:
- Constructor that takes a number of generations and whether or not low fitness is good.
  Performs any initialization.
  This is more or less completely user-defined with no constraints required by the framework.
  However, if one ignores both these values it's not particularly useful!
- `shouldTerminate`: Takes the current population, and returns true if the GA should terminate.

## Included Example ##
The large parsimony problem is relevant to Biology, and is useful for understanding evolutionary relationships.
The included individual (`PhylogeneticTreeIndividual`) takes a multiple sequence alignment in ClustalW format and generates a tree from it.
In testing, it has been shown to edge out the neighbor joining algorithm when the GA is properly tuned, though it takes magnitudes more CPU power to do this.  


## Notes of Interest ##
This framework has been applied to two other problems.
Code for these have not been included.
A description of these two problems follows:

1. This one was an experimental hackish attempt at using a GA to tune a neural network without backpropagation.
On a binary choice, it got at best 55% accurracy.
On the same problem, other methods get well over 90% accurracy without difficulty.
2. This was an attempt to choose which inputs would be best for backpropagation on a neural network.
Other methods based on statistics were shown to produce results that were just as good in a tiny fraction of the time.

If anyone really wants, I can release these.

Personally, I have found that the mutation, crossover, and elitism
rates to be highly problem-specific.  I have had the best luck with
the truncation selection mechanism, but that might just be due to the
nature of the problems the operator was applied to.  

## Compiling ##
A simple call to `make` is necessary, like so:

```console
cd src
make
```

