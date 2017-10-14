dtmc 
 
module swat_0
s:[0..2] init 0; 
[]s=0 -> 0.5:(s'=1) + 0.5 :(s'=2);
[]s=1 -> 0.9992673992673993 :(s'=1) + 7.326007326007326E-4 :(s'=2);
[]s=2 -> 1.0 :(s'=2);
endmodule 

label "hold" = s=1|s=2;
label "swat_error" = s=2;
