import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

public class InteractionUtils {
   private WebDriver driver;
   private WaitUtils waitUtils;
   private Actions actions;

   InteractionUtils(WebDriver givenDriver) {
      this.driver = givenDriver;
      this.waitUtils = new WaitUtils(givenDriver);
      this.actions = new Actions(givenDriver);
   }

   /**
    * Scroll the viewport of the browser page to a specific element so it is visible.
    * @param givenXpath The xpath of the element to be scrolled to.
    */
   void scrollToLocator(String givenXpath) {
      try {
         ((JavascriptExecutor) driver).executeScript(
                 "arguments[0].scrollIntoView(true);",
                 waitUtils.waitForPresenceOfLocator(givenXpath));
      } catch (NoSuchElementException nse_ex) {
         ((JavascriptExecutor) driver).executeScript(
                 "arguments[0].scrollIntoView(true);",
                 waitUtils.waitForPresenceOfLocator(givenXpath));
      }
   }

   /**
    * Click on an element via the provided xpath, using javascript and actions instead of normal web elements.
    *    Firefox occasionally struggles to click elements on the edge of the page.
    *    A combination of javascript and actions can overcome this.
    *    Note: This method was created to solve a problem it didn't end up solving.
    *    I'm leaving it in, in case it is needed in the future -Adam McKeown
    * @param givenXpath The xpath of the element to be clicked.
    */
   void javascriptClickElementByXpath(String givenXpath){
      WebElement  element = driver.findElement(By.xpath(givenXpath));
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
      Actions act=new Actions(driver);
      act.scrollToElement(element);

      waitUtils.waitForVisibilityOfLocator(givenXpath);
      waitUtils.waitForClickabilityOfLocator(givenXpath);

      waitUtils.hardWait(1000);
      act.moveToElement(element).click().perform();
   }

   /**
    * Click on an element via the provided xpath.
    * @param givenXpath The xpath of the element to be clicked.
    */
   void clickOnElementByXpath(String givenXpath) {
      waitUtils.waitForPresenceOfLocator(givenXpath);
      waitUtils.waitForVisibilityOfLocator(givenXpath);
      WebElement element = waitUtils.waitForClickabilityOfElement(waitUtils.waitForVisibilityOfLocator(givenXpath));

      //I hate to use a thread.sleep here but ran out of options. - Adam McKeown
      try {
         element.click();
      } catch (ElementNotInteractableException ex){
         waitUtils.hardWait(1000);
         element.click();
      }
   }

   /**
    * Simulate the user hovering their mouse over an element, usually to open a dropdown menu.
    * @param givenXpath The xpath of the element to be hovered.
    */
   void hoverElementByXpath(String givenXpath) {
      scrollToLocator(givenXpath);
      actions.moveToElement(waitUtils.waitForVisibilityOfLocator(givenXpath)).perform();
   }
}
