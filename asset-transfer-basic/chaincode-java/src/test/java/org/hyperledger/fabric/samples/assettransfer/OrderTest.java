package org.hyperledger.fabric.samples.assettransfer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OrderTest {

        @Nested
        class Equality {

            @Test
            public void isReflexive() {
                Order order = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);

                Assertions.assertThat(order).isEqualTo(order);
            }

            @Test
            public void isSymmetric() {
                Order orderA = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);
                Order orderB = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);

                Assertions.assertThat(orderA).isEqualTo(orderB);
                Assertions.assertThat(orderB).isEqualTo(orderA);
            }

            @Test
            public void isTransitive() {
                Order orderA = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);
                Order orderB = new Order("id", "productName", 2, "2022-05-06", "status", 10, 0, 0);
                Order orderC = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);

                Assertions.assertThat(orderA).isEqualTo(orderB);
                Assertions.assertThat(orderB).isEqualTo(orderC);
                Assertions.assertThat(orderA).isEqualTo(orderC);
            }

            @Test
            public void handlesInequality() {
                Order orderA = new Order("id", "productName", 2, "2022-05-06", "status", 100,  0, 0);
                Order orderB = new Order("id", "productName", 2, "2022-05-06", "status", 10, 0, 0);

                Assertions.assertThat(orderA).isNotEqualTo(orderB);
            }

            @Test
            public void handlesOtherObjects() {
                Order assetA = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);

                String assetB = "not a asset";

                Assertions.assertThat(assetA).isNotEqualTo(assetB);
            }

            @Test
            public void handlesNull() {
                Order asset = new Order("id", "productName", 2, "2022-05-06", "status", 10, 0, 0);

                Assertions.assertThat(asset).isNotEqualTo(null);
            }
        }

    @Test
    public void toStringIdentifiesOrder() {
        Order order = new Order("id", "productName", 2, "2022-05-06", "status", 10,  0, 0);
        String expected = "Order@e38aa175" +
                " [iD=" + "id" +
                ", productName=" + "productName" +
                ", quantity=" + 2 +
                ", deliveryDate=" + "2022-05-06" +
                ", status=" + "status" +
                ", price=" + 10 +
                ", leatherCount=" + 0 +
                ", metalCount=" + 0 +
                "]";
//        Assertions.assertThat(order.toString()).isEqualTo(expected);
    }

}
