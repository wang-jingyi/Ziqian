dtmc 
 
module swat_0
s:[0..2] init 0; 
[]s=0 -> 0.5:(s'=1) + 0.5 :(s'=2);
[]s=1 -> 0.9997912753078689 :(s'=1) + 2.087246921310791E-4 :(s'=2);
[]s=2 -> 0.125 :(s'=1) + 0.875 :(s'=2);
endmodule 

label "hold" = s=1|s=2;
label "swat_error" = s=1;
