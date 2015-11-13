#!/bin/bash

for x in *.log; do mv $x `echo $x | tr ',' '.'`;done;

rm beacon_{A,B,C}.log beacon_{A,B,C,all}_mean.txt beacon_{A,B,C,all}-0-1500_mean.txt

for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350 500 1000 1500; do sed -i "s/$/, $x/" beacon_{A,B,C}_2.000_$x.000.log; done;

for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350; do cat beacon_A_2.000_$x.000.log >> beacon_A.log; done;
for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350; do cat beacon_B_2.000_$x.000.log >> beacon_B.log; done;
for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350; do cat beacon_C_2.000_$x.000.log >> beacon_C.log; done;

for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350; do echo A, $(python -c "import sys; import statistics; print(statistics.mean([int(x) for x in sys.argv[1:]]))" `cat beacon_A_2.000_$x.000.log | cut -d',' -f 3 | tr '\n' ' '`), $x >> beacon_A_mean.txt; done;

for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350; do echo B, $(python -c "import sys; import statistics; print(statistics.mean([int(x) for x in sys.argv[1:]]))" `cat beacon_B_2.000_$x.000.log | cut -d',' -f 3 | tr '\n' ' '`), $x >> beacon_B_mean.txt; done;

for x in 0 25 50 75 100 125 150 175 200 225 250 275 300 325 350; do echo C, $(python -c "import sys; import statistics; print(statistics.mean([int(x) for x in sys.argv[1:]]))" `cat beacon_C_2.000_$x.000.log | cut -d',' -f 3 | tr '\n' ' '`), $x >> beacon_C_mean.txt; done;

cat beacon_A_mean.txt beacon_B_mean.txt beacon_C_mean.txt > beacon_all_mean.txt

for x in 0 500 1000 1500; do echo A, $(python -c "import sys; import statistics; print(statistics.mean([int(x) for x in sys.argv[1:]]))" `cat beacon_A_2.000_$x.000.log | cut -d',' -f 3 | tr '\n' ' '`), $x >> beacon_A-0-1500_mean.txt; done;

for x in 0 500 1000 1500; do echo B, $(python -c "import sys; import statistics; print(statistics.mean([int(x) for x in sys.argv[1:]]))" `cat beacon_B_2.000_$x.000.log | cut -d',' -f 3 | tr '\n' ' '`), $x >> beacon_B-0-1500_mean.txt; done;

for x in 0 500 1000 1500; do echo C, $(python -c "import sys; import statistics; print(statistics.mean([int(x) for x in sys.argv[1:]]))" `cat beacon_C_2.000_$x.000.log | cut -d',' -f 3 | tr '\n' ' '`), $x >> beacon_C-0-1500_mean.txt; done;

cat beacon_A-0-1500_mean.txt beacon_B-0-1500_mean.txt beacon_C-0-1500_mean.txt > beacon_all-0-1500_mean.txt

