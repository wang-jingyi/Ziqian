dtmc 
 
module swat_0
s:[0..2] init 0; 
[]s=0 -> 0.0:(s'=1) + 1.0 :(s'=2);
[]s=1 -> 0.9821428571428571 :(s'=1) + 0.017857142857142856 :(s'=2);
[]s=2 -> 1.0 :(s'=2);
endmodule 

label "hold" = s=1|s=2;
label "swat_error" = s=1;
