package com.vim.aas.aasserver.aasenvironment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.core.DeserializationException;
import org.eclipse.digitaltwin.basyx.aasenvironment.base.DefaultAASEnvironment;
import org.eclipse.digitaltwin.basyx.aasenvironment.environmentloader.CompleteEnvironment;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudAasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.CrudConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.backend.inmemory.AasInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelInMemoryBackendProvider;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.backend.CrudSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.DefaultResourceLoader;


public class AasEnvironmentLoaderTest {

	protected static final String TEST_ENVIRONMENT_JSON = "/com/vim/aas/aasserver/aasenvironment/environment.json";
	protected static final String TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON = "/com/vim/aas/aasserver/aasenvironment/environment_version_on_second.json";
	protected static final String TEST_ENVIRONMENT_VERSION_AND_REVISION_ON_SECOND_JSON = "/com/vim/aas/aasserver/aasenvironment/environment_version_and_revision_on_second.json";

	protected static final String TEST_ENVIRONMENT_SHELLS_ONLY_JSON = "/com/vim/aas/aasserver/aasenvironment/environment_with_shells_only.json";
	protected static final String TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON = "/com/vim/aas/aasserver/aasenvironment/environment_with_submodels_only.json";

	protected static final PaginationInfo ALL = new PaginationInfo(0, null);

	protected AasRepository aasRepository;
	protected SubmodelRepository submodelRepository;
	protected ConceptDescriptionRepository conceptDescriptionRepository;
	protected DefaultResourceLoader rLoader = new DefaultResourceLoader();

	@Before
	public void setUp() {
		submodelRepository = Mockito.spy(new CrudSubmodelRepository(new SubmodelInMemoryBackendProvider(), new InMemorySubmodelServiceFactory()));
		aasRepository = Mockito.spy(new CrudAasRepository(new AasInMemoryBackendProvider(), new InMemoryAasServiceFactory()));
		conceptDescriptionRepository = Mockito.spy(new CrudConceptDescriptionRepository(new ConceptDescriptionInMemoryBackendProvider()));
	}

	protected void loadRepositories(List<String> pathsToLoad) throws IOException, DeserializationException, InvalidFormatException {
		DefaultAASEnvironment envLoader = new DefaultAASEnvironment(aasRepository, submodelRepository, conceptDescriptionRepository);
		
		for (String path: pathsToLoad) {
			File file = rLoader.getResource(path).getFile();
			envLoader.loadEnvironment(CompleteEnvironment.fromFile(file));
		}
	}

	@Test
	public void testWithResourceFile_AllElementsAreDeployed() throws InvalidFormatException, IOException, DeserializationException {
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON));

		Assert.assertEquals(2, aasRepository.getAllAas(ALL).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(ALL).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDeployedTwiceNoVersion_AllDeployedButNotOverriden() throws InvalidFormatException, IOException, DeserializationException {
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON));
		loadRepositories(List.of(TEST_ENVIRONMENT_JSON));

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(0)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(0)).updateSubmodel(Mockito.anyString(), Mockito.any());

		Assert.assertEquals(2, aasRepository.getAllAas(ALL).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(ALL).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDeployedTwiceWithSameVersion_AllDeployedButNotOverriden() throws InvalidFormatException, IOException, DeserializationException {
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(0)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(0)).updateSubmodel(Mockito.anyString(), Mockito.any());

		Assert.assertEquals(2, aasRepository.getAllAas(ALL).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(ALL).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDeployedTwiceNewRevision_ElementsAreOverriden() throws InvalidFormatException, IOException, DeserializationException {
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_ON_SECOND_JSON));
		loadRepositories(List.of(TEST_ENVIRONMENT_VERSION_AND_REVISION_ON_SECOND_JSON));

		Mockito.verify(aasRepository, Mockito.times(2)).createAas(Mockito.any());
		Mockito.verify(aasRepository, Mockito.times(1)).updateAas(Mockito.anyString(), Mockito.any());

		Mockito.verify(submodelRepository, Mockito.times(2)).createSubmodel(Mockito.any());
		Mockito.verify(submodelRepository, Mockito.times(1)).updateSubmodel(Mockito.anyString(), Mockito.any());

		Assert.assertEquals(2, aasRepository.getAllAas(ALL).getResult().size());
		Assert.assertEquals(2, submodelRepository.getAllSubmodels(ALL).getResult().size());
		Assert.assertEquals(2, conceptDescriptionRepository.getAllConceptDescriptions(ALL).getResult().size());
	}

	@Test
	public void testDuplicateSubmodelIdsInEnvironments_ExceptionIsThrown() throws InvalidFormatException, IOException, DeserializationException {

		String expectedMsg = new CollidingIdentifierException("aas1").getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> loadRepositories(List.of(TEST_ENVIRONMENT_SHELLS_ONLY_JSON, TEST_ENVIRONMENT_SHELLS_ONLY_JSON)));
	}

	@Test
	public void testDuplicateShellIdsInEnvironments_ExceptionIsThrown() {
		String expectedMsg = new CollidingIdentifierException("sm1").getMessage();
		Assert.assertThrows(expectedMsg, CollidingIdentifierException.class, () -> loadRepositories(List.of(TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON, TEST_ENVIRONMENT_SUBMODELS_ONLY_JSON)));
	}
}