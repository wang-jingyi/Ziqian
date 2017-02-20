# README #

***Ziqian*** (named after a famous student of Confucius) is a research tool actively maintained in Singapore University of Technology and Design, Singapore, to support automatically 'learning' probabilistic models (mainly in the form of discrete-time Markov Chains) from system logs for the purpose of model checking or other system analysis tasks. 

## Ziqian supports:## 
* two categories of learning: from multiple executions and from a single execution. For either case, two types of learning algorithms are implemented: state-of-art tree based algorithms and evolution-based algorithms.
* integrating predicate abstraction and abstraction refinement to the learning framework. Given a safety property of interest, Ziqian starts from the coarsest abstraction and iteratively refine the abstraction by learning new predicates using SVM until Ziqian successfully verifies the property or identify a counterexample.
* actively generating more informative samples to learn more accurate models, where 'active' means that we compute an optimal initial distribution to generate a most useful sample in terms of learning better models.

For all the mentioned features, Ziqian has been evaluated by multiple PRISM benchmark systems, random generated DTMCs, probabilistic boolean networks, as well as a real world water purification system testbed in SUTD. The promising experimental results prove that the theory and Ziqian work smoothly.     

### What is this repository for? ###

* This repository is maintained for the development of tool ZiQian. The evaluation results of multiple systems of submitted research papers are hosted in another neighborhood repository called ziqian_evaluation[here](https://bitbucket.org/jingyi_wang/ziqian_evaluation). 


### Our ongoing research is to apply our proposed approaches into the the real-world Singapore water treatment system testbed to build an environment model from system logs fully automatically. ###

### How do I set up? ###

* This is a maven project based on jdk1.7 or later.
* You have to install maven to run this project, which can be downloaded [here](http://maven.apache.org/).
* There are multiple example case studies in the package 'example'.

### Dependencies ###
The following external tools is not included in maven repository and have to be manually downloaded (they're already in ext/ folder) and installed to your local maven repository. Before this, make sure maven has been installed in your system by running "mvn -version" in command line. The PRISM and javaml jar files are in /ext under project folder.


* PRISM

Execute 'mvn install:install-file -Dfile=./ext/prism.jar -DgroupId=oxford.modelchecker 
    -DartifactId=prism -Dversion=4.2.1 -Dpackaging=jar' in command line



* javaml

Execute 'mvn install:install-file -Dfile=./ext/javaml-0.1.7/javaml-0.1.7.jar -DgroupId=net.sf 
    -DartifactId=javaml -Dversion=0.1.7 -Dpackaging=jar' in command line

We use javaml to apply SVM for generating new predicates for abstraction.

* Gurobi

Gurobi is free for academic use but make sure you acquire a license. Install GUROBI following the instructions and install the license as well. Then,

Execute 'mvn install:install-file -Dfile=./ext/gurobi.jar -DgroupId=com.gurobi.www 
    -DartifactId=gurobi -Dversion=6.5 -Dpackaging=jar' in command line

We use Gurobi for the optimization of initial distribution for active learning. 

After installing all the dependencies, update the project.

### Guidelines ###
* Please follow the examples in package 'example' and guidelines in 'run.Main' and 'run.LearnMain' to write your own case studies. Check run.PlatformDependent to set up the tool paths used. Check run.Config for configuration of the algorithms. The learned model is in PRISM DTMC model format, which can be directly used for model checking using PRISM.

### Who do I talk to? ###

* This project is maintained by ***WANG Jingyi (王竟亦)***, contact ***wangjyee@gmail.com*** if you encounter any issues or have suggestions to improve.