# README #

***ZiQian*** is a research tool actively maintained in SUTD, Singapore, to learn probabilistic models (mainly DTMCs) from system logs for model checking. 

* In the first stage, ZiQian supports learning from multiple executions and a single execution. For both cases, two categories of algorithms are implemented: state-of-art tree based algorithms and evolution-based algorithms.
* In the second stage, ZiQian integrates predicate abstraction to the learning framework. ZiQian starts from the coarsest abstraction and iteratively refine it by learning new predicates using SVM.
* In the third stage, ZiQian supports active learning for probabilistic models, where 'active' means we compute an optimal initial distribution for new samples to better estimate the model.

ZiQian has been evaluated by multiple PRISM benchmark systems, random generated DTMCs, probabilistic boolean networks, as well as a real world water purification system testbed in SUTD. The promising experimental results prove that our theory and ZiQian work smoothly.     

### What is this repository for? ###

* This repository is maintained for the development of tool ZiQian. The evaluation results of multiple systems of submitted research papers are hosted in another neighborhood repository called ziqian_evaluation. 


### The current version supports the all three stages described above. Our next plan is to apply our research into the real world Singapore water treatment system testbed to build a complete environment model from system logs fully automatically. ###

### How do I set up? ###

* This is a maven project based on jdk1.7 or later.
* You have to install maven to run this project, which can be downloaded [here](http://maven.apache.org/).
* There are multiple example case studies in the package 'example'.

### Dependencies ###
The following external tools is not included in maven repository and have to be manually installed to your local maven repository. Before this, make sure maven has been installed in your system by running "mvn -version" in command line. The PRISM and javaml jar files are in /ext under project folder.


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
* Follow the examples in 'example' package and guidelines in 'run.Main' and 'run.LearnMain' to write your own case studies. Check run.Config for configuration of the algorithms. The learned model is in PRISM DTMC model format, which can be directly used for model checking using PRISM.

### Who do I talk to? ###

* This project is maintained by ***WANG Jingyi (王竟亦)***, contact ***wangjyee@gmail.com*** if you encounter any issues or have suggestions to improve.