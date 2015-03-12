Multi-Armed core.Bandit
==================

*By: Sam Beckmann, Nate Beckemeyer*

Utility for testing algorithms in the multi-armed bandit problem with budget constraints.

Changeable Parameters:

* Number of Arms

* Number of Trials

* Mean Reward of Each core.Arm

* Standard Deviation of Reward for Each core.Arm

* Cost of Each core.Arm

* Reward Distributions

* Number of Trials to Run

* Budgets

Inputting Data
--------------

To input a dataset to the program, create a file named `datasetXXX.dat` where `XXX` can be anything.
Place this file in the `datasets` folder, one level under Multi-Armed core.Bandit.
The following two datasets input the same information.

`datasetSample1.dat` :

```
# Distributions to Run
Gaussian
Poisson

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

# core.Arm Costs
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

# defaultAlgorithms.Algorithms
greedy
eFirst, .1
eFirst, .2
fKUBE
UCBBV
eProgressive, .1
eProgressive, .2
lSplit, .5
SOAAv, 0
```
`datasetSample2.dat` :
```
# Distributions to Run
Gaussian
Poisson

# Budgets to Run
*
50
500
50

# Number of Trials
1000

# Number of Arms
10

# core.Arm Costs
*
1

# Mean Rewards
*
linear

# Standard Deviations
*
.3

# defaultAlgorithms.Algorithms
greedy
eFirst, .1
eFirst, .2
fKUBE
fKDE
UCBBV
eProgressive, .1
eProgressive, .2
lSplit, .5
SOAAv, 0

```
To break down the precise syntax:

* **Distributions to Run:** The first line of the dataset is skipped by the interpretor, but should be `# Distributions to Run` or something equally descriptive. The next lines should contain one distribution to run per line. Each distribution run will generate its own output file. The defaultDistributions supported by default are Gaussian and Poisson, but more can be added. As in all sections, a blank line should be left after the last dataset to indicate to the interpretor you are finsihed adding defaultDistributions.

* **Budgets:** The next line is skipped by the interpretor, but should be `# Budgets to Run` or something equally descriptive. The next lines should contain one budget per line. Budgets are natural numbers. Each budget will run once for each distribution. The shortened syntax for this is indicated by making the first line under `# Budgets to Run` equal to `*`. The next line should contain the minimum budget. The line after that contains the maximum budget. The final line contains the increment by which the budget increases by. An example can be found in the `Sample2` dataset.

* **Number of Trials:** The next line is skipped by the interpretor, but should be `# Number of Trials` or something equally descriptive. The next line contains the number of trials that will be averaged for each algorithm for each budget. We recommend 1000, though when running with high numbers of arms or budgets, it might be necessary to decrease this value in order to achieve timely results. Note that the number of trials is a natural number.

* **Number of Arms:** The next line is skipped by the interpretor, but should be `# Number of Arms` or something equally descriptive. The next line contains the number of arms that will be used the bandit. Note that the number of arms is a natural number.

* **core.Arm Costs:**