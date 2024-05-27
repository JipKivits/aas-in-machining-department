package com.vim.aas.aasserver.aasenvironment;

import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.preconfiguration.AasEnvironmentPreconfigurationLoader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class PreconfigurationLoaderTextualResourceTest extends AasEnvironmentLoaderTest {

	@Override
	protected void loadRepositories(List<String> pathsToLoad) throws IOException, InvalidFormatException, DeserializationException {
		AasEnvironmentPreconfigurationLoader envLoader = new AasEnvironmentPreconfigurationLoader(rLoader, pathsToLoad);
		envLoader.loadPreconfiguredEnvironments(new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository));
	}

	@Test
	public void testWithEmptyResource_NoElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException {
		loadRepositories(List.of());
		Assert.assertTrue(aasRepository.getAllAas(ALL).getResult().isEmpty());
		Assert.assertTrue(submodelRepository.getAllSubmodels(ALL).getResult().isEmpty());
		Assert.assertTrue(conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().isEmpty());

		Mockito.verify(aasRepository, Mockito.never()).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.never()).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.never()).createSubmodel(Mockito.any());
	}
}
