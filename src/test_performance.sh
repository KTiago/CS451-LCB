#!/bin/bash
#
# Tests the performance of the Uniform Reliable Broadcast application.
#
# This is an example script that shows the general structure of the
# test. The details and parameters of the actual test might differ.
#

# time to wait for correct processes to broadcast all messages (in seconds)
# (should be adapted to the number of messages to send)
l1='cat da_proc_1.out |grep "d"|wc -l| cut -d " " -f 1'
l2='cat da_proc_2.out |grep "d"|wc -l| cut -d " " -f 1'
l3='cat da_proc_3.out |grep "d"|wc -l| cut -d " " -f 1'
l4='cat da_proc_4.out |grep "d"|wc -l| cut -d " " -f 1'
l5='cat da_proc_5.out |grep "d"|wc -l| cut -d " " -f 1'
init_time=10
ttf=20

echo "---PERFORMANCE TEST---"

# compile (should output: Da_proc.class)
#make

for msg in `seq 10 50 160`;
do 
	# start 5 processes, each broadcasting 100 messages
	for i in `seq 1 5`
	do
		java Da_proc $i membership $msg &
    		da_proc_id[$i]=$!
	done

	# leave some time for process initialization
	sleep $init_time

	for i in `seq 1 5`
	do
    	if [ -n "${da_proc_id[$i]}" ]; then
			kill -USR2 "${da_proc_id[$i]}"
    	fi
	done

	# leave some time for the correct processes to broadcast all messages
	sleep $ttf

	# stop all processes
	for i in `seq 1 5`
	do
    	if [ -n "${da_proc_id[$i]}" ]; then
		kill -TERM "${da_proc_id[$i]}"
    	fi
	done
	# wait until all processes stop
	for i in `seq 1 5`
	do
    	if [ -n "${da_proc_id[$i]}" ]; then
	    	wait "${da_proc_id[$i]}"
    	fi	
	done

	y1=$(eval $l1)
	y2=$(eval $l2) 
	y3=$(eval $l3) 
	y4=$(eval $l4) 
	y5=$(eval $l5)
	sum=$((y1 + y2 + y3 + y4 + y5))
	div=$(($ttf * 5))
	avr=$((sum / div))
	echo "For $msg messages and a time to finish of $ttf seconds:"
	echo "Number of message delivered by seconds $avr"
	echo ""
done
