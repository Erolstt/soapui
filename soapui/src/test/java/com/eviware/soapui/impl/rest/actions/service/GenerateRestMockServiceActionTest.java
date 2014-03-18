package com.eviware.soapui.impl.rest.actions.service;

import com.eviware.soapui.impl.rest.RestService;
import com.eviware.soapui.impl.rest.mock.RestMockService;
import com.eviware.soapui.model.mock.MockOperation;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.utils.ModelItemFactory;
import com.eviware.x.form.XFormDialog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.NotNull;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenerateRestMockServiceActionTest
{

	private RestService restService;
	private String restMockServiceName;
	private XFormDialog dialog;
	private GenerateRestMockServiceAction action;

	@Before
	public void setUp() throws Exception
	{
		restService = ModelItemFactory.makeRestService();
		restMockServiceName = "My Mock Service";
		action = new GenerateRestMockServiceAction();

		mockFormDialog();
	}

	public void mockFormDialog()
	{
		dialog = mock( XFormDialog.class );
		when( dialog.getValue( GenerateRestMockServiceAction.Form.MOCKSERVICENAME ) ).thenReturn( restMockServiceName );
		when( dialog.show() ).thenReturn( true ).thenReturn( false );
		action.setFormDialog( dialog );
	}

	@Test
	public void shouldGenerateRestMockService() throws SoapUIException
	{
		action.perform( restService, null );

		RestMockService restMockService = getResultingRestMockService();
		assertThat( restMockService, is( NotNull.NOT_NULL ) );
		assertThat( restMockService.getName(), is( restMockServiceName ));
	}

	public RestMockService getResultingRestMockService()
	{
		return restService.getProject().getRestMockServiceByName( restMockServiceName );
	}

	@Test
	public void shouldGenerateRestMockServiceWithResources()
	{
		restService.addNewResource( "one", "/one" );
		restService.addNewResource( "two", "/two" );

		action.perform( restService, null );

		RestMockService restMockService = getResultingRestMockService();
		assertThat( restMockService.getMockOperationCount(), is( 2 ));
		assertThat( restMockService.getMockOperationAt( 1 ).getName(), is( "two" ) );

		for( MockOperation mockAction : restMockService.getMockOperationList() )
		{
			assertThat( mockAction.getMockResponseCount(), is( 1 ) );
		}
	}
}