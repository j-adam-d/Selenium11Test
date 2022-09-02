import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import java.util.Base64;
import java.util.Properties;

public class TestSelenium {
   public static void main(String[] args) {
      RunTest test = new RunTest(new SetUpUtils().getDriver());
      test.createBookingAndVerify();
   }

   public static class SetUpUtils {
      private static final String USER_PATH = System.getProperty("user.dir");

      final static PropertiesHandler propHandler = new PropertiesHandler();
      final static Properties prop = propHandler.readPropertiesFile(USER_PATH + "\\src\\main\\resources\\config.properties");

      private static final String DRIVER_PATH = USER_PATH + "\\src\\main\\resources\\" + prop.getProperty("BROWSER") + "driver.exe";
      private static final String DRIVER_EXE = "webdriver." + prop.getProperty("BROWSER") + ".driver";

      /**
       * Create a new driver for the browser tests to run in.
       * This defa
       * @return
       */
      WebDriver getDriver() {
         System.out.println("Launching Browser");
         System.setProperty(DRIVER_EXE, DRIVER_PATH);


         switch (prop.getProperty("BROWSER")){
            case ("gecko"):
               return new FirefoxDriver();
            case ("chrome"):
               return new ChromeDriver();
            default:
               return new FirefoxDriver();
         }
      }
   }

   public static class RunTest {
      private static final String EMAIL_INPUT_BOX_XPATH = "//input[@id='login_email']";
      private static final String PASSWORD_INPUT_BOX_XPATH = "//input[@id='login_password']";
      private static final String SIGN_IN_BUTTON_XPATH = "//button[@data-testid='submit']";

      private static final int BOOKING_DATE = 10;
      private static final String BOOKING_START_HOUR = "6 pm";
      private static final String BOOKING_START_MINUTE = "05";
      private static final String BOOKING_DURATION_HOUR = "1 hr";
      private static final String BOOKING_DURATION_MINUTE = "30 min";
      private static final String BOOKING_DURATION_STRING = "6:05pm - 7:35pm";
      private static final String BOOKING_COMMENT_STRING = "Test comment for QA";

      WebDriver driver;
      WaitUtils waitUtils;
      Actions actions;
      StateUtils stateUtils;
      InteractionUtils interactionUtils;

      RunTest(WebDriver givenDriver) {
         this.driver = givenDriver;
         this.actions = new Actions(givenDriver);
         this.waitUtils = new WaitUtils(givenDriver);
         this.stateUtils = new StateUtils(givenDriver);
         this.interactionUtils = new InteractionUtils(givenDriver);
      }

      void login() {
         System.out.println("Logging in");

         driver.navigate().to(SetUpUtils.prop.getProperty("TEST_URL"));
         driver.manage().window().maximize();


         waitUtils.waitForTribePageLoad();

         //Enter email address and password and log in.
         byte[] decodedBytes = Base64.getDecoder().decode(SetUpUtils.prop.getProperty("USER"));
         waitUtils.waitForVisibilityOfLocator(EMAIL_INPUT_BOX_XPATH).sendKeys(new String(decodedBytes));

         decodedBytes = Base64.getDecoder().decode(SetUpUtils.prop.getProperty("PWORD"));
         waitUtils.waitForVisibilityOfLocator(PASSWORD_INPUT_BOX_XPATH).sendKeys(new String(decodedBytes));

         interactionUtils.clickOnElementByXpath(SIGN_IN_BUTTON_XPATH);
      }

      /**
       * Author: Adam McKeown
       * Date: 1 September, 2022
       * 1. Log in using the provided account
       * 2. Enter the bazinga Test Building community
       * 3. On the side navigation bar, click on Amenities
       * 4. Click on the Toddler Bootcamp amenity
       * 5. From the date picker, select a day in the future when the amenity is open and bookable
       * 6. Click on Request a Booking
       * 7. Book a time slot for 1 hour and 30 minutes
       * 8. Verify that the event shows up under Bookings and events on this day
       * 9. Open up the Booking Request and add a comment. Confirm the comment
       * appears.
       * 10. Try to book the exact same time slot and confirm that you cannot book an
       * overlapping time
       */
      void createBookingAndVerify() {
         login();

         waitUtils.waitForTribePageLoad();

         SideMenu sideMenu = new SideMenu(driver);
         sideMenu.sideMenuNavigation(SideMenu.SideMenuButtons.AMENITIES);

         Amenities amenitiesPage = new Amenities(driver);
         amenitiesPage.amenitiesNavigation(Amenities.AmenitiesLinks.TODDLER_BOOTCAMP);
         Assert.assertTrue(amenitiesPage.requestBooking(BOOKING_DATE, BOOKING_START_HOUR, BOOKING_START_MINUTE, BOOKING_DURATION_HOUR, BOOKING_DURATION_MINUTE));
         Assert.assertTrue(amenitiesPage.confirmBooking(BOOKING_DURATION_STRING));

         amenitiesPage.addCommentToBooking(BOOKING_DURATION_STRING, BOOKING_COMMENT_STRING);
         Assert.assertTrue(amenitiesPage.commentAppears(BOOKING_COMMENT_STRING));

         sideMenu.sideMenuNavigation(SideMenu.SideMenuButtons.AMENITIES);
         amenitiesPage.amenitiesNavigation(Amenities.AmenitiesLinks.TODDLER_BOOTCAMP);
         Assert.assertFalse(amenitiesPage.isBookingAvailable(BOOKING_DATE, BOOKING_START_HOUR, BOOKING_START_MINUTE, BOOKING_DURATION_HOUR, BOOKING_DURATION_MINUTE));

         cleanUp();
      }

      void cleanUp() {
         System.out.println("Logging Out");
         driver.close();
      }
   }
}