dtmc 
 
module swat_9
s:[0..109] init 0; 
[]s=0 -> 0.03545978190007522:(s'=1) + 0.034750586227705 :(s'=2) + 0.034041390253731804 :(s'=3) + 0.03333219132685504 :(s'=4) + 0.03262296350220789 :(s'=5) + 0.031913453012785344 :(s'=6) + 0.03120117903194122 :(s'=7) + 0.030461904342939824 :(s'=8) + 0.02945922269730136 :(s'=9) + 0.03160199190451466 :(s'=10) + 0.03083121161416065 :(s'=11) + 0.030060431323806633 :(s'=13) + 0.02928965103345262 :(s'=15) + 0.028518870743098598 :(s'=17) + 0.0277480904527446 :(s'=19) + 0.02697731016239056 :(s'=21) + 0.026206529872036553 :(s'=23) + 0.02543574958168254 :(s'=25) + 0.0246649692913285 :(s'=27) + 0.023894189000974526 :(s'=29) + 0.023123408710620488 :(s'=31) + 0.022352628420266467 :(s'=33) + 0.021581848129912453 :(s'=35) + 0.02081106783955842 :(s'=37) + 0.020040287549204415 :(s'=39) + 0.0192695072588504 :(s'=41) + 0.01849872696849638 :(s'=43) + 0.017727946678142363 :(s'=45) + 0.016957166387788356 :(s'=47) + 0.016186386097434346 :(s'=49) + 0.015415605807080325 :(s'=51) + 0.014644825516726297 :(s'=53) + 0.0138740452263723 :(s'=55) + 0.01310326493601827 :(s'=57) + 0.012332484645664263 :(s'=59) + 0.011561704355310244 :(s'=61) + 0.010790924064956227 :(s'=63) + 0.010020143774602207 :(s'=65) + 0.009249363484248194 :(s'=67) + 0.008478583193894173 :(s'=69) + 0.0077078029035401695 :(s'=71) + 0.00693702261318615 :(s'=73) + 0.006166242322832131 :(s'=75) + 0.005395462032478113 :(s'=77) + 0.004624681742124097 :(s'=79) + 0.0038539014517700813 :(s'=81) + 0.0030831211614160657 :(s'=83) + 0.0023123408710620493 :(s'=85) + 0.0015415605807080329 :(s'=87) + 7.707802903540164E-4 :(s'=89) + 7.70780290430247E-4 :(s'=91) + 7.707802911925569E-4 :(s'=93) + 7.707802988156596E-4 :(s'=95) + 7.707803750466852E-4 :(s'=97) + 7.707811373577004E-4 :(s'=99) + 7.707887605432444E-4 :(s'=101) + 7.708649999388441E-4 :(s'=103) + 7.716281486573042E-4 :(s'=105) + 0.03616897754165387 :(s'=107) + 7.793358660135668E-4 :(s'=108);
[]s=1 -> 1.0 :(s'=2);
[]s=2 -> 1.0 :(s'=3);
[]s=3 -> 1.0 :(s'=4);
[]s=4 -> 1.0 :(s'=5);
[]s=5 -> 1.0 :(s'=6);
[]s=6 -> 1.0 :(s'=7);
[]s=7 -> 1.0 :(s'=8);
[]s=8 -> 1.0 :(s'=9);
[]s=9 -> 1.0 :(s'=10);
[]s=10 -> 1.0 :(s'=11);
[]s=11 -> 1.0 :(s'=13);
[]s=12 -> 1:(s'=12);
[]s=13 -> 1.0 :(s'=15);
[]s=14 -> 1:(s'=14);
[]s=15 -> 1.0 :(s'=17);
[]s=16 -> 1:(s'=16);
[]s=17 -> 1.0 :(s'=19);
[]s=18 -> 1:(s'=18);
[]s=19 -> 1.0 :(s'=21);
[]s=20 -> 1:(s'=20);
[]s=21 -> 1.0 :(s'=23);
[]s=22 -> 1:(s'=22);
[]s=23 -> 1.0 :(s'=25);
[]s=24 -> 1:(s'=24);
[]s=25 -> 1.0 :(s'=27);
[]s=26 -> 1:(s'=26);
[]s=27 -> 1.0 :(s'=29);
[]s=28 -> 1:(s'=28);
[]s=29 -> 1.0 :(s'=31);
[]s=30 -> 1:(s'=30);
[]s=31 -> 1.0 :(s'=33);
[]s=32 -> 1:(s'=32);
[]s=33 -> 1.0 :(s'=35);
[]s=34 -> 1:(s'=34);
[]s=35 -> 1.0 :(s'=37);
[]s=36 -> 1:(s'=36);
[]s=37 -> 1.0 :(s'=39);
[]s=38 -> 1:(s'=38);
[]s=39 -> 1.0 :(s'=41);
[]s=40 -> 1:(s'=40);
[]s=41 -> 1.0 :(s'=43);
[]s=42 -> 1:(s'=42);
[]s=43 -> 1.0 :(s'=45);
[]s=44 -> 1:(s'=44);
[]s=45 -> 1.0 :(s'=47);
[]s=46 -> 1:(s'=46);
[]s=47 -> 1.0 :(s'=49);
[]s=48 -> 1:(s'=48);
[]s=49 -> 1.0 :(s'=51);
[]s=50 -> 1:(s'=50);
[]s=51 -> 1.0 :(s'=53);
[]s=52 -> 1:(s'=52);
[]s=53 -> 1.0 :(s'=55);
[]s=54 -> 1:(s'=54);
[]s=55 -> 1.0 :(s'=57);
[]s=56 -> 1:(s'=56);
[]s=57 -> 1.0 :(s'=59);
[]s=58 -> 1:(s'=58);
[]s=59 -> 1.0 :(s'=61);
[]s=60 -> 1:(s'=60);
[]s=61 -> 1.0 :(s'=63);
[]s=62 -> 1:(s'=62);
[]s=63 -> 1.0 :(s'=65);
[]s=64 -> 1:(s'=64);
[]s=65 -> 1.0 :(s'=67);
[]s=66 -> 1:(s'=66);
[]s=67 -> 1.0 :(s'=69);
[]s=68 -> 1:(s'=68);
[]s=69 -> 1.0 :(s'=71);
[]s=70 -> 1:(s'=70);
[]s=71 -> 1.0 :(s'=73);
[]s=72 -> 1:(s'=72);
[]s=73 -> 1.0 :(s'=75);
[]s=74 -> 1:(s'=74);
[]s=75 -> 1.0 :(s'=77);
[]s=76 -> 1:(s'=76);
[]s=77 -> 1.0 :(s'=79);
[]s=78 -> 1:(s'=78);
[]s=79 -> 1.0 :(s'=81);
[]s=80 -> 1:(s'=80);
[]s=81 -> 1.0 :(s'=83);
[]s=82 -> 1:(s'=82);
[]s=83 -> 1.0 :(s'=85);
[]s=84 -> 1:(s'=84);
[]s=85 -> 1.0 :(s'=87);
[]s=86 -> 1:(s'=86);
[]s=87 -> 1.0 :(s'=89);
[]s=88 -> 1:(s'=88);
[]s=89 -> 1.0 :(s'=91);
[]s=90 -> 1:(s'=90);
[]s=91 -> 1.0 :(s'=93);
[]s=92 -> 1:(s'=92);
[]s=93 -> 1.0 :(s'=95);
[]s=94 -> 1:(s'=94);
[]s=95 -> 1.0 :(s'=97);
[]s=96 -> 1:(s'=96);
[]s=97 -> 1.0 :(s'=99);
[]s=98 -> 1:(s'=98);
[]s=99 -> 1.0 :(s'=101);
[]s=100 -> 1:(s'=100);
[]s=101 -> 1.0 :(s'=103);
[]s=102 -> 1:(s'=102);
[]s=103 -> 1.0 :(s'=105);
[]s=104 -> 1:(s'=104);
[]s=105 -> 1.0 :(s'=108);
[]s=106 -> 1:(s'=106);
[]s=107 -> 0.9955514649486118 :(s'=107) + 0.0044485350513882495 :(s'=1);
[]s=108 -> 1.0 :(s'=107);
[]s=109 -> 1:(s'=109);
endmodule 

label "hold" = s=1|s=2|s=3|s=4|s=5|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "swat_error" = s=10;
label "learned_predicate_0" = s=1|s=2|s=3|s=4|s=5|s=6|s=7|s=8|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_1" = s=1|s=2|s=3|s=4|s=5|s=6|s=7|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_2" = s=1|s=2|s=3|s=4|s=5|s=6|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_3" = s=1|s=2|s=3|s=4|s=5|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_4" = s=1|s=2|s=3|s=4|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_5" = s=1|s=2|s=3|s=5|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_6" = s=1|s=2|s=4|s=5|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_7" = s=1|s=3|s=4|s=5|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
label "learned_predicate_8" = s=2|s=3|s=4|s=5|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109;
