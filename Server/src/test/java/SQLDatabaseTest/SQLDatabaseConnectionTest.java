package SQLDatabaseTest;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.HashSet;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import BasicCommonClasses.CatalogProduct;
import BasicCommonClasses.Ingredient;
import BasicCommonClasses.Location;
import BasicCommonClasses.Manufacturer;
import BasicCommonClasses.PlaceInMarket;
import BasicCommonClasses.ProductPackage;
import BasicCommonClasses.SmartCode;
import CommonDefs.CLIENT_TYPE;
import SQLDatabase.SQLDatabaseConnection;
import SQLDatabase.SQLDatabaseException;
import SQLDatabase.SQLDatabaseException.AuthenticationError;
import SQLDatabase.SQLDatabaseException.CriticalError;
import SQLDatabase.SQLDatabaseException.NumberOfConnectionsExceeded;
import SQLDatabase.SQLDatabaseException.ProductNotExistInCatalog;
import SQLDatabase.SQLDatabaseException.ProductPackageAmountNotMatch;
import SQLDatabase.SQLDatabaseException.ProductPackageNotExist;
import SQLDatabase.SQLDatabaseException.ProductStillForSale;
import SQLDatabase.SQLDatabaseException.ClientAlreadyConnected;
import SQLDatabase.SQLDatabaseException.ClientNotConnected;

/**
 * @author Noam Yefet
 * @since 2016-12-14
 */
public class SQLDatabaseConnectionTest {

	final long barcodeDebug = 423324;
	final LocalDate date112000 = LocalDate.of(2000, 1, 1);
	final LocalDate date232015 = LocalDate.of(2015, 3, 2);
	final Location locationWarehouse = new Location(0, 0, PlaceInMarket.WAREHOUSE);
	final Location locationStore = new Location(0, 0, PlaceInMarket.STORE);

	private CatalogProduct createDummyProduct(long barcode, String name, int manufacturerId, String manufacturerName,
			double price) {
		return new CatalogProduct(barcode, name, new HashSet<Ingredient>(),
				new Manufacturer(manufacturerId, manufacturerName), "", price, "", new HashSet<Location>());
	}

	@Test
	public void testInitialize() {
		new SQLDatabaseConnection().hashCode();
	}

	@Before
	public void setup() {
		PropertyConfigurator.configure("../log4j.properties");

	}

