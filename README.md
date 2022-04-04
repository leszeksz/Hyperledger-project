1. Navigate to "test-network"

2. Use script: "./network.sh up createChannel -c mychannel -ca"

3. Use script: "./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-java/ -ccl java"

4. Again from "test-network" directory use the command to set your CLI Path "export PATH=${PWD}/../bin:$PATH"

5. Navigate to "asset-tranfer-basic/application-gateway-java"

6. Use script: "./gradlew run"

7. Run the application with App.java (asset-transfer-basic\application-java\src\main\java\application\java\App.java)

8. Close the network from "test_network" with script: "./network.sh down"



If you want to start over by closing the network and bringing it up again you have to delete "**wallet**" folder from directory "asset-transfer-basic" and 
"**ordererOrganizations**" + "**peerOrganizations**" folders from directory "test-network\organizations" because new credentials and wallet will be generated again.
