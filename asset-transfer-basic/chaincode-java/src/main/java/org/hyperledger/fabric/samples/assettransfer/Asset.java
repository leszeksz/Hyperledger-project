/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Asset {

    @Property()
    private final String productID;

    @Property()
    private final String owner;

    @Property()
    private final int price;

    public String getProductID() {
        return productID;
    }

    public String getOwner() {
        return owner;
    }

    public int getPrice() {
        return price;
    }

    public Asset(@JsonProperty("productID") final String productID,
            @JsonProperty("owner") final String owner,
            @JsonProperty("price") final int price) {
        this.productID = productID;
        this.owner = owner;
        this.price = price;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;

        if ((obj == null) || (getClass() != obj.getClass())) return false;

        Asset other = (Asset) obj;

        return Objects.deepEquals(
                new String[] {getProductID(), getOwner()},
                new String[] {other.getProductID(), other.getOwner()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductID(),getOwner(), getPrice());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                " [productID=" + productID +
                ", owner=" + owner +
                ", price=" + price +
                "]";
    }
}