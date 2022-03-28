1. Navigate to "test-network"
1.1 use script: ./network.sh up createChannel -c mychannel -ca
1.2 use script: ./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-java/ -ccl java

2. Navigate to "application-gateway-java"
2.2 use script: ./gradlew run
