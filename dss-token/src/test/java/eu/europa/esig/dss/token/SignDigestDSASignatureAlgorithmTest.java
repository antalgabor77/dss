package eu.europa.esig.dss.token;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.spi.DSSUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore.PasswordProtection;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignDigestDSASignatureAlgorithmTest {

    private static final Logger LOG = LoggerFactory.getLogger(SignDigestDSATest.class);

    private static Collection<SignatureAlgorithm> data() {
        Collection<SignatureAlgorithm> dsaCombinations = new ArrayList<>();
        for (DigestAlgorithm digestAlgorithm : DigestAlgorithm.values()) {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getAlgorithm(EncryptionAlgorithm.DSA, digestAlgorithm);
            if (isSupportedByJDK(digestAlgorithm) && signatureAlgorithm != null) {
                dsaCombinations.add(signatureAlgorithm);
            }
        }
        return dsaCombinations;
    }

    /**
     * Data for RawDSA must be exactly 20 bytes long
     * sun.security.provider.DSA.generateS(DSA.java:306)
     */
    private static boolean isSupportedByJDK(DigestAlgorithm digestAlgorithm) {
        return digestAlgorithm.getSaltLength() <= 20;
    }

    @ParameterizedTest(name = "SignatureAlgorithm {index} : {0}")
    @MethodSource("data")
    public void testPkcs12(SignatureAlgorithm signatureAlgorithm) throws IOException {
        try (Pkcs12SignatureToken signatureToken = new Pkcs12SignatureToken("src/test/resources/good-dsa-user.p12",
                new PasswordProtection("ks-password".toCharArray()))) {

            List<DSSPrivateKeyEntry> keys = signatureToken.getKeys();
            KSPrivateKeyEntry entry = (KSPrivateKeyEntry) keys.get(0);

            ToBeSigned toBeSigned = new ToBeSigned("Hello world".getBytes("UTF-8"));

            SignatureValue signValue = signatureToken.sign(toBeSigned, signatureAlgorithm, entry);
            assertNotNull(signValue.getAlgorithm());
            LOG.info("Sig value : {}", Base64.getEncoder().encodeToString(signValue.getValue()));
            try {
                Signature sig = Signature.getInstance(signValue.getAlgorithm().getJCEId());
                sig.initVerify(entry.getCertificate().getPublicKey());
                sig.update(toBeSigned.getBytes());
                assertTrue(sig.verify(signValue.getValue()));
            } catch (GeneralSecurityException e) {
                Assertions.fail(e.getMessage());
            }

            final DigestAlgorithm digestAlgorithm = signatureAlgorithm.getDigestAlgorithm();
            final byte[] digestBinaries = DSSUtils.digest(digestAlgorithm, toBeSigned.getBytes());
            Digest digest = new Digest(digestAlgorithm, digestBinaries);

            SignatureValue signDigestValue = signatureToken.signDigest(digest, signatureAlgorithm, entry);
            assertNotNull(signDigestValue.getAlgorithm());
            LOG.info("Sig value : {}", Base64.getEncoder().encodeToString(signDigestValue.getValue()));

            try {
                Signature sig = Signature.getInstance(signDigestValue.getAlgorithm().getJCEId());
                sig.initVerify(entry.getCertificate().getPublicKey());
                sig.update(toBeSigned.getBytes());
                assertTrue(sig.verify(signDigestValue.getValue()));
            } catch (GeneralSecurityException e) {
                Assertions.fail(e.getMessage());
            }

            // Sig values are not equals like with RSA. (random number is generated on
            // signature creation)
        }
    }

}