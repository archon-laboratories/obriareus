Obriareus
=========

*By: Sam Beckmann, Nate Beckemeyer*

Utility for testing algorithms in the multi-armed bandit problem with budget constraints.

Changeable Parameters:

* Number of Arms

* Number of Trials

* Mean Reward of Each Arm

* Standard Deviation of Reward for Each Arm

* Cost of Each Arm

* Reward Distributions

* Number of Trials to Run

* Budgets

* Algorithms

Inputting Data
--------------

To input a dataset to the program, create a file in a `datasets` folder your working directory. There are some shortcuts for the syntax of entering data, so the following two sample datasets input the same data:

```
# Distributions to Run
com.samvbeckmann.obriareus.distributions.Gaussian
com.samvbeckmann.obriareus.distributions.Poisson

# Budgets to Run
50
100
150
200
250
300
350
400
450
500

# Number of Trials
1000

# Number of Arms
10

# Arm Costs
1
1
1
1
1
1
1
1
1
1

# Mean Rewards
.1
.2
.3
.4
.5
.6
.7
.8
.9
1

# Standard Deviations
.3
.3
.3
.3
.3
.3
.3
.3
.3
.3

# Algorithms
com.samvbeckmann.obriareus.algorithms.Greedy
com.samvbeckmann.obriareus.algorithms.EFirst, .1
com.samvbeckmann.obriareus.algorithms.EFirst, .2
com.samvbeckmann.obriareus.algorithms.FKUBE
com.samvbeckmann.obriareus.algorithms.FKDE, 10
com.samvbeckmann.obriareus.algorithms.UCBBV1
com.samvbeckmann.obriareus.algorithms.EProgressive, .1
com.samvbeckmann.obriareus.algorithms.EProgressive, .2
com.samvbeckmann.obriareus.algorithms.LSplit, .5
com.samvbeckmann.obriareus.algorithms.SOAAv, 0
```
```
# Distributions to Run
com.samvbeckmann.obriareus.distributions.Gaussian
com.samvbeckmann.obriareus.distributions.Poisson

# Budgets to Run
*
50
500
50

# Number of Trials
1000

# Number of Arms
10

# Arm Costs
*
1

# Mean Rewards
*
linear

# Standard Deviations
*
.3

# Algorithms
com.samvbeckmann.obriareus.algorithms.Greedy
com.samvbeckmann.obriareus.algorithms.EFirst, .1
com.samvbeckmann.obriareus.algorithms.EFirst, .2
com.samvbeckmann.obriareus.algorithms.FKUBE
com.samvbeckmann.obriareus.algorithms.FKDE, 10
com.samvbeckmann.obriareus.algorithms.UCBBV1
com.samvbeckmann.obriareus.algorithms.EProgressive, .1
com.samvbeckmann.obriareus.algorithms.EProgressive, .2
com.samvbeckmann.obriareus.algorithms.LSplit, .5
com.samvbeckmann.obriareus.algorithms.SOAAv, 0

```
To break down the precise syntax:

* **Distributions to Run:** The first line of the dataset is skipped by the interpreter, but should be
                            `# Distributions to Run` or something equally descriptive. The next lines should contain
                            one distribution to run per line. Each distribution run will generate its own output file.
                            The distributions supported by default are Gaussian and Poisson, but more can be added.
                            As in all sections, a blank line should be left after the last dataset to indicate to
                            the interpreter you are finished adding defaultDistributions.

* **Budgets:** The next line is skipped by the interpreter, but should be `# Budgets to Run` or something equally
               descriptive. The next lines should contain one budget per line. Budgets are natural numbers.
               Each budget will run once for each distribution. The shortened syntax for this is indicated by making
               the first line under `# Budgets to Run` equal to `*`. The next line should contain the minimum budget.
               The line after that contains the maximum budget. The final line contains the increment by which the
               budget increases by. An example can be found in the second dataset above.

* **Number of Trials:** The next line is skipped by the interpreter, but should be `# Number of Trials`  or something
                        equally descriptive. The next line contains the number of trials that will be averaged for
                        each algorithm for each budget. We recommend 1000, though when running with high numbers of
                        arms or budgets, it might be necessary to decrease this value in order to achieve timely results
                        Note that the number of trials is a natural number.

* **Number of Arms:** The next line is skipped by the interpreter, but should be `# Number of Arms` or something
                      equally descriptive. The next line contains the number of arms that will be used the bandit.
                      Note that the number of arms is a natural number.

* **Arm Costs:** The next line is skipped by the interpreter, but should be `# Arm Costs` or something equally
                 descriptive. The next lines should be the costs for each arm, iterating from the first arm through the
                 last arm. There should be the the same number of lines in this section at the number of arms specified
                 in the section before. The shortened syntax for this section is indicated by a `*` in the first line of
                 the section. The next line then contains a single cost, which is applied to all the lines.
                 Note that a cost is a double greater than 0.

* **Mean Rewards:** The next line is skipped by the interpreter, but should be `# Mean Rewards` or something equally
                    descriptive. The next lines should be the costs for each arm, iterating from the first arm through
                    the last arm. There should be the same number of lines in this section at the number of arms
                    specified in an earlier section. The shortened syntax for this section is indicated by a `*` in the
                    first line of the section. The next line then contains a distibution to applied to the mean rewards.
                    The accepted notations are `linear`, which spaces the means according to the algorithm.

