# README #

***Ziqian*** (named after a famous student of Confucius) is an academic tool actively maintained in Singapore University of Technology and Design (SUTD), Singapore, to support automatically 'learning' probabilistic models (mainly in the form of discrete-time Markov Chains for now) from system logs for the purpose of probabilistic model checking or other system analysis tasks like runtime monitoring. 

## Ziqian supports: ##
* two categories of learning: from multiple system executions and from a single system execution. For either case, two types of learning algorithms are implemented: state-of-the-art tree-based algorithms and evolution-based algorithms.
* predicate abstraction and abstraction refinement to the learning framework. Given a safety property to verify, Ziqian starts from the coarsest abstraction and iteratively refine the abstraction by learning new predicates using SVM until Ziqian successfully verifies the property or identify a counterexample.

For the above features, Ziqian has been evaluated by multiple PRISM benchmark systems, random generated DTMCs, probabilistic boolean networks, as well as a real world water treatment system (SWaT) in SUTD.

### What is this repository for? ###

* This repository is maintained for the development of ZiQian. The evaluation results of multiple systems of submitted research papers are hosted in another neighborhood repository called ziqian_evaluation[here](https://bitbucket.org/jingyi_wang/ziqian_evaluation). 

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

After installing the dependencies to your machine, update the project.

### Guidelines ###
* Check run.PlatformDependent to set up the tool paths used.
* Check run.Config for configuration of the algorithms, like where to hold the generated models.
* Follow the guidelines in example.CrowdPositive to write your own property or predicate or atomic propositions.
* Follow the guidelines in 'run.Main' and 'run.LearnMain' to write your own case studies.
* The learned model is in PRISM DTMC model format, which can be directly used for model checking using PRISM.

### Who do I talk to? ###

* This project is maintained by ***WANG Jingyi (王竟亦)***, contact ***wangjyee@gmail.com*** if you encounter any issues or have suggestions to improve.