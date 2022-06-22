import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Base64;

public class TestSelenium {
   public static void main(String[] args) {
      RunTest test = new RunTest(new SetUpUtils().getDriver());
      test.checkLandingPage();
   }

   public static class SetUpUtils {
      private static final String USER_PATH = System.getProperty("user.dir");
      private static final String FIREFOX_DRIVER_PATH = USER_PATH + "\\src\\main\\resources\\geckodriver.exe";
      private static final String DRIVER_EXE = "webdriver.gecko.driver";

      WebDriver getDriver() {
         System.out.println("Launching Firefox");
         System.setProperty(DRIVER_EXE, FIREFOX_DRIVER_PATH);

         return new FirefoxDriver();
      }
   }


   public static class RunTest {
      WebDriver driver;
      WaitUtils waitUtils;
      Actions actions;
      StateUtils stateUtils;
      InteractionUtils interactionUtils;

      private static final String TEST_URL = "http://www.amazon.ca";
      private static final String FIRST_LOGIN_BUTTON_XPATH = "//div[@class='nav-bb-right']/a[text()='Your Account']";
      private static final String SECOND_LOGIN_BUTTON_XPATH = "//span[@id='nav-link-accountList-nav-line-1']";
      private static final String USERNAME_XPATH = "";
      private static final String PWORD_XPATH = "";

      private static final String EMAIL_INPUT_BOX_XPATH = "//input[@id='ap_email']";
      private static final String CONTINUE_BUTTON_XPATH = "//input[@id='continue']";
      private static final String PASSWORD_INPUT_BOX_XPATH = "//input[@id='ap_password']";
      private static final String SIGN_IN_BUTTON_XPATH = "//input[@id='signInSubmit']";


      private static final String LANDING_PATH_TITLE_BAR_XPATH = "//div[@id='nav-xshop']";
      private static final String BUY_AGAIN_XPATH = LANDING_PATH_TITLE_BAR_XPATH + "//a[contains(@class, 'nav-a') and text()='Buy Again']";
      private static final String LANGUAGE_DROPDOWN_XPATH = "//span[@class='icp-container-desktop']//div[@class='navFooterLine']//a[@id='icp-touch-link-language']";
      private static final String ENGLISH_LANGUAGE_DROPDOWN_SELECTION_XPATH = LANGUAGE_DROPDOWN_XPATH + "//span[@class='icp-color-base' and text()='English']";

      private static final String TITLE_BAR_USER_HOVER_MENU_XPATH = "//a[@id='nav-link-accountList']//span[@class='nav-line-2 ']";
      private static final String USER_HOVER_MENU_SIGNOUT_XPATH = "//a[@id='nav-item-signout']//span[@class='nav-text' and text()='Sign Out']";


      RunTest(WebDriver givenDriver) {
         this.driver = givenDriver;
         this.actions = new Actions(givenDriver);
         this.waitUtils = new WaitUtils(givenDriver);
         this.stateUtils = new StateUtils(givenDriver);
         this.interactionUtils = new InteractionUtils(givenDriver);
      }

      @BeforeMethod(alwaysRun = true)
      public void login() {
         System.out.println("Logging in");
         boolean firstLoginPageFound = true;

         driver.navigate().to(TEST_URL);

         //Amazon has two landing page title bars. We need to find out which log in button exists.
         //This could also be an if statement where I get a WebElements<List> and check its size, but I don't like that implementation.
         try {
            interactionUtils.clickOnElementByXpath(FIRST_LOGIN_BUTTON_XPATH);
         } catch (NoSuchElementException e) {
            firstLoginPageFound = false;
         }

         interactionUtils.clickOnElementByXpath(SECOND_LOGIN_BUTTON_XPATH);

         //Enter email address and click Continue
         byte[] decodedBytes = Base64.getDecoder().decode(USERNAME_XPATH);
         waitUtils.waitForVisibilityOfLocator(EMAIL_INPUT_BOX_XPATH).sendKeys(new String(decodedBytes));
         interactionUtils.clickOnElementByXpath(CONTINUE_BUTTON_XPATH);

         //Enter password and click Sign in.
         decodedBytes = Base64.getDecoder().decode(PWORD_XPATH);
         waitUtils.waitForVisibilityOfLocator(PASSWORD_INPUT_BOX_XPATH).sendKeys(new String(decodedBytes));
         interactionUtils.clickOnElementByXpath(SIGN_IN_BUTTON_XPATH);
      }

