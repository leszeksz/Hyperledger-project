/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;


import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "AssetTransfer",
        info = @Info (contact = @Contact( name = "Asset_Transfer")))

public final class AssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx the transaction context
     * @param productID the ID of the new asset
     * @param owner the owner of the new asset
     * @param price the price of the new asset
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset CreateAsset(final Context ctx, final String productID,
        final String owner, final int price) {
        ChaincodeStub stub = ctx.getStub();

        checkAssetAlreadyExists(ctx, productID, "Asset %s already exists");

        Asset asset = new Asset(productID,owner, price);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        serialize(stub,asset,productID);
        return asset;
    }

    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Asset ReadAsset(final Context ctx, final String productID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(productID);

        checkIfAssetExist(assetJSON == null || assetJSON.isEmpty(), productID);

        Asset asset = genson.deserialize(assetJSON, Asset.class);
        return asset;
    }

    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset being updated
     * @param owner the owner of the asset being updated
     * @param price the price of the asset being updated
     * @return the transferred asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset UpdateAsset(final Context ctx, final String productID,
        final String owner, final int price) {
        ChaincodeStub stub = ctx.getStub();

        checkIfAssetExist(!AssetExists(ctx, productID), productID);

        Asset newAsset = new Asset(productID, owner, price);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        serialize(stub,newAsset,productID);
        return newAsset;
    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteAsset(final Context ctx, final String productID) {
        ChaincodeStub stub = ctx.getStub();

        checkIfAssetExist(!AssetExists(ctx, productID), productID);

        stub.delState(productID);
    }

    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String productID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(productID);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferAsset(final Context ctx, final String productID, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(productID);

        checkIfAssetExist(assetJSON == null || assetJSON.isEmpty(), productID);

        Asset asset = genson.deserialize(assetJSON, Asset.class);

        Asset newAsset = new Asset(asset.getProductID(), newOwner, asset.getPrice());
        //Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
        serialize(stub,newAsset,productID);

        return asset.getOwner();
    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Asset> queryResults = new ArrayList<Asset>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Asset asset = genson.deserialize(result.getStringValue(), Asset.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
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
    public DistributionAsset CreateDistribution(final Context ctx, final String distributionId, final String owner, final String salesId, final String productId, final int quantity, final String shipper, final String location, final int shippingCost){
        ChaincodeStub stub = ctx.getStub();
        checkAssetAlreadyExists(ctx, distributionId, "Distribution %s already exists");
        DistributionAsset asset = new DistributionAsset(owner, salesId, distributionId, productId, quantity, shipper, location, shippingCost);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        serialize(stub,asset,distributionId);

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
    public static boolean DistributionExists(final Context ctx, final String distributionId) {
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
        String distributionJSON = stub.getStringState(distributionId);
        checkIfAssetExist(distributionJSON == null || distributionJSON.isEmpty(), distributionId);
        DistributionAsset distribution = genson.deserialize(distributionJSON, DistributionAsset.class);

        return distribution;
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
    public DistributionAsset UpdateDistribution(final Context ctx, final String distributionId, final String owner, final String salesId, final String productId, final int quantity, final String shipper, final String location, final int shippingCost) {
        ChaincodeStub stub = ctx.getStub();
        checkIfAssetExist(!DistributionExists(ctx, distributionId), distributionId);
        DistributionAsset newAsset = new DistributionAsset(owner, salesId, distributionId, productId,
                quantity, shipper, location, shippingCost);
        serialize(stub, newAsset, distributionId);

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
        checkIfAssetExist(!DistributionExists(ctx, distributionId), distributionId);
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
        String distributionJSON = stub.getStringState(distributionId);
        checkIfAssetExist(distributionJSON == null || distributionJSON.isEmpty(), distributionId);
        DistributionAsset asset = genson.deserialize(distributionJSON, DistributionAsset.class);
        DistributionAsset newAsset = new DistributionAsset(newOwner, asset.getSalesId(), asset.getDistributionId(), asset.getProductId(),
                asset.getQuantity(), asset.getShipper(), asset.getLocation(), asset.getShippingCost());
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(distributionId, sortedJson);

        return newAsset.getShipper();
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
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        List<DistributionAsset> queryResults = new ArrayList<>();

        for (KeyValue result: results) {
            DistributionAsset asset = genson.deserialize(result.getStringValue(), DistributionAsset.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public SaleAsset CreateSaleAsset(final Context ctx, final String saleID, final String owner,
                             final String product, final int quantity, final String contractor) {
        ChaincodeStub stub = ctx.getStub();

        checkAssetAlreadyExists(ctx, saleID, "Sale asset %s already exists");
        SaleAsset saleAsset = new SaleAsset(saleID, owner, product, quantity, contractor);
        serialize(stub,saleAsset,saleID);

        return saleAsset;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public SaleAsset ReadSaleAsset(final Context ctx, final String saleID){
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(saleID);

        checkIfAssetExist(assetJSON == null || assetJSON.isEmpty(), saleID);

        SaleAsset saleAsset = genson.deserialize(assetJSON, SaleAsset.class);
        return saleAsset;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public SaleAsset UpdateSaleAsset(final Context ctx, final String saleID, final String owner,
                                     final String product, final int quantity, final String contractor){
        ChaincodeStub stub = ctx.getStub();

        checkIfAssetExist(!AssetExists(ctx, saleID), saleID);
        SaleAsset newSaleAsset = new SaleAsset(saleID,owner, product,quantity,contractor);
        serialize(stub,newSaleAsset,saleID);
        return newSaleAsset;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteSaleAsset(final Context ctx, final String saleID){
        ChaincodeStub stub = ctx.getStub();

        checkIfAssetExist(!AssetExists(ctx, saleID), saleID);
        stub.delState(saleID);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferSaleAsset(final Context ctx, final String saleID, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(saleID);

        checkIfAssetExist(assetJSON == null || assetJSON.isEmpty(), saleID);

        SaleAsset saleAsset = genson.deserialize(assetJSON, SaleAsset.class);

        SaleAsset newSaleAsset = new SaleAsset(saleAsset.getSaleID(),newOwner, saleAsset.getProduct(),saleAsset.getQuantity(),null);
        String sortedJSON = genson.serialize(newSaleAsset);

        return saleAsset.getOwner();
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllSaleAssets(final Context ctx){
        ChaincodeStub stub = ctx.getStub();

        List<SaleAsset> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for(KeyValue result: results){
            SaleAsset saleAsset = genson.deserialize(result.getStringValue(), SaleAsset.class);
            System.out.println(saleAsset);
            queryResults.add(saleAsset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    private void checkAssetAlreadyExists(Context ctx, String assetId, String format) {
        if (AssetExists(ctx, assetId)) {
            String errorMessage = String.format(format, assetId);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
    }

    private void checkIfAssetExist(boolean ctx, String assetId) {
        if (ctx){
            String errorMessage = String.format("Asset %s does not exist", assetId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
    }

    private void serialize(ChaincodeStub stub, Object obj, String id){
        String sortedJson = genson.serialize(obj);
        stub.putStringState(id,sortedJson);
    }

}