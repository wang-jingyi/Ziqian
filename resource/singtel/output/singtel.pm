dtmc 
 
module singtel
s:[0..10] init 0; 
[]s=0 -> 0.5543622374796041:(s'=1) + 7.097476945521897E-4 :(s'=2) + 5.717411983892641E-4 :(s'=3) + 4.928803434390207E-4 :(s'=4) + 4.6330752283267943E-4 :(s'=5) + 4.5344991596389894E-4 :(s'=6) + 4.4359230909511856E-4 :(s'=7) + 4.435923090951186E-4 :(s'=8) + 3.3515863353853403E-4 :(s'=9) + 0.4417242925934902 :(s'=10);
[]s=1 -> 0.9987197041093941 :(s'=1) + 0.001280295890605829 :(s'=2);
[]s=2 -> 0.19444444444444445 :(s'=1) + 0.8055555555555556 :(s'=3);
[]s=3 -> 0.13793103448275862 :(s'=1) + 0.8620689655172413 :(s'=4);
[]s=4 -> 0.06 :(s'=1) + 0.94 :(s'=5);
[]s=5 -> 0.02127659574468085 :(s'=1) + 0.9787234042553191 :(s'=6);
[]s=6 -> 0.021739130434782608 :(s'=1) + 0.9782608695652174 :(s'=7);
[]s=7 -> 1.0 :(s'=8);
[]s=8 -> 0.24444444444444444 :(s'=1) + 0.7555555555555555 :(s'=9);
[]s=9 -> 0.08823529411764706 :(s'=1) + 0.9117647058823529 :(s'=10);
[]s=10 -> 6.918021445866483E-4 :(s'=1) + 0.9993081978554134 :(s'=10);
endmodule 

label "hold" = s=1|s=2|s=3|s=4|s=5|s=6|s=7|s=8|s=9|s=10;
