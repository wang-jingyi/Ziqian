dtmc 
 
module singtel
s:[0..2] init 0; 
[]s=0 -> 0.7379643669446595:(s'=1) + 0.2620356330553405 :(s'=2);
[]s=1 -> 0.9993579041993066 :(s'=1) + 6.420958006934634E-4 :(s'=2);
[]s=2 -> 0.0018083182640144665 :(s'=1) + 0.9981916817359855 :(s'=2);
endmodule 

label "hold" = s=1|s=2;
