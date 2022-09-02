
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {
   private WebDriver driver;
   private WebDriverWait wait;

   WaitUtils(WebDriver givenDriver) {
      this.driver = givenDriver;

      //This could be turned into a factory where we have a variety of wait times depending on the page in question.
      this.wait = new WebDriverWait(driver, Duration.ofSeconds(30L));
   }

   /**
    * Makes sure an element is visible.
    * @param givenElement The element to wait for
    * @return A WebElement
    */
   WebElement waitForVisibilityOfElement(WebElement givenElement) {
      return wait.until(ExpectedConditions.visibilityOf(givenElement));
   }

   /**
    * Makes sure an element is NOT visible.
    * @param givenElement The element to wait for
    * @return True if the element is NOT visible, false if the element IS visible.
    */
   boolean waitForInvisibilityOfElement(WebElement givenElement) {
      return wait.until(ExpectedConditions.invisibilityOf(givenElement));
   }

   /**
    * Makes sure an element is clickable.
    * @param givenElement The element to verify clickability
    * @return A WebElement
    */
   WebElement waitForClickabilityOfElement(WebElement givenElement) {
      WebElement result;

      //If an element cannot be clicked on the first try, there could be a loading spinner on the page.
      //Even though we should check for these elsewhere, this is a last ditch effort to try again.
      try {
         result = wait.until(ExpectedConditions.elementToBeClickable(givenElement));
      } catch (ElementNotInteractableException | NoSuchElementException | TimeoutException ex){
         System.out.println("Problem with Element, waiting and trying again: " + ex);
         waitForTribePageLoad();
         waitForBookingFormLoad();
         waitForPageLoader();
         result = wait.until(ExpectedConditions.elementToBeClickable(givenElement));
      }
      return result;
   }

   /**
    * An awful, last ditch effort. If a page loads so asynchronously, or unreliably, that no explicit wait will
    * function, a hard wait can be used.
    * @param miliseconds How long to wait for.
    */
   void hardWait(int miliseconds){
      try{
         Thread.sleep(miliseconds);
      } catch (InterruptedException ie){
         System.out.println("Thread Sleep Exception: " + ie);
      }
   }

   /**
    * Makes sure an element is visible, by looking for its xpath
    * @param givenXpath The xpath to verify visibility
    * @return A WebElement
    */
   WebElement waitForVisibilityOfLocator(String givenXpath) {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(givenXpath)));
   }

   /**
    * Makes sure an element is NOT visible, by looking for its xpath
    * @param givenXpath The xpath to verify invisibility
    * @return A WebElement
    */
   boolean waitForInvisibilityOfLocator(String givenXpath) {
      //If a TimeoutException occurs, the element isn't found and we can move on.
      try {
         return wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(givenXpath)));
      } catch (TimeoutException ex){
         return true;
      }
   }

   /**
    * Makes sure an element is clickable, by looking for its xpath
    * @param givenXpath The xpath to verify clickability
    * @return A WebElement
    */
   WebElement waitForClickabilityOfLocator(String givenXpath) {
      WebElement result;

      //If an element cannot be clicked on the first try, there could be a loading spinner on the page.
      //Even though we should check for these elsewhere, this is a last ditch effort to try again.
      try {
         result = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(givenXpath)));
      } catch (ElementNotInteractableException | NoSuchElementException | TimeoutException ex){
         System.out.println("Problem with Element, waiting and trying again: " + ex);
         waitForTribePageLoad();
         waitForBookingFormLoad();
         waitForPageLoader();
         result = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(givenXpath)));
      }
      return result;
   }

   /**
    * Makes sure an element is present in the DOM, by looking for its xpath
    * @param givenXpath The xpath to verify presence
    * @return A WebElement
    */
   WebElement waitForPresenceOfLocator(String givenXpath) {
      return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(givenXpath)));
   }

   /**
    * Wait for the generic Tribe website navigation loading spinner to disappear.
    */
   void waitForTribePageLoad(){
      waitForInvisibilityOfLocator("//div[@class='loading-msg-spinner']");
   }

   /**
    * Wait for the booking form load spinner to disappear.
    */
   void waitForBookingFormLoad(){
      waitForInvisibilityOfLocator("//div[@class='form-submitting']");
   }

   /**
    * Wait for the page refresh loading spinner to disappear.
    */
   void waitForPageLoader(){
      waitForInvisibilityOfLocator("//aside[@class='page-loader']");
   }


}