	@Test
	public void testWorkerConnection() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		int session = 0;
		try {
			session = sqlConnection.login("admin", "admin");

		} catch (AuthenticationError | CriticalError | ClientAlreadyConnected | NumberOfConnectionsExceeded e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.logout(session, "admin");
		} catch (CriticalError | ClientNotConnected e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testLogoutAll() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		int session = 0;
		try {
			session = sqlConnection.login("admin", "admin");
			sqlConnection.logoutAllUsers();
			session = sqlConnection.login("admin", "admin");
		} catch (AuthenticationError | CriticalError | ClientAlreadyConnected | NumberOfConnectionsExceeded e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.logout(session, "admin");
		} catch (CriticalError | ClientNotConnected e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSimpleGetProductFromCatalog() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		HashSet<Ingredient> ingredients = new HashSet<Ingredient>();
		ingredients.add(new Ingredient(1, "חלב"));
		HashSet<Location> locations = new HashSet<Location>();
		// locations.add(new Location(1, 1, PlaceInMarket.STORE));
		String milkImage = "";
		try {
			assertEquals(sqlConnection.getProductFromCatalog(null, 1234567890),
					new Gson().toJson((new CatalogProduct(1234567890L, "חלב", ingredients, new Manufacturer(1, "תנובה"),
							"", 10.5, milkImage, locations))));
		} catch (ProductNotExistInCatalog | ClientNotConnected | CriticalError e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSimpleGetProductFromCatalog2() {

		final long barcodeNum = 72900046;
		final int manufaturerID = 2;
		final String manufaturerName = "מאפיות ברמן";
		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		CatalogProduct product = null;

		try {
			product = new Gson().fromJson(sqlConnection.getProductFromCatalog(null, barcodeNum), CatalogProduct.class);
		} catch (ProductNotExistInCatalog | ClientNotConnected | CriticalError e) {
			e.printStackTrace();
			fail();
		}

		assertEquals(product.getBarcode(), barcodeNum);
		assertEquals(product.getManufacturer().getId(), manufaturerID);
		assertEquals(product.getManufacturer().getName(), manufaturerName);
	}

	@Test
	public void testSimpleAddRemoveProductFromCatalog() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		HashSet<Ingredient> ingredients = new HashSet<Ingredient>();
		HashSet<Location> locations = new HashSet<Location>();

		CatalogProduct newProduct = new CatalogProduct(123L, "name", ingredients, new Manufacturer(1, "תנובה"), "", 20,
				"", locations);
		try {
			sqlConnection.addProductToCatalog(null, newProduct);
			assertEquals(sqlConnection.getProductFromCatalog(null, newProduct.getBarcode()),
					new Gson().toJson(newProduct));
			sqlConnection.removeProductFromCatalog(null, new SmartCode(newProduct.getBarcode(), null));
		} catch (SQLDatabaseException e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.getProductFromCatalog(null, newProduct.getBarcode());
			fail();
		} catch (ProductNotExistInCatalog e) {

		} catch (ClientNotConnected e) {
			e.printStackTrace();
			fail();
		} catch (CriticalError e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testAddRemoveProductRealBarcodeFromCatalog() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		HashSet<Ingredient> ingredients = new HashSet<Ingredient>();
		HashSet<Location> locations = new HashSet<Location>();

		CatalogProduct newProduct = new CatalogProduct(7290010328246L, "thini", ingredients,
				new Manufacturer(1, "תנובה"), "", 20, "", locations);
		try {
			sqlConnection.addProductToCatalog(null, newProduct);
			assertEquals(sqlConnection.getProductFromCatalog(null, newProduct.getBarcode()),
					new Gson().toJson(newProduct));
			sqlConnection.removeProductFromCatalog(null, new SmartCode(newProduct.getBarcode(), null));
		} catch (SQLDatabaseException e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.getProductFromCatalog(null, newProduct.getBarcode());
			fail();
		} catch (ProductNotExistInCatalog e) {

		} catch (ClientNotConnected e) {
			e.printStackTrace();
			fail();
		} catch (CriticalError e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testRemoveCatalogProductStillForSell() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		CatalogProduct newProduct = createDummyProduct(456L, "testRemoveCatalogProductStillForSell", 1, "תנובה", 3.0);
		ProductPackage newPackage = new ProductPackage(new SmartCode(newProduct.getBarcode(), date232015), 5,
				locationWarehouse);

		// add catalog-product and add it to warehouse
		try {
			sqlConnection.addProductToCatalog(null, newProduct);
			assertEquals(sqlConnection.getProductFromCatalog(null, newProduct.getBarcode()),
					new Gson().toJson(newProduct));
			sqlConnection.addProductPackageToWarehouse(null, newPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, newPackage));
		} catch (SQLDatabaseException e) {
			e.printStackTrace();
			fail();
		}

		// try to remove
		try {
			sqlConnection.removeProductFromCatalog(null, new SmartCode(newProduct.getBarcode(), null));
			fail();
		} catch (ProductStillForSale e1) {

		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog e1) {
			e1.printStackTrace();
			fail();
		}

		// move to shelf
		try {
			sqlConnection.placeProductPackageOnShelves(null, newPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, newPackage));
			assertEquals("5", sqlConnection.getProductPackageAmonutOnShelves(null, newPackage));
		} catch (SQLDatabaseException e) {
			e.printStackTrace();
			fail();
		}

		// try to remove
		try {
			sqlConnection.removeProductFromCatalog(null, new SmartCode(newProduct.getBarcode(), null));
			fail();
		} catch (ProductStillForSale e1) {

		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog e1) {
			e1.printStackTrace();
			fail();
		}

		// move to shelf
		try {
			sqlConnection.removeProductPackageFromShelves(null, newPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, newPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, newPackage));
			sqlConnection.removeProductFromCatalog(null, new SmartCode(newProduct.getBarcode(), null));
		} catch (SQLDatabaseException e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testSimpleAddRemovePakage() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 5,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testAddTwicePakageToWarehouse() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 5,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("10", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));

			productPackage.setAmount(10);
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testRemovePakageFromWarehouseTwice() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 10,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("10", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));

			productPackage.setAmount(5);
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testRemoveMoreThanHaveFromWarehouse() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 10,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("10", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));

		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog e) {
			e.printStackTrace();
			fail();
		}

		productPackage.setAmount(11);
		try {
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			fail();
		} catch (ProductPackageAmountNotMatch e) {
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

		productPackage.setAmount(10);
		try {
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testRemoveNotExistedPakageFromWarehouse() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 10,
				locationWarehouse);

		try {
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			fail();
		} catch (ProductPackageNotExist e) {

		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testSimpleAddMoveToShelfRemovePakage() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 5,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));

			sqlConnection.placeProductPackageOnShelves(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("5", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			fail();
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch e) {
			e.printStackTrace();
			fail();
		} catch (ProductPackageNotExist e) {

		}

		try {
			sqlConnection.removeProductPackageFromShelves(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.close();
		} catch (CriticalError e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testMovePakageToShelfAndRemoveTwice() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 5,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("10", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));

			sqlConnection.placeProductPackageOnShelves(null, productPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("5", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));
			sqlConnection.placeProductPackageOnShelves(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("10", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			fail();
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch e) {
			e.printStackTrace();
			fail();
		} catch (ProductPackageNotExist e) {

		}

		try {
			sqlConnection.removeProductPackageFromShelves(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("5", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));
			sqlConnection.removeProductPackageFromShelves(null, productPackage);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, productPackage));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testSplitPackageToShelfPakage() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		ProductPackage productPackage = new ProductPackage(new SmartCode(barcodeDebug, date112000), 5,
				locationWarehouse);
		ProductPackage productPackageShelf = new ProductPackage(new SmartCode(barcodeDebug, date112000), 2,
				locationWarehouse);

		try {
			sqlConnection.addProductPackageToWarehouse(null, productPackage);
			assertEquals("5", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, productPackageShelf));

			sqlConnection.placeProductPackageOnShelves(null, productPackageShelf);
			assertEquals(productPackage.getAmount() - productPackageShelf.getAmount() + "",
					sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals(productPackageShelf.getAmount() + "",
					sqlConnection.getProductPackageAmonutOnShelves(null, productPackageShelf));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch
				| ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

		productPackage.setAmount(3);
		try {
			sqlConnection.removeProductPackageFromWarehouse(null, productPackage);
			sqlConnection.removeProductPackageFromShelves(null, productPackageShelf);
			assertEquals("0", sqlConnection.getProductPackageAmonutInWarehouse(null, productPackage));
			assertEquals("0", sqlConnection.getProductPackageAmonutOnShelves(null, productPackageShelf));
		} catch (CriticalError | ClientNotConnected | ProductNotExistInCatalog | ProductPackageAmountNotMatch e) {
			e.printStackTrace();
			fail();
		} catch (ProductPackageNotExist e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testCartConnection() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		int session = 0;
		try {
			session = sqlConnection.login("Cart", "");
			assertTrue(sqlConnection.isClientLoggedIn(session));
		} catch (AuthenticationError | CriticalError | ClientAlreadyConnected | NumberOfConnectionsExceeded e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.logout(session, "Cart");
			assertFalse(sqlConnection.isClientLoggedIn(session));
		} catch (CriticalError | ClientNotConnected e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCartClientType() {

		SQLDatabaseConnection sqlConnection = new SQLDatabaseConnection();

		int session = 0;

		try {
			session = sqlConnection.login("Cart", "");
			assertTrue(sqlConnection.isClientLoggedIn(session));
			assertEquals(new Gson().toJson(CLIENT_TYPE.CART), sqlConnection.getClientType(session));
		} catch (AuthenticationError | CriticalError | ClientAlreadyConnected | NumberOfConnectionsExceeded
				| ClientNotConnected e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.logout(session, "Cart");
			assertFalse(sqlConnection.isClientLoggedIn(session));
		} catch (CriticalError | ClientNotConnected e) {
			e.printStackTrace();
			fail();
		}

		try {
			sqlConnection.getClientType(session);
			fail();
		} catch (ClientNotConnected e) {

		} catch (CriticalError e) {
			e.printStackTrace();
			fail();
		}
	}

}
