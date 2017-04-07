#!/bin/bash
clear

function pause(){
   read -p "$*"
}

echo "***************Starting Script********************"
echo
echo " > Generating Vars..."
MC_ADDRESS="230.0.0.2"
MC_PORT="3334"
MCB_ADDRESS="230.0.0.3"
MCB_PORT="4445"
MCR_ADDRESS="230.0.0.4"
MCR_PORT="5556"
echo 
echo MC_ADDRESS $MC_ADDRESS &
echo MC_PORT $MC_PORT &
echo MCB_ADDRESS $MCB_ADDRESS &
echo MCB_PORT $MCB_PORT &
echo MCR_ADDRESS $MCR_ADDRESS &
echo MCR_PORT $MCR_PORT
echo 
BIN="bin/"
echo "*********************Compiling********************"
echo 
echo " > Start Running Peer 2 ..."
cd Peer2_Storage
java -cp ../$BIN interfaces.Main 2 1.0 $MC_ADDRESS $MC_PORT $MCB_ADDRESS $MCB_PORT $MCR_ADDRESS $MCR_PORT