dtmc 
 
module swat_0
s:[0..119] init 0; 
[]s=0 -> 0.028873917228103917:(s'=1) + 0.028873917228103917 :(s'=2) + -0.0 :(s'=3) + 0.02887391722810392 :(s'=4) + -0.0 :(s'=5) + -0.0 :(s'=6) + 0.028873917228103934 :(s'=7) + 0.01339080219274386 :(s'=8) + -0.0 :(s'=9) + -0.0 :(s'=10) + -0.0 :(s'=11) + 0.02887391722810394 :(s'=12) + 0.01339080219274386 :(s'=13) + -0.0 :(s'=14) + -0.0 :(s'=15) + -0.0 :(s'=16) + 0.028873917228103906 :(s'=17) + 0.01339080219274386 :(s'=18) + -0.0 :(s'=19) + -0.0 :(s'=20) + -0.0 :(s'=21) + 0.028873917228103885 :(s'=22) + 0.01339080219274386 :(s'=23) + -0.0 :(s'=24) + -0.0 :(s'=25) + -0.0 :(s'=26) + 0.028873917228103962 :(s'=27) + 0.01339080219274386 :(s'=28) + -0.0 :(s'=29) + -0.0 :(s'=30) + -0.0 :(s'=31) + 0.028873917228103976 :(s'=32) + 0.01339080219274386 :(s'=33) + -0.0 :(s'=34) + -0.0 :(s'=35) + -0.0 :(s'=36) + 0.02887391722810394 :(s'=37) + 0.021425283508390174 :(s'=38) + 0.01339080219274386 :(s'=39) + -0.0 :(s'=40) + -0.0 :(s'=41) + -0.0 :(s'=42) + 0.028873917228103882 :(s'=43) + -0.0 :(s'=44) + -0.0 :(s'=45) + -0.0 :(s'=46) + 0.028873917228103906 :(s'=47) + -0.0 :(s'=48) + -0.0 :(s'=49) + -0.0 :(s'=50) + 0.028873917228103944 :(s'=51) + -0.0 :(s'=52) + -0.0 :(s'=53) + -0.0 :(s'=54) + 0.028873917228103965 :(s'=55) + -0.0 :(s'=56) + -0.0 :(s'=57) + -0.0 :(s'=58) + 0.02866468594384232 :(s'=59) + -0.0 :(s'=60) + -0.0 :(s'=61) + 2.0923128426162286E-4 :(s'=62) + 0.028455454659580725 :(s'=63) + -0.0 :(s'=64) + 2.092312842616228E-4 :(s'=65) + 2.09231284261623E-4 :(s'=66) + 0.028455454659580697 :(s'=67) + 2.0923128426162286E-4 :(s'=68) + 2.09231284261623E-4 :(s'=69) + -0.0 :(s'=70) + 0.0284554546595807 :(s'=71) + 2.092312842616229E-4 :(s'=72) + -0.0 :(s'=73) + -0.0 :(s'=74) + 0.028455454659580676 :(s'=75) + -0.0 :(s'=76) + -0.0 :(s'=77) + -0.0 :(s'=78) + 0.028455454659580708 :(s'=79) + -0.0 :(s'=80) + -0.0 :(s'=81) + -0.0 :(s'=82) + 0.02845545465958073 :(s'=83) + -0.0 :(s'=84) + -0.0 :(s'=85) + -0.0 :(s'=86) + 0.028455454659580715 :(s'=87) + -0.0 :(s'=88) + -0.0 :(s'=89) + -0.0 :(s'=90) + 0.02845545465958069 :(s'=91) + -0.0 :(s'=92) + -0.0 :(s'=93) + -0.0 :(s'=94) + 0.02803699209105746 :(s'=95) + -0.0 :(s'=96) + -0.0 :(s'=97) + 4.1846256852324555E-4 :(s'=98) + 0.027618529522534224 :(s'=99) + -0.0 :(s'=100) + 4.1846256852324555E-4 :(s'=101) + 4.1846256852324566E-4 :(s'=102) + 0.023852366405825002 :(s'=103) + 4.1846256852324577E-4 :(s'=104) + 4.184625685232455E-4 :(s'=105) + 0.0037661631167092103 :(s'=106) + 0.01862158429928443 :(s'=107) + 4.1846256852324555E-4 :(s'=108) + 0.003766163116709209 :(s'=109) + 0.018621584299284427 :(s'=110) + 0.0052307821065405696 :(s'=111) + 0.017156965309453074 :(s'=112) + 0.00376616311670921 :(s'=113) + 0.00523078210654057 :(s'=114) + 0.018621584299284427 :(s'=115) + 0.008996945223249779 :(s'=116) + 0.016110808888144954 :(s'=117) + 0.015901577603883336 :(s'=118) + 0.0052307821065405696 :(s'=119);
[]s=1 -> 1.0 :(s'=2);
[]s=2 -> 1.0 :(s'=4);
[]s=3 -> 1:(s'=3);
[]s=4 -> 1.0 :(s'=7);
[]s=5 -> 1:(s'=5);
[]s=6 -> 1:(s'=6);
[]s=7 -> 1.0 :(s'=12);
[]s=8 -> 1.0 :(s'=13);
[]s=9 -> 1:(s'=9);
[]s=10 -> 1:(s'=10);
[]s=11 -> 1:(s'=11);
[]s=12 -> 1.0 :(s'=17);
[]s=13 -> 1.0 :(s'=18);
[]s=14 -> 1:(s'=14);
[]s=15 -> 1:(s'=15);
[]s=16 -> 1:(s'=16);
[]s=17 -> 1.0 :(s'=22);
[]s=18 -> 1.0 :(s'=23);
[]s=19 -> 1:(s'=19);
[]s=20 -> 1:(s'=20);
[]s=21 -> 1:(s'=21);
[]s=22 -> 1.0 :(s'=27);
[]s=23 -> 1.0 :(s'=28);
[]s=24 -> 1:(s'=24);
[]s=25 -> 1:(s'=25);
[]s=26 -> 1:(s'=26);
[]s=27 -> 1.0 :(s'=32);
[]s=28 -> 1.0 :(s'=33);
[]s=29 -> 1:(s'=29);
[]s=30 -> 1:(s'=30);
[]s=31 -> 1:(s'=31);
[]s=32 -> 1.0 :(s'=37);
[]s=33 -> 1.0 :(s'=39);
[]s=34 -> 1:(s'=34);
[]s=35 -> 1:(s'=35);
[]s=36 -> 1:(s'=36);
[]s=37 -> 1.0 :(s'=43);
[]s=38 -> 0.6153846153846154 :(s'=1) + 0.38461538461538464 :(s'=38);
[]s=39 -> 0.015384615384615385 :(s'=1) + 0.9846153846153847 :(s'=38);
[]s=40 -> 1:(s'=40);
[]s=41 -> 1:(s'=41);
[]s=42 -> 1:(s'=42);
[]s=43 -> 1.0 :(s'=47);
[]s=44 -> 1:(s'=44);
[]s=45 -> 1:(s'=45);
[]s=46 -> 1:(s'=46);
[]s=47 -> 1.0 :(s'=51);
[]s=48 -> 1:(s'=48);
[]s=49 -> 1:(s'=49);
[]s=50 -> 1:(s'=50);
[]s=51 -> 1.0 :(s'=55);
[]s=52 -> 1:(s'=52);
[]s=53 -> 1:(s'=53);
[]s=54 -> 1:(s'=54);
[]s=55 -> 0.9927536231884058 :(s'=59) + 0.007246376811594203 :(s'=62);
[]s=56 -> 1:(s'=56);
[]s=57 -> 1:(s'=57);
[]s=58 -> 1:(s'=58);
[]s=59 -> 0.9927007299270073 :(s'=63) + 0.0072992700729927005 :(s'=66);
[]s=60 -> 1:(s'=60);
[]s=61 -> 1:(s'=61);
[]s=62 -> 1.0 :(s'=65);
[]s=63 -> 1.0 :(s'=67);
[]s=64 -> 1:(s'=64);
[]s=65 -> 1.0 :(s'=68);
[]s=66 -> 1.0 :(s'=69);
[]s=67 -> 1.0 :(s'=71);
[]s=68 -> 1.0 :(s'=8);
[]s=69 -> 1.0 :(s'=72);
[]s=70 -> 1:(s'=70);
[]s=71 -> 1.0 :(s'=75);
[]s=72 -> 1.0 :(s'=8);
[]s=73 -> 1:(s'=73);
[]s=74 -> 1:(s'=74);
[]s=75 -> 1.0 :(s'=79);
[]s=76 -> 1:(s'=76);
[]s=77 -> 1:(s'=77);
[]s=78 -> 1:(s'=78);
[]s=79 -> 1.0 :(s'=83);
[]s=80 -> 1:(s'=80);
[]s=81 -> 1:(s'=81);
[]s=82 -> 1:(s'=82);
[]s=83 -> 1.0 :(s'=87);
[]s=84 -> 1:(s'=84);
[]s=85 -> 1:(s'=85);
[]s=86 -> 1:(s'=86);
[]s=87 -> 1.0 :(s'=91);
[]s=88 -> 1:(s'=88);
[]s=89 -> 1:(s'=89);
[]s=90 -> 1:(s'=90);
[]s=91 -> 0.9852941176470589 :(s'=95) + 0.014705882352941176 :(s'=98);
[]s=92 -> 1:(s'=92);
[]s=93 -> 1:(s'=93);
[]s=94 -> 1:(s'=94);
[]s=95 -> 0.9850746268656716 :(s'=99) + 0.014925373134328358 :(s'=102);
[]s=96 -> 1:(s'=96);
[]s=97 -> 1:(s'=97);
[]s=98 -> 1.0 :(s'=101);
[]s=99 -> 0.8636363636363636 :(s'=103) + 0.13636363636363635 :(s'=106);
[]s=100 -> 1:(s'=100);
[]s=101 -> 1.0 :(s'=104);
[]s=102 -> 1.0 :(s'=105);
[]s=103 -> 0.7807017543859649 :(s'=107) + 0.21929824561403508 :(s'=111);
[]s=104 -> 1.0 :(s'=8);
[]s=105 -> 1.0 :(s'=108);
[]s=106 -> 1.0 :(s'=109);
[]s=107 -> 0.9213483146067416 :(s'=112) + 0.07865168539325842 :(s'=110);
[]s=108 -> 1.0 :(s'=8);
[]s=109 -> 1.0 :(s'=113);
[]s=110 -> 1.0 :(s'=115);
[]s=111 -> 1.0 :(s'=114);
[]s=112 -> 0.9390243902439024 :(s'=117) + 0.06097560975609756 :(s'=110);
[]s=113 -> 1.0 :(s'=8);
[]s=114 -> 1.0 :(s'=119);
[]s=115 -> 0.14606741573033707 :(s'=1) + 0.8539325842696629 :(s'=118);
[]s=116 -> 1.0 :(s'=110);
[]s=117 -> 0.5584415584415584 :(s'=116) + 0.44155844155844154 :(s'=110);
[]s=118 -> 0.8026315789473685 :(s'=1) + 0.19736842105263158 :(s'=8);
[]s=119 -> 1.0 :(s'=8);
endmodule 

label "hold" = s=1|s=2|s=3|s=4|s=5|s=6|s=7|s=8|s=9|s=10|s=11|s=12|s=13|s=14|s=15|s=16|s=17|s=18|s=19|s=20|s=21|s=22|s=23|s=24|s=25|s=26|s=27|s=28|s=29|s=30|s=31|s=32|s=33|s=34|s=35|s=36|s=37|s=38|s=39|s=40|s=41|s=42|s=43|s=44|s=45|s=46|s=47|s=48|s=49|s=50|s=51|s=52|s=53|s=54|s=55|s=56|s=57|s=58|s=59|s=60|s=61|s=62|s=63|s=64|s=65|s=66|s=67|s=68|s=69|s=70|s=71|s=72|s=73|s=74|s=75|s=76|s=77|s=78|s=79|s=80|s=81|s=82|s=83|s=84|s=85|s=86|s=87|s=88|s=89|s=90|s=91|s=92|s=93|s=94|s=95|s=96|s=97|s=98|s=99|s=100|s=101|s=102|s=103|s=104|s=105|s=106|s=107|s=108|s=109|s=110|s=111|s=112|s=113|s=114|s=115|s=116|s=117|s=118|s=119;
label "swat_error" = s=3|s=5|s=6|s=8|s=9|s=10|s=11|s=13|s=14|s=15|s=16|s=18|s=19|s=20|s=21|s=23|s=24|s=25|s=26|s=28|s=29|s=30|s=31|s=33|s=34|s=35|s=36|s=38|s=39|s=40|s=41|s=42|s=44|s=45|s=46|s=48|s=49|s=50|s=52|s=53|s=54|s=56|s=57|s=58|s=60|s=61|s=62|s=64|s=65|s=66|s=68|s=69|s=70|s=72|s=73|s=74|s=76|s=77|s=78|s=80|s=81|s=82|s=84|s=85|s=86|s=88|s=89|s=90|s=92|s=93|s=94|s=96|s=97|s=98|s=100|s=101|s=102|s=104|s=105|s=106|s=108|s=109|s=110|s=111|s=113|s=114|s=115|s=118|s=119;
