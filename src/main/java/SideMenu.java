import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SideMenu {
   private WebDriver driver;
   private InteractionUtils interaction;
   private  WaitUtils waitUtils;

   private static final String SIDE_MENU_XPATH = "//span[contains(@class, 'sidemenu-label') and contains(text(), '%s')]";

   SideMenu(WebDriver givenDriver) {
      this.driver = givenDriver;
      this.interaction = new InteractionUtils(driver);
      this.waitUtils = new WaitUtils(driver);
   }

   enum SideMenuButtons {

      AMENITIES("Amenities");
      String button_name;

      //Constructor to define name
      SideMenuButtons(String button_name) {
         this.button_name = button_name;
      }

      //override the inherited method
      @Override
      public String toString() {
         return button_name;
      }
   }

   /**
    * Method used to navigate between links in the side menu navigation panel.
    * @param givenMenuButton An enum that corresponds to a side menu panel link.
    */
   void sideMenuNavigation(SideMenuButtons givenMenuButton){
      interaction.clickOnElementByXpath(String.format(SIDE_MENU_XPATH, givenMenuButton.toString()));

      waitUtils.waitForTribePageLoad();
   }
}
