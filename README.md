# README #

***Ziqian*** (named after a famous student of the Confucius) is an academic tool maintained in Singapore University of Technology and Design (SUTD), Singapore, to learn a discrete-time Markov Chain (DTMC) from system traces using different algorithms. The learned DTMC can then be used for the purpose of probabilistic model checking or other system analysis tasks like runtime monitoring. 

## Ziqian implements: ##
* AA: AAlergia algorithm documented in the paper ["Learning Probabilistic Automata for Model Checking"](https://ieeexplore.ieee.org/abstract/document/6042035/) 
* GA: Evolutionary algorithm documented in the paper ["Learning Probabilistic Models for Model Checking: an Evolutionary Approach and an Empirical Study"](https://link.springer.com/article/10.1007/s10009-018-0492-7)
* LAR: A property-guided learning algorithm based on abstraction refinement documented in the paper ["Automatically Verifying Discrete-Time Complex Systems through Learning, Abstraction and Refinement"](https://arxiv.org/abs/1610.06371)

### What is this repository for? ###

* This repository is maintained for the development of ZiQian. The evaluation results of multiple systems of submitted research papers are hosted in another neighborhood repository called ziqian_evaluation [here](https://bitbucket.org/jingyi_wang/ziqian_evaluation). 

### How do I set up? ###

* This is a maven project based on jdk1.7 or later.
* You have to install maven to run this project, which can be downloaded [here](http://maven.apache.org/).
* After cloning the project, import the project as a maven project to Eclipse.

### Dependencies ###
The following external tools have to be manually installed to your local maven repository. The required library 'PRISM' and 'javaml' .jar files are already in /ext under project folder. Make sure that Maven has been installed in your system by running "mvn -version" in command line. 

* Install PRISM source to Maven

Execute 'mvn install:install-file -Dfile=./ext/prism.jar -DgroupId=oxford.modelchecker 
    -DartifactId=prism -Dversion=4.2.1 -Dpackaging=jar' in command line
    
* Install javaml to Maven (for SVM)

Execute 'mvn install:install-file -Dfile=./ext/javaml-0.1.7/javaml-0.1.7.jar -DgroupId=net.sf 
    -DartifactId=javaml -Dversion=0.1.7 -Dpackaging=jar' in command line


* Install PRISM tool (for probabilistic model checking)

Download PRISM from [here](https://www.prismmodelchecker.org/download.php). After install PRISM to your machine, update PlatformDependent.PRISM_PATH to the path where PRISM is installed.


After installing the dependencies to your machine, update the project.

### Usage ###
* AA: Learn a probabilistic model from system traces using AAlergia algorithm.

Usage: AA [-hV] [--random_length] [--additional_traces=<additional_trace_path>]
          [--delimiter=<delimiter>] [--epsilon=<max_epsilon>]
          [--length=<data_length>] [--model_setting=<model_setting>]
          [--step=<data_step>] <model_name> <trace_path> <result_path>
          <vars_path>

      <model_name>         The name of the system to learn.  
      <trace_path>         The directory path containing all the system traces.
      <result_path>        The directory path to store the results.
      <vars_path>          The path to the variables to learn from.
      --additional_traces=<additional_trace_path> Add additional traces to learn from.
      --delimiter=<delimiter> The delimiter of the trace files.
      --epsilon=<max_epsilon> The maximum epsilon to choose from.
      --length=<data_length> The total data length.
      --model_setting=<model_setting> The model setting.
      --random_length      Whether the sample length is randmized.
      --step=<data_step>   The sampling frequency.
      -h, --help               Show this help message and exit.
      -V, --version            Print version information and exit.

* GA : Learn a probabilistic model from system traces using evolutionary algorithm.

Usage: GA [-hV] [--random_length] [--additional_traces=<additional_trace_path>]
          [--delimiter=<delimiter>] [--generation=<gen_num>]
          [--length=<data_length>] [--model_setting=<model_setting>]
          [--mutation=<mutation_rate>] [--prob=<select_prob>]
          [--size=<gen_size>] [--step=<data_step>] <model_name> <trace_path>
          <result_path> <vars_path>

      <model_name>           The name of the system to verify.
      <trace_path>           The directory path containing all the system traces.
      <result_path>          The directory path to store the results.
      <vars_path>            The path to the variables to learn from.
      --additional_traces=<additional_trace_path> Add additional traces to learn from.
      --delimiter=<delimiter> The delimiter of the trace files.
      --generation=<gen_num> The number of generations.
      --length=<data_length> The total data length.
      --model_setting=<model_setting> The model setting.
      --mutation=<mutation_rate> The mutation rate.
      --prob=<select_prob>   The probability to select the winner.
      --random_length        Whether the sample length is randmized.
      --size=<gen_size>      The number of chromosomes in each generation.
      --step=<data_step>     The sampling frequency.
      -h, --help                 Show this help message and exit.
      -V, --version              Print version information and exit.
  
  * LAR: Verify a safety property from system traces through learning, abstraction and refinement
  
  Usage: LAR [-hV] [--collect] [--loop] [--random_length] [--alpha=<alpha>]
           [--beta=<beta>] [--delimiter=<delimiter>] [--epsilon=<max_epsilon>]
           [--length=<data_length>] [--max_iter=<max_iteration>]
           [--min_acc=<min_svm_accuracy>] [--sampler=<sampler>]
           [--sigma=<sigma>] [--step=<data_step>] <model_name> <trace_path>
           <property_path> <result_path> <model> <model_setting>

      <model_name>          The name of the system to verify.
      <trace_path>          The directory path containing all the system traces.
      <property_path>       The path to the property to verify.
      <result_path>         The directory path to store the results.
      <model>               The model to sample new traces for hypothesis testing.
      <model_setting>       The model setting to sample new traces for hypothesis
                              testing.
      --alpha=<alpha>       The Type-1 error bound of hypothesis testing.
      --beta=<beta>         The Type-2 error bound of hypothesis testing.
      --collect             Whether to collect all or part of the concrete states.
      --delimiter=<delimiter> The delimiter of the trace files.
      --epsilon=<max_epsilon> The maximum epsilon to choose from.
      --length=<data_length> The total data length.
      --loop                Whether to refine loops first.
      --max_iter=<max_iteration> The maximum number of iterations.
      --min_acc=<min_svm_accuracy> The minimum classification accuracy for SVM.
      --random_length       Whether the sample length is randmized.
      --sampler=<sampler>   The sampler to sample a new path.
      --sigma=<sigma>       The indifference region of hypothesis testing.
      --step=<data_step>    The sampling frequency.
      -h, --help                Show this help message and exit.
      -V, --version             Print version information and exit.
  
### Who do I talk to? ###

* This project is maintained by ***WANG Jingyi (王竟亦)***, contact ***wangjyee@gmail.com*** if you encounter any issues or have suggestions to improve.
