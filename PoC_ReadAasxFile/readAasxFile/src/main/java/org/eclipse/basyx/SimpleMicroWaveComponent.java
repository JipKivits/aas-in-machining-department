package org.eclipse.basyx;

import java.io.IOException;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.xml.sax.SAXException;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;

import java.util.Set;
import java.util.Random;

public class SimpleMicroWaveComponent {
    
    private BaSyxContextConfiguration registryContext;
    private AASBundle heaterAASBundle;

    private double manuallySetTemperature;

    public SimpleMicroWaveComponent(String aasxResourcePath) {
        heaterAASBundle = loadStaticBundleFromAASX(aasxResourcePath);
        connectMicrowaveBundle(heaterAASBundle);
    }

    public void SetRegistryContext(BaSyxContextConfiguration registryContext){
        this.registryContext = registryContext;
    }

    public void start(BaSyxContextConfiguration aasContext) {
        AASServerComponent serverComponent = new AASServerComponent(aasContext);
        setAASServerComponentBundles(serverComponent);
        setAASServerComponentRegistry(serverComponent);
        serverComponent.startComponent();
    }

    private void setAASServerComponentRegistry(AASServerComponent serverComponent) {
        String registryEndpoint = registryContext.getUrl();
        IAASRegistry registryProxy = new AASRegistryProxy(registryEndpoint);
        serverComponent.setRegistry(registryProxy);
    }

    private void setAASServerComponentBundles(AASServerComponent serverComponent) {
        Set<AASBundle> aasBundles = new HashSet<>();
        aasBundles.add(heaterAASBundle);
        serverComponent.setAASBundle(heaterAASBundle);
    }

    private static AASBundle loadStaticBundleFromAASX(String aasxPath){
        AASXToMetamodelConverter converter = new AASXToMetamodelConverter(aasxPath);
        Set<AASBundle> loadedAASBundles;
        try {
            loadedAASBundles = converter.retrieveAASBundles();
            return loadedAASBundles.iterator().next();
        } catch (InvalidFormatException | IOException | ParserConfigurationException | SAXException e){
            throw new RuntimeException("Could not load AASX file from path " + aasxPath);
        }
    }

    private void connectMicrowaveBundle(AASBundle heaterAasBundle) {
        SubmodelAggregator submodelAggregator = new SubmodelAggregator();
        ISubmodel sensorSubmodel = submodelAggregator.getSubmodelbyIdShort("Nameplate");
        connectTemperatureSubmodel(sensorSubmodel);
    }

    private void connectTemperatureSubmodel (ISubmodel sensorSubmodel) {
        final ISubmodelElement currentTempProp = sensorSubmodel.getSubmodelElement("Temperature");
        new Thread(() -> {
            while (true) {
                updateCurrentTemperature(currentTempProp);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateCurrentTemperature (ISubmodelElement currentTempProp) {
        double newValue;
        if (this.manuallySetTemperature != 0) {
            newValue = this.manuallySetTemperature;
        } else {
            Random rand = new Random();
            newValue = rand.nextFloat();
        }
        currentTempProp.setValue(newValue);
    }
}
