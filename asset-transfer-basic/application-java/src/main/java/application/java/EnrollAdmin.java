/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package application.java;

import java.nio.file.Paths;
import java.util.Properties;

import org.checkerframework.checker.units.qual.C;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class EnrollAdmin {

	public static void main(String[] args) throws Exception {

		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put(Constants.PROP_KEY,
				Constants.PROP_VALUE);
		props.put(Constants.PROP_KEY_2, Constants.PROP_VALUE_2);
		HFCAClient caClient = HFCAClient.createNewInstance(Constants.URL, props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get(Constants.WALLET));

		// Check to see if we've already enrolled the admin user.
		if (wallet.get(Constants.ADMIN) != null) {
			System.out.println("An identity for the admin user " + Constants.ADMIN + " already exists in the wallet");
			return;
		}

		// Enroll the admin user, and import the new identity into the wallet.
		final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
		enrollmentRequestTLS.addHost(Constants.LOCALHOST);
		enrollmentRequestTLS.setProfile(Constants.TLS);
		Enrollment enrollment = caClient.enroll(Constants.ADMIN, Constants.ADMIN_PW, enrollmentRequestTLS);
		Identity user = Identities.newX509Identity(Constants.ORG_1_MSP, enrollment);
		wallet.put(Constants.ADMIN, user);
		System.out.println("Successfully enrolled user " + Constants.ADMIN + " and imported it into the wallet");
	}
}