* **Standard Deviations:** The next line is skipped by the interpreter, but should be `# Standard Deviations` or
                           something equally descriptive. The next lines should be the standard deviations for each arm,
                           iterating from the first arm through the last arm. There should be the the same number
                           of lines in this section at the number of arms specified in a previous section.
                           The shortened syntax for this section is indicated by a `*` in the first line of the section.
                           The next line then contains a single standard deviation, which is applied to all the lines.
                           Note that a standard deviation is a double greater than 0.

* **Algorithms:** The next line is skipped by the interpreter, but should be `# Algorithms` or something equally
                  descriptive. The following lines each initialize one algorithm that will be used by the dataset.
                  Initializing an algorithm is done by writing the fully qualified classpath of the class that contains the algorithm. If the algorithm accepts
                  any other arguments, they are given in doubles separated by a comma and a space. The default
                  algorithms and their arguments are detailed below.

Output
------

The program outputs data in two ways: First, a sample of data is outputted the console. The output lists the
distribution, budget, and algorithm that is being evaluated, followed by the normalized and absolute output of the algorithm
(normalized output is the algorithm's average utility minus the average utility of of all the algorithms).

For plotting purposes, the data is also outputted to the `output` folder in the project as a .txt file under the name
`dataset_distribution_normalized.txt` where dataset is the name of the dataset that is being used, distribution being the
distribution of rewards for that specific output, and normalized being either normalized or absolute, based on what data the file contains. The file is formatted with the first item in each line is the
budget for that line contains the output of, and the remaining items are the rewards of the algorithms,
printed in the order the algorithms were initialized in the dataset. All values are separated by commas.

Default Algorithms
------------------

Obriareus contains 8 algorithms by default:

* **e-first:** This algorithm uniformly selects from the set of arms, performing unordered sweeps of each arm
               before beginning again, until its exploration budget is exhausted (not enough remains to pull even
               the minimum cost arm). At the end of the exploration phase, the arms are sorted by the ratio of their
               estimated mean reward to pulling cost. During exploitation, the best-ranking arm, which we refer to
               as the active arm, is pulled until it cannot be afforded in the remainder of the budget.
               This process is repeated with the best of the remaining affordable arms until the budget is exhausted.
               Note that this algorithm, along with all algorithms included by default, is "online," meaning
               that if an arm other than the active arm becomes the best-ranking arm, the active arm will switch
               to the new best-arm.

* **greedy:** A specific case of the e-first algorithm, greedy pulls each arm exactly once in the exploration phase, 
              and thereafter always pulls the arm with the highest observed average payoff.

* **fKUBE:** The Fractional Knapsack-based
             Upper Confidence Bound Exploration and Exploitation
             (fKUBE) [Tran-Thanh et al., 2010], is based on a class of
             upper-confidence bound (UCB) based algorithms originally
             developed for the MAB problem without budget constraints.
             This algorithm has a minimal exploration phase, pulling each
             arm exactly once, and thereafter uses a confidence bound
             based on the reward-cost ratio of the arms to determine
             which arm should be pulled at time t + 1.

* **fKDE:** The Fractional Knapsack-based Decreasing Epsilon algorithm (fKDE) [Long Tran–Thanh], uses an epsilon value
            to assign each arm (whose cost <= budget) a probability of being pulled. The arm with the best reward/cost
            ratio is given a probability of (1 - epsilon), all other feasible arms are assigned a probability of
            epsilon/K, where K is the number of feasible arms. An arm is selected based on those probabilities to be
            pulled. As the time increases, the epsilon decreases.

* **UCB-BV1:** An upper confidence-bound (UCB) algorithm, UCB-BV1 [Wenkui Ding et al., 2013] explores by pulling each
               arm once, then calculating a "d value" for each arm. The arm with the highest d value is then pulled on
               each pass.

* **l-split:** l-split drops a proportion of the available arms. The drop fraction is given by a passed l-value.
               After each iteration, the drop fraction of the remaining arms is dropped, only the arms with the best
               cost/reward ratio making it to the next iteration.

* **e-progressive:** A special version of the l-split algorithm, e-progressive has a well-defined exploration phase
                     determined by an epsilon passed to the algorithm. The l-value is generated such that the algorithm
                     gets down to one arm at the end of the exploration phase.

* **SOAAv:** Survival of the Above Averge (SOAAv), successively narrows down the set of active arms by eliminating
             underperforming arms. But rather than eliminating a fixed number of arms after each pass, it eliminates
             arms whose estimate reward-cost ratio is below (1 + x) times the average of such ratios of the arms in the
             last pass. Setting x = 0 means only above average individuals survive from one pass of the arms to the
             next. Note again that this is an online-exploration approach where a previously eliminated arm can come
             back into the active set if estimates of other active arms drop.

Default Distributions
---------------------

Obriareus contains 3 different value distributions by default:

* **Constant:** Returns the same mean value consistently.

* **Gaussian:** A normal distribution of values, centered on a passed mean and standard deviation given by a passed
                standard deviation.

* **Poisson:** The poisson distribution of values, where the values center on a mean and only return positive values.
               Calculated using the Knuth algorithm.
               
Extending
---------

It is possible to extend Obriareus to implement your own algorithms and distributions.

To do so, simply import Obriareus as a library or framework, then create your own algorithms and distributions in your project. For algorithms to be recognized by Obriareus, they must implement the `IAlgorithms` interface. Distributions must implement the
`IDistribution` interface. To call new algorithms and distributions, simply put the fully qualified classpath in the
appropriate location in the dataset file.
