dtmc 
 
module swat_1
s:[0..3] init 0; 
[]s=0 -> 0.3333333333333333:(s'=1) + 0.3333333333333333 :(s'=2) + 0.3333333333333333 :(s'=3);
[]s=1 -> 0.9997892962494732 :(s'=1) + 2.1070375052675939E-4 :(s'=2);
[]s=2 -> 0.9807692307692307 :(s'=2) + 0.019230769230769232 :(s'=3);
[]s=3 -> 1.0 :(s'=1);
endmodule 

label "hold" = s=1|s=2|s=3;
label "swat_error" = s=2|s=3;
label "learned_predicate_1" = s=3;
