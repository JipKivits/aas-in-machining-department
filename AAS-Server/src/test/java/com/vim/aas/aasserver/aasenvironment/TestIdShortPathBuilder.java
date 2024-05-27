package com.vim.aas.aasserver.aasenvironment;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidIdShortPathElementsException;
import org.eclipse.digitaltwin.basyx.aasenvironment.*;

import static com.vim.aas.aasserver.aasenvironment.IdShortPathTestHelper.*;

import org.junit.Test;


public class TestIdShortPathBuilder {
	
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_1 = "SML_ONE[2][0][1]";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_2 = "SMC_TWO.SMC1.SMC2.File1_TWO";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_3 = "SML_THREE[2].SMC1.SML1[1]";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_4 = "SMC_FOUR.SML0[1][1].File1_FOUR";
	private static final String EXPECTED_IDSHORTPATH_SCENARIO_5 = "File1_FIVE";
	
	@Test
	public void scenario1() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario1Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_1, actualIdShortPath);
	}
	
	@Test
	public void scenario2() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario2Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_2, actualIdShortPath);
	}
	
	@Test
	public void scenario3() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario3Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_3, actualIdShortPath);
	}
	
	@Test
	public void scenario4() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario4Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_4, actualIdShortPath);
	}
	
	@Test
	public void scenario5() {
		IdShortPathBuilder builder = new IdShortPathBuilder(createScenario5Element());
		
		String actualIdShortPath = builder.build();
		
		assertEquals(EXPECTED_IDSHORTPATH_SCENARIO_5, actualIdShortPath);
	}
	
	@Test(expected = InvalidIdShortPathElementsException.class)
	public void createIdShortPathBuilderWithInvalidArgument() {
		List<SubmodelElement> invalidIdShortPathElements = new ArrayList<>();
		
		new IdShortPathBuilder(invalidIdShortPathElements);
	}

}