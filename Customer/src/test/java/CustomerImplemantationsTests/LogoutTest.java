package CustomerImplemantationsTests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ClientServerApi.ClientServerDefs;
import ClientServerApi.CommandDescriptor;
import ClientServerApi.CommandWrapper;
import ClientServerApi.ResultDescriptor;
import CustomerContracts.ICustomer;
import SMExceptions.CommonExceptions.CriticalError;
import CustomerContracts.ACustomerExceptions.CustomerNotConnected;
import CustomerImplementations.Customer;
import CustomerImplementations.CustomerDefs;
import UtilsContracts.IClientRequestHandler;
import UtilsImplementations.Serialization;

/** 
 * @author Lior Ben Ami
 * @since 2017-06-30
 */
@RunWith(MockitoJUnitRunner.class)
public class LogoutTest {
	private ICustomer customer;

	@Mock
	private IClientRequestHandler clientRequestHandler;

	@Before
	public void setup() {
		PropertyConfigurator.configure("../log4j.properties");
		customer = new Customer(clientRequestHandler);
	}
	
	@Test
	public void logoutSuccessfulTest() {
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond(new CommandWrapper(CustomerDefs.loginCommandSenderId, CommandDescriptor.LOGOUT,
						Serialization.serialize(ClientServerDefs.anonymousCustomerUsername)).serialize()))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK).serialize());
		} catch (IOException ¢) {
			fail();
		}
		
		try {
			customer.logout();
		} catch (CustomerNotConnected | CriticalError e) {			
			fail();
		}
	}
	
	@Test
	public void logoutCriticalErrorTest() {
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond(new CommandWrapper(CustomerDefs.loginCommandSenderId, CommandDescriptor.LOGOUT,
						Serialization.serialize(ClientServerDefs.anonymousCustomerUsername)).serialize()))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_ERR).serialize());
		} catch (IOException ¢) {
			fail();
		}
		
		try {
			customer.logout();
		} catch (CustomerNotConnected e) {			
			fail();
		} catch (CriticalError __) {
			/* success */
		}
	}
	
	@Test
	public void logoutCustomerNotConnectedTest() {
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond(new CommandWrapper(CustomerDefs.loginCommandSenderId, CommandDescriptor.LOGOUT,
						Serialization.serialize(ClientServerDefs.anonymousCustomerUsername)).serialize()))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_SENDER_IS_NOT_CONNECTED).serialize());
		} catch (IOException ¢) {
			fail();
		}
		
		try {
			customer.logout();
		} catch (CriticalError e) {			
			fail();
		} catch (CustomerNotConnected __) {
			/* success */
		}
	}
	
	@Test
	public void logoutConnectionFailureTest() {
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond(new CommandWrapper(CustomerDefs.loginCommandSenderId, CommandDescriptor.LOGOUT,
						Serialization.serialize(ClientServerDefs.anonymousCustomerUsername)).serialize()))
			.thenThrow(new SocketTimeoutException());
		} catch (IOException ¢) {
			fail();
		}
		
		try {
			customer.logout();
		} catch (CustomerNotConnected e) {			
			fail();
		} catch (CriticalError __) {
			/* success */
		}
	}
	
	@Test
	public void IllegalResultTest() {
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond(new CommandWrapper(CustomerDefs.loginCommandSenderId, CommandDescriptor.LOGOUT,
						Serialization.serialize(ClientServerDefs.anonymousCustomerUsername)).serialize()))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_GROCERY_LIST_IS_EMPTY).serialize());
		} catch (IOException ¢) {
			fail();
		}
		
		try {
			customer.logout();
		} catch (CustomerNotConnected e) {			
			fail();
		} catch (CriticalError __) {
			/* success */
		}
	}
}
