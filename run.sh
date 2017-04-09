#!/bin/bash
clear
function pause(){
   read -p "$*"
}
echo "***************Starting Script********************"
MC_ADDRESS="230.0.0.2"
MC_PORT="3334"
MCB_ADDRESS="230.0.0.3"
MCB_PORT="4445"
MCR_ADDRESS="230.0.0.4"
MCR_PORT="5556"

BIN="bin/"

echo -n "How much peers in this machine? > "
if read -t 10 response; then
    echo "Great, you want $response peer(s)!"
else
	response=1;
    echo "Great, you want" $response "peer!"
fi

echo -n "Need peer initiator?true/false > "
if read -t 10 initiator; then
    echo "With peer initiator"
else
	initiator="false";
fi

if [[ "$initiator" = "true" ]]; then
	    gnome-terminal -x ./RUN_Peer1Initiator.sh &	
		if [[ "$response" -eq "2" ]]; then
			gnome-terminal -x ./RUN_Peer2.sh
		fi
	
		if [[ "$response" -eq "3" ]]; then
			/bin/bash RUN_Peer2.sh &
			/bin/bash RUN_Peer3.sh
		fi		
else
		if [[ "$response" -eq "2" ]]; then
			/bin/bash RUN_Peer2.sh
		fi

		if [[ "$response" -eq "3" ]]; then
			/bin/bash RUN_Peer2.sh &
			/bin/bash RUN_Peer3.sh
		fi
fi
pause;







