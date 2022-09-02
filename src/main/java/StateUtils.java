import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class StateUtils {
   private WebDriver driver;
   private WaitUtils waitUtils;

   StateUtils(WebDriver givenDriver) {
      this.driver = givenDriver;
      this.waitUtils = new WaitUtils(givenDriver);
   }

   /**
    * Ensure that a specific locator is visible to the user.
    * @param givenXpath The xpath of the element to be verified
    * @return True if the locator is visible, false if the locator is not visible.
    */
   boolean verifyLocatorIsVisible(String givenXpath) {
      boolean result = false;

      try {
         waitUtils.waitForVisibilityOfLocator(givenXpath);
         result = true;
      } catch (NoSuchElementException nse_ex) {
         System.out.println("No Such Element exception: " + nse_ex);
      } catch (TimeoutException time_ex) {
         System.out.println("Timeout Exception: " + time_ex);
      }

      return result;
   }
}
