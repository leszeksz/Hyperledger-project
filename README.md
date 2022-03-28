1. Navigate to "test-network"

2. use script: "./network.sh up createChannel -c mychannel -ca"

3. use script: "./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-java/ -ccl java"

4. Again from "test-network" directory use the command to set your CLI Path "export PATH=${PWD}/../bin:$PATH"

4. Navigate to "asset-tranfer-basic/application-gateway-java"

5. use script: "./gradlew run"
