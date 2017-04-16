package CustomerImplemantationsTests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import BasicCommonClasses.CatalogProduct;
import BasicCommonClasses.Manufacturer;
import BasicCommonClasses.ProductPackage;
import BasicCommonClasses.SmartCode;
import ClientServerApi.CommandDescriptor;
import ClientServerApi.CommandWrapper;
import ClientServerApi.ResultDescriptor;
import CustomerContracts.ICustomer;
import CustomerContracts.ACustomerExceptions.AmountBiggerThanAvailable;
import CustomerContracts.ACustomerExceptions.CriticalError;
import CustomerContracts.ACustomerExceptions.CustomerNotConnected;
import CustomerContracts.ACustomerExceptions.InvalidParameter;
import CustomerContracts.ACustomerExceptions.ProductPackageDoesNotExist;
import CustomerImplementations.Customer;
import CustomerImplementations.CustomerDefs;
import UtilsContracts.IClientRequestHandler;
import UtilsImplementations.Serialization;

@RunWith(MockitoJUnitRunner.class)

public class AddProductToCartTest {
	private ICustomer customer;

	static int amount = 10;
	static SmartCode sc = new SmartCode(111,LocalDate.now());
	static ProductPackage pp = new ProductPackage(sc, 
			amount, null);
	static CatalogProduct catalogProduct = new CatalogProduct(sc.getBarcode(), "name", null,
			new Manufacturer(1, "Manufacturer"), "description", 22.0, null, null);
	
	@Mock
	private IClientRequestHandler clientRequestHandler;

	@Before
	public void setup() {
		PropertyConfigurator.configure("../log4j.properties");
		customer = new Customer(clientRequestHandler);
	}
	
	@Test
	public void addProductToCartSuccessfulTest() {	
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.ADD_PRODUCT_TO_GROCERY_LIST, Serialization.serialize(pp)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.VIEW_PRODUCT_FROM_CATALOG, Serialization.serialize(sc)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK, Serialization.serialize(catalogProduct)).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CriticalError | CustomerNotConnected | AmountBiggerThanAvailable | ProductPackageDoesNotExist
				| InvalidParameter e1) {
			e1.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CriticalError | CustomerNotConnected | AmountBiggerThanAvailable | ProductPackageDoesNotExist
				| InvalidParameter e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void addProductToCartCriticalErrorTest() {	
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.ADD_PRODUCT_TO_GROCERY_LIST, Serialization.serialize(pp)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_ERR).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.VIEW_PRODUCT_FROM_CATALOG, Serialization.serialize(sc)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK, Serialization.serialize(catalogProduct)).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CustomerNotConnected | AmountBiggerThanAvailable | ProductPackageDoesNotExist
				| InvalidParameter e) {
			e.printStackTrace();
			fail();
		} catch (CriticalError __) {
			/* success */
		}
	}
	
	@Test
	public void addProductToCartCustomerNotConnectedTest() {	
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.ADD_PRODUCT_TO_GROCERY_LIST, Serialization.serialize(pp)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_SENDER_IS_NOT_CONNECTED).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.VIEW_PRODUCT_FROM_CATALOG, Serialization.serialize(sc)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK, Serialization.serialize(catalogProduct)).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CriticalError | AmountBiggerThanAvailable | ProductPackageDoesNotExist
				| InvalidParameter e) {
			e.printStackTrace();
			fail();
		} catch (CustomerNotConnected __) {
			/* success */
		}
	}
	
	@Test
	public void addProductToCartAmountBiggerThanAvailableTest() {	
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.ADD_PRODUCT_TO_GROCERY_LIST, Serialization.serialize(pp)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_PRODUCT_PACKAGE_AMOUNT_BIGGER_THEN_AVAILABLE).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.VIEW_PRODUCT_FROM_CATALOG, Serialization.serialize(sc)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK, Serialization.serialize(catalogProduct)).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CriticalError | CustomerNotConnected | ProductPackageDoesNotExist
				| InvalidParameter e) {
			e.printStackTrace();
			fail();
		} catch (AmountBiggerThanAvailable __) {
			/* success */
		}
	}
	
	@Test
	public void addProductToCartProductPackageDoesNotExistTest() {	
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.ADD_PRODUCT_TO_GROCERY_LIST, Serialization.serialize(pp)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_PRODUCT_PACKAGE_DOES_NOT_EXIST).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.VIEW_PRODUCT_FROM_CATALOG, Serialization.serialize(sc)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK, Serialization.serialize(catalogProduct)).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CriticalError | CustomerNotConnected | AmountBiggerThanAvailable
				| InvalidParameter e) {
			e.printStackTrace();
			fail();
		} catch (ProductPackageDoesNotExist __) {
			/* success */
		}
	}
	
	@Test
	public void addProductToCartProductInvalidParameterTest() {	
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.ADD_PRODUCT_TO_GROCERY_LIST, Serialization.serialize(pp)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_INVALID_PARAMETER).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			Mockito.when(
				clientRequestHandler.sendRequestWithRespond((new CommandWrapper(CustomerDefs.loginCommandSenderId,
						CommandDescriptor.VIEW_PRODUCT_FROM_CATALOG, Serialization.serialize(sc)).serialize())))
				.thenReturn(new CommandWrapper(ResultDescriptor.SM_OK, Serialization.serialize(catalogProduct)).serialize());
		} catch (IOException ¢) {
			¢.printStackTrace();
			fail();
		}
		
		try {
			customer.addProductToCart(sc, amount);
		} catch (CriticalError | CustomerNotConnected | AmountBiggerThanAvailable
				| ProductPackageDoesNotExist e) {
			e.printStackTrace();
			fail();
		} catch (InvalidParameter __) {
			/* success */
		}
	}
}