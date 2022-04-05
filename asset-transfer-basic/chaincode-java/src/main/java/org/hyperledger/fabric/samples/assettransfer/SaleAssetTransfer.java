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
        name = "sale_asset",
        info = @Info(contact = @Contact( name = "Ja")))
@Default
public final class SaleAssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public SaleAsset CreateSaleAsset(final Context ctx, final String saleID, final String owner,
                             final String product, final int quantity, final String contractor) {
        ChaincodeStub stub = ctx.getStub();

        if (AssetExists(ctx, saleID)) {
            String errorMessage = String.format("Sale asset %s already exists", saleID);
            throw new ChaincodeException(errorMessage, SaleAssetTransfer.AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }
        SaleAsset saleAsset = new SaleAsset(saleID, owner, product,quantity,contractor);

        String sortedJson = genson.serialize(saleAsset);
        stub.putStringState(saleID, sortedJson);

        return saleAsset;
    }
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String saleID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(saleID);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public SaleAsset ReadSaleAsset(final Context ctx, final String saleID){
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(saleID);

        if (assetJSON == null || assetJSON.isEmpty()){
            String errorMessage = String.format("Asset %s does not exist", saleID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        SaleAsset saleAsset = genson.deserialize(assetJSON, SaleAsset.class);
        return saleAsset;
    }

    public SaleAsset UpdateSaleAsset(final Context ctx, final String saleID, final String owner,
                                     final String product, final int quantity, final String contractor){
        ChaincodeStub stub = ctx.getStub();

        if (!AssetExists(ctx,saleID)){
            String errorMessage = String.format("Asset %s does not exist", saleID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage,AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        SaleAsset newSaleAsset = new SaleAsset(saleID,owner, product,quantity,contractor);
        String sortedJson = genson.serialize(newSaleAsset);
        stub.putStringState(saleID,sortedJson);
        return newSaleAsset;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteSaleAsset(final Context ctx, final String saleID){
        ChaincodeStub stub = ctx.getStub();

        if (!AssetExists(ctx,saleID)){
            String errorMessage = String.format("Asset %s does not exist", saleID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }
        stub.delState(saleID);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferSaleAsset(final Context ctx, final String saleID, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(saleID);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", saleID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage,AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

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
}
