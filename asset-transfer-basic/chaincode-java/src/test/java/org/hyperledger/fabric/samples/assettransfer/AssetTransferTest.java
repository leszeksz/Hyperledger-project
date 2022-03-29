/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public final class AssetTransferTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockAssetResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> assetList;

        MockAssetResultsIterator() {
            super();

            assetList = new ArrayList<KeyValue>();

            assetList.add(new MockKeyValue("asset1",
                    "{ \"productID\": \"asset1\", \"owner\": \"Tomoko\", \"price\": 300 }"));
            assetList.add(new MockKeyValue("asset2",
                    "{ \"productID\": \"asset2\", \"owner\": \"Brad\", \"price\": 400 }"));
            assetList.add(new MockKeyValue("asset3",
                    "{ \"productID\": \"asset3\", \"owner\": \"Jin Soo\", \"price\": 500 }"));
            assetList.add(new MockKeyValue("asset4",
                    "{ \"productID\": \"asset4\", \"owner\": \"Max\", \"price\": 600 }"));
            assetList.add(new MockKeyValue("asset5",
                    "{ \"productID\": \"asset5\", \"owner\": \"Adrian\", \"price\": 700 }"));
            assetList.add(new MockKeyValue("asset6",
                    "{ \"productID\": \"asset6\", \"owner\": \"Michel\", \"price\": 800 }"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return assetList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        AssetTransfer contract = new AssetTransfer();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Nested
    class InvokeReadAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"productID\": \"asset1\", \"owner\": \"Tomoko\", \"price\": 300 }");

            Asset asset = contract.ReadAsset(ctx, "asset1");

            assertThat(asset).isEqualTo(new Asset("asset1", "Tomoko", 300));
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.ReadAsset(ctx, "asset1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

//    @Test
//    void invokeInitLedgerTransaction() {
//        AssetTransfer contract = new AssetTransfer();
//        Context ctx = mock(Context.class);
//        ChaincodeStub stub = mock(ChaincodeStub.class);
//        when(ctx.getStub()).thenReturn(stub);
//
//        contract.InitLedger(ctx);
//
//        InOrder inOrder = inOrder(stub);
//        inOrder.verify(stub).putStringState("asset1", "{\"price\":300,\"productID\":\"asset1\",\"owner\":\"Tomoko}");
//        inOrder.verify(stub).putStringState("asset2", "{\"price\":400,\"productID\":\"asset2\",\"owner\":\"Brad}");
//        inOrder.verify(stub).putStringState("asset3", "{\"price\":500,\"productID\":\"asset3\",\"owner\":\"Jin Soo}");
//        inOrder.verify(stub).putStringState("asset4", "{\"price\":600,\"productID\":\"asset4\",\"owner\":\"Max}");
//        inOrder.verify(stub).putStringState("asset5", "{\"price\":700,\"productID\":\"asset5\",\"owner\":\"Adrian}");
//
//    }

    @Nested
    class InvokeCreateAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"productID\": \"asset1\", \"owner\": \"Tomoko\", \"price\": 300 }");

            Throwable thrown = catchThrowable(() -> {
                contract.CreateAsset(ctx, "asset1","Siobhán", 60);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 already exists");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_ALREADY_EXISTS".getBytes());
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Asset asset = contract.CreateAsset(ctx, "asset1","Siobhán", 60);

            assertThat(asset).isEqualTo(new Asset("asset1", "Siobhán", 60));
        }
    }

//    @Test
//    void invokeGetAllAssetsTransaction() {
//        AssetTransfer contract = new AssetTransfer();
//        Context ctx = mock(Context.class);
//        ChaincodeStub stub = mock(ChaincodeStub.class);
//        when(ctx.getStub()).thenReturn(stub);
//        when(stub.getStateByRange("", "")).thenReturn(new MockAssetResultsIterator());
//
//        String assets = contract.GetAllAssets(ctx);
//
//        assertThat(assets).isEqualTo("[{\"owner\":\"Tomoko, \"price\":300,\"productID\":\"asset1\"},"
//                + "{\"owner\":\"Brad,\"price\":400,\"productID\":\"asset2\"},"
//                + "{\"owner\":\"Jin Soo,\"price\":500,\"productID\":\"asset3\"},"
//                + "{\"owner\":\"Max, \"price\":600,\"productID\":\"asset4\"},"
//                + "{\"owner\":\"Adrian, \"price\":700,\"productID\":\"asset5\"},"
//                + "{\"owner\":\"Michel, \"price\":800,\"productID\":\"asset6\"}]");
//
//    }

    @Nested
    class TransferAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"productID\": \"asset1\",\"owner\": \"Tomoko\", \"price\": 300 }");

            String oldOwner = contract.TransferAsset(ctx, "asset1", "Dr Evil");

            assertThat(oldOwner).isEqualTo("Tomoko");
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferAsset(ctx, "asset1", "Dr Evil");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class UpdateAssetTransaction {

        @Test
        public void whenAssetExists() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1"))
                    .thenReturn("{ \"productID\": \"asset1\",\"owner\": \"Arturo\", \"price\": 60 }");

            Asset asset = contract.UpdateAsset(ctx, "asset1", "Arturo", 600);

            assertThat(asset).isEqualTo(new Asset("asset1", "Arturo", 600));
        }

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.TransferAsset(ctx, "asset1", "Alex");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class DeleteAssetTransaction {

        @Test
        public void whenAssetDoesNotExist() {
            AssetTransfer contract = new AssetTransfer();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("asset1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.DeleteAsset(ctx, "asset1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Asset asset1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ASSET_NOT_FOUND".getBytes());
        }
    }
}
