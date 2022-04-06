package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "DistributionAssetTransfer",
        info = @Info(contact = @Contact(name = "Distribution_Transfer")))

public class DistributionAssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum DistributionTransferErrors {
        DISTRIBUTION_NOT_FOUND,
        DISTRIBUTION_ALREADY_EXISTS,
        DISTRIBUTION_NOT_UPDATED,
        SALES_DOES_NOT_EXISTS,
        DISTRIBUTION_NOT_TRANSFERRED
    }

    /**
     * Creates a new distribution on the ledger.
     *
     * @param ctx the transaction context
     * @param owner the owner of the new distribution
     * @param distributionId the ID of the new distribution
     * @param quantity quantity of distributed assets
     * @param location the location of shipping
     * @param shippingCost cost of shipping
     * @return the created distribution of assets
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DistributionAsset CreateDistribution(final Context ctx, final String owner, final String salesId, final String distributionId, final String saledProductId, final int quantity, final String shipper, final String location, final int shippingCost){
        ChaincodeStub stub = ctx.getStub();
        if (DistributionExists(ctx, distributionId)) {
            String errorMessage = String.format("Distribution %s already exists", distributionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_ALREADY_EXISTS.toString());
        }
//        if(!SaleAssetTransfer.AssetExists(ctx, salesId)){
//            String errorMessage = String.format("Sales %s does not exists", distributionId);
//            System.out.println(errorMessage);
//            throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.SALES_DOES_NOT_EXISTS.toString());
//        }

        DistributionAsset asset = new DistributionAsset(owner, salesId, distributionId, saledProductId, quantity, shipper, location, shippingCost);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(asset);
        stub.putStringState(distributionId, sortedJson);
        return asset;

    }

    /**
     * Checks the existence of the distribution on the ledger
     *
     * @param ctx the transaction context
     * @param distributionId the ID of the distribution to check
     * @return boolean indicating the existence of the distribution
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean DistributionExists(final Context ctx, final String distributionId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(distributionId);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Retrieves an distribution with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param distributionId the ID of the distribution to retrieve
     * @return the distribution found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public DistributionAsset ReadDistribution(final Context ctx, final String distributionId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(distributionId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Distribution %s does not exist", distributionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_NOT_FOUND.toString());
        }

        DistributionAsset asset = genson.deserialize(assetJSON, DistributionAsset.class);
        return asset;
    }

    /**
     * Updates the properties of an distribution on the ledger.
     *
     * @param ctx the transaction context
     * @param distributionId the ID of the distribution to update
     * @param location the owner of the new distribution
     * @return the created distribution
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public DistributionAsset UpdateDistribution(final Context ctx, final String distributionId, final String shipper, final String location) {
        ChaincodeStub stub = ctx.getStub();

        DistributionAsset asset = null;
        DistributionAsset newAsset = null;
        if (!DistributionExists(ctx, distributionId)) {
            String errorMessage = String.format("Distribution %s does not exist", distributionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_NOT_FOUND.toString());
        } else {
            asset = ReadDistribution(ctx, distributionId);
            boolean shouldLocationBeUpdate = true;
            boolean shouldShipperBeUpdate = true;
            shouldLocationBeUpdate = (location.equals(null) || location.equals(asset.getDeliveryLocation()) ? false : true);
            shouldShipperBeUpdate = (shipper.equals(null) | shipper.equals(asset.getShipper()) ? false : true);

            if(!(shouldLocationBeUpdate && shouldShipperBeUpdate)){
                String errorMessage = String.format("Distribution %s has not been updated", distributionId);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_NOT_UPDATED.toString());
            } else if(shouldLocationBeUpdate && shouldShipperBeUpdate){
                newAsset = new DistributionAsset(
                        asset.getDistributionOwner(), asset.getSalesId(), asset.getDistributionId(), asset.getProductId(), asset.getQuantity(), shipper, location, asset.getShippingCost()
                );
            } else if(shouldLocationBeUpdate){
                newAsset = new DistributionAsset(
                        asset.getDistributionOwner(), asset.getSalesId(), asset.getDistributionId(), asset.getProductId(), asset.getQuantity(), asset.getShipper(), location, asset.getShippingCost()
                );
            } else if(shouldShipperBeUpdate){
                newAsset = new DistributionAsset(
                        asset.getDistributionOwner(), asset.getSalesId(), asset.getDistributionId(), asset.getProductId(), asset.getQuantity(), shipper, asset.getDeliveryLocation(), asset.getShippingCost()
                );
            }
        }

        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(distributionId, sortedJson);
        return newAsset;
    }

    /**
     * Deletes distribution on the ledger.
     *
     * @param ctx the transaction context
     * @param distributionId the ID of the distribution being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteDistribution(final Context ctx, final String distributionId) {
        ChaincodeStub stub = ctx.getStub();

        if (!DistributionExists(ctx, distributionId)) {
            String errorMessage = String.format("Distribution %s does not exist", distributionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_NOT_FOUND.toString());
        }

        stub.delState(distributionId);
    }

    /**
     * Changes the shipper of a distribution on the ledger.
     *
     * @param ctx the transaction context
     * @param distributionId the ID of the asset being transferred
     * @param newOwner the new owner of distribution
     * @return the old owner
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferDistribution(final Context ctx, final String distributionId, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(distributionId);
        DistributionAsset newAsset = null;
        DistributionAsset asset = null;

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Distribution %s does not exist", distributionId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_NOT_FOUND.toString());
        } else {
            asset = genson.deserialize(assetJSON, DistributionAsset.class);
            boolean shouldOwnerBeUpdate = (newOwner.equals(null) | newOwner.equals(asset.getShipper()) ? false : true);

            if(!(shouldOwnerBeUpdate)){
                String errorMessage = String.format("Distribution %s has not been transfered", distributionId);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, DistributionAssetTransfer.DistributionTransferErrors.DISTRIBUTION_NOT_TRANSFERRED.toString());
            } else {
                newAsset = new DistributionAsset(
                        newOwner, asset.getSalesId(), asset.getDistributionId(), asset.getProductId(), asset.getQuantity(), asset.getShipper(), asset.getDeliveryLocation(), asset.getShippingCost()
                );
                //Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
                String sortedJson = genson.serialize(newAsset);
                stub.putStringState(distributionId, sortedJson);

                return newAsset.getShipper();
            }
        }
    }

    /**
     * Retrieves all distributions from the ledger.
     *
     * @param ctx the transaction context
     * @return array of distributions found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllDistributions(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<DistributionAsset> queryResults = new ArrayList<DistributionAsset>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            DistributionAsset asset = genson.deserialize(result.getStringValue(), DistributionAsset.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
