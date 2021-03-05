package eu.europa.esig.dss.xades.signature;

import eu.europa.esig.dss.DomUtils;
import eu.europa.esig.dss.xades.DSSXMLUtils;
import eu.europa.esig.dss.xades.definition.xades132.XAdES132Paths;
import org.junit.jupiter.api.BeforeEach;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XAdESLevelCv2Test extends XAdESLevelCTest {

    @Override
    @BeforeEach
    public void init() throws Exception {
        super.init();
        signatureParameters.setEn319132(true);
    }

    @Override
    protected void onDocumentSigned(byte[] byteArray) {
        Document document = DomUtils.buildDOM(byteArray);
        NodeList signaturesList = DSSXMLUtils.getAllSignaturesExceptCounterSignatures(document);
        assertEquals(1, signaturesList.getLength());

        XAdES132Paths paths = new XAdES132Paths();

        Node signature = signaturesList.item(0);
        NodeList signingCertificateList = DomUtils.getNodeList(signature, paths.getSigningCertificatePath());
        assertEquals(0, signingCertificateList.getLength());

        NodeList signingCertificateV2List = DomUtils.getNodeList(signature, paths.getSigningCertificateV2Path());
        assertEquals(1, signingCertificateV2List.getLength());

        NodeList completeCertificateRefsList = DomUtils.getNodeList(signature, paths.getCompleteCertificateRefsPath());
        assertEquals(0, completeCertificateRefsList.getLength());

        NodeList completeCertificateRefsV2List = DomUtils.getNodeList(signature, paths.getCompleteCertificateRefsV2Path());
        assertEquals(1, completeCertificateRefsV2List.getLength());

        NodeList completeRevocationRefsList = DomUtils.getNodeList(signature, paths.getCompleteRevocationRefsPath());
        assertEquals(1, completeRevocationRefsList.getLength());
    }

}