      @Test
      public void checkLandingPage() {
         login();
         System.out.println("Checking the Landing Page");

         //Verify that the "Buy Again" tab exists.
         Assert.assertTrue(stateUtils.verifyElementIsVisible(BUY_AGAIN_XPATH), "Buy again button is missing.");

         //Verify the language is set to English
         interactionUtils.scrollToLocator(ENGLISH_LANGUAGE_DROPDOWN_SELECTION_XPATH);
         Assert.assertTrue(stateUtils.verifyElementIsVisible(ENGLISH_LANGUAGE_DROPDOWN_SELECTION_XPATH), "Language is not set to English.");

         cleanUp();
      }

      @AfterMethod(alwaysRun = true)
      public void cleanUp() {
         System.out.println("Logging Out");
         interactionUtils.hoverElementByXpath(TITLE_BAR_USER_HOVER_MENU_XPATH);
         interactionUtils.clickOnElementByXpath(USER_HOVER_MENU_SIGNOUT_XPATH);
         driver.close();
      }
   }

   public static class StateUtils {
      WebDriver driver;
      WaitUtils waitUtils;

      StateUtils(WebDriver givenDriver) {
         this.driver = givenDriver;
         this.waitUtils = new WaitUtils(givenDriver);
      }

      boolean verifyElementIsVisible(String givenXpath) {
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


   public static class WaitUtils {
      WebDriver driver;
      Wait<WebDriver> wait;

      WaitUtils(WebDriver givenDriver) {
         this.driver = givenDriver;

         //This could be turned into a factory where we have a variety of wait times depending on the page in question.
         wait = new FluentWait<WebDriver>(driver)
                 .withTimeout(Duration.ofSeconds(10L))
                 .pollingEvery(Duration.ofSeconds(1L))
                 .ignoring(NoSuchElementException.class);
      }

      WebElement waitForVisibilityOfElement(WebElement givenElement) {
         return wait.until(ExpectedConditions.visibilityOf(givenElement));
      }

      WebElement waitForClickabilityOfElement(WebElement givenElement) {
         return wait.until(ExpectedConditions.elementToBeClickable(givenElement));
      }


      WebElement waitForVisibilityOfLocator(String givenXpath) {
         return waitForVisibilityOfElement(driver.findElement(By.xpath(givenXpath)));
      }

      WebElement waitForClickabilityOfLocator(String givenXpath) {
         return waitForClickabilityOfElement(driver.findElement(By.xpath(givenXpath)));
      }

      WebElement waitForPresenceOfLocator(String givenXpath) {
         return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(givenXpath)));
      }

   }

   public static class InteractionUtils {
      WebDriver driver;
      WaitUtils waitUtils;
      Actions actions;

      InteractionUtils(WebDriver givenDriver) {
         this.driver = givenDriver;
         this.waitUtils = new WaitUtils(givenDriver);
         this.actions = new Actions(givenDriver);
      }

      void scrollToLocator(String givenXpath) {
         //Sometimes when scrolling on an amazon page, a new block of content will appear, so what you just scrolled to
         //disappears off screen. So we try a second time.
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


      void clickOnElementByXpath(String givenXpath) {
         WebElement element = driver.findElement(By.xpath(givenXpath));
         waitUtils.waitForVisibilityOfElement(element);
         waitUtils.waitForClickabilityOfElement(element);
         element.click();
      }

      void hoverElementByXpath(String givenXpath) {
         scrollToLocator(givenXpath);
         actions.moveToElement(waitUtils.waitForVisibilityOfLocator(givenXpath)).perform();
      }
   }
}