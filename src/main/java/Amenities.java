import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class Amenities {

   private WebDriver driver;
   private InteractionUtils interaction;
   private  WaitUtils waitUtils;
   private StateUtils state;

   //the use of a . identifier is because some elements on the Amenities page have text not attached to a tag, e.g. span or div.
   private static final String AMENITIES_LINK_XPATH = "//span[contains(.,'%s')]";
   private static final String DATE_PICKER_XPATH = "//div[@class='date-picker-controls']";
   private static final String DATE_PICKER_DATES_XPATH = "//div[contains(@class, 'datepicker')]//td[text()='%s']";

   //normally I'd use single quotes for the attribute name, so I wouldn't have to escape double quotes. But xpath can't escape single quotes, so here we are.
   private static final String DATE_PICKER_NEXT_MONTH_XPATH = "//th[contains(@ng-click,\"next('month')\")]";

   private static final String BOOKING_CARD_TITLE_XPATH = "//booking-details//div[@class='card-info']//div[text()='%s']";
   private static final String COMMENT_XPATH = "//textarea[@id='replyForm_comment']";
   private static final String COMMENT_BUTTON_XPATH = "//button[@data-testid='comment-btn']";
   private static final String COMMENT_BUTTON_SPINNING_XPATH = "//button[@data-testid='comment-btn' and @disabled='']";
   private static final String BOOKING_BUTTON_XPATH = "//button[contains(@class, 'create-booking-btn')]";
   private static final String NEXT_BUTTON_XPATH = "//button[@type='submit' and contains(text(), 'Next')]";
   private static final String SUBMIT_BUTTON_XPATH = "//button[@type='submit' and contains(text(), 'Submit Request')]";
   private static final String POSTED_COMMENT_XPATH = "//div[@class='loop-msg-body' and text()='%s']";

   private final static String BOOKING_START_HOUR_XPATH = "//select[@ng-change='updateRangeByStartHour()']";
   private final static String BOOKING_START_MINUTE_XPATH = "//select[contains(@ng-options,'availableStartMinutes')]";
   private final static String BOOKING_DURATION_HOUR_XPATH = "//select[contains(@ng-options,'durationHours')]";
   private final static String BOOKING_DURATION_MINUTE_XPATH = "//select[contains(@ng-options,'durationMinutes')]";

   Amenities(WebDriver givenDriver) {
      this.driver = givenDriver;
      this.interaction = new InteractionUtils(driver);
      this.waitUtils = new WaitUtils(driver);
      this.state = new StateUtils(driver);
   }

   enum AmenitiesLinks {
      TODDLER_BOOTCAMP("Toddler Bootcamp");
      String link_name;

      //Constructor to define name
      AmenitiesLinks(String link_name) {
         this.link_name = link_name;
      }

      //override the inherited method
      @Override
      public String toString() {
         return link_name;
      }
   }

   /**
    * Click on a specific link within the Amenities page.
    * @param givenLink The enum for the link to which we will navigate.
    */
   void amenitiesNavigation(AmenitiesLinks givenLink){
      waitUtils.waitForTribePageLoad();
      waitUtils.waitForPageLoader();
      waitUtils.waitForClickabilityOfLocator(String.format(AMENITIES_LINK_XPATH, givenLink.toString()));
      interaction.clickOnElementByXpath(String.format(AMENITIES_LINK_XPATH, givenLink.toString()));
      waitUtils.waitForTribePageLoad();
   }

   /**
    * Click on a date picker on the Amenities Page.
    */
   private void openDatePicker(){
      waitUtils.waitForBookingFormLoad();
      interaction.clickOnElementByXpath(DATE_PICKER_XPATH);
      interaction.scrollToLocator(DATE_PICKER_NEXT_MONTH_XPATH);
   }

   /**
    * Select a date from the date picker that has been opened.
    * @param givenDate An int for the date to select.
    */
   private void selectDate(int givenDate){
      openDatePicker();
      interaction.clickOnElementByXpath(DATE_PICKER_NEXT_MONTH_XPATH);
      interaction.clickOnElementByXpath(String.format(DATE_PICKER_DATES_XPATH, givenDate));
      waitUtils.waitForBookingFormLoad();
   }

   /**
    * A method to go through the full flow of requesting a booking at a specific date and time.
    * @param givenDate The calendar date to select.
    * @param startHour The hour at which the booking begins.
    * @param startMin The minute at which the booking begins.
    * @param durHour The booking's duration hour.
    * @param durMin The booking's duration minutes.
    * @return True if the booking is successful, false if the booking is unsuccessful.
    */
   boolean requestBooking(int givenDate, String startHour, String startMin, String durHour, String durMin){
      selectDate(givenDate);
      interaction.clickOnElementByXpath(BOOKING_BUTTON_XPATH);

      boolean isStartTimeAvailable = requestStartTime(startHour, startMin);
      boolean isRequestedDurationAvailable = requestDurationTime(durHour, durMin);

      //Check to see if the booking time is available.
      if (!isStartTimeAvailable || !isRequestedDurationAvailable){
         return false;
      }

      requestStartTime(startHour, startMin);
      requestDurationTime(durHour, durMin);
      interaction.clickOnElementByXpath(NEXT_BUTTON_XPATH);
      interaction.clickOnElementByXpath(SUBMIT_BUTTON_XPATH);
      waitUtils.waitForBookingFormLoad();
      return true;
   }

   /**
    * Method to determine if a booking available.
    * @param givenDate The calendar date to check.
    * @param startHour The hour at which the booking begins.
    * @param startMin The minute at which the booking begins.
    * @param durHour The booking's duration hour.
    * @param durMin The booking's duration minutes.
    * @return True if the booking is available, false if the booking is not available.
    */
   boolean isBookingAvailable(int givenDate, String startHour, String startMin, String durHour, String durMin){
      selectDate(givenDate);
      interaction.clickOnElementByXpath(BOOKING_BUTTON_XPATH);
      boolean isStartTimeAvailable = requestStartTime(startHour, startMin);
      boolean isRequestedDurationAvailable = requestDurationTime(durHour, durMin);

      if (!isStartTimeAvailable || !isRequestedDurationAvailable){
         return false;
      } else {
         return true;
      }
   }

   /**
    * Method to confirm that a booking that was requested was actually made.
    * @param givenTime The String that indicates the beginning and end time of the booking.
    * @return True if the booking was created successfully, false if it was not created successfully.
    */
   boolean confirmBooking(String givenTime){
      return state.verifyLocatorIsVisible(String.format(BOOKING_CARD_TITLE_XPATH, givenTime));
   }

   /**
    * Method to click (select) a specific booking from the Amenities page.
    * @param givenTime The String that indicates the beginning and end time of the booking.
    */
   void selectBooking(String givenTime){
      interaction.clickOnElementByXpath(String.format(BOOKING_CARD_TITLE_XPATH, givenTime));

      waitUtils.waitForTribePageLoad();
      waitUtils.waitForBookingFormLoad();
      waitUtils.waitForPageLoader();
   }

   /**
    * Method to add a comment to a booking that has been created previously.
    * @param givenTime The String that indicates the beginning and end time of the booking.
    * @param commentToAdd The comment to be added to the booking.
    */
   void addCommentToBooking(String givenTime, String commentToAdd){
      selectBooking(givenTime);

      WebElement element = waitUtils.waitForClickabilityOfLocator(COMMENT_XPATH);
      interaction.clickOnElementByXpath(COMMENT_XPATH);
      WebElement commentButton = waitUtils.waitForVisibilityOfLocator(COMMENT_BUTTON_XPATH);

      //After clicking the "Add a comment" area, the DOM changes, and we need to refresh the element.
      element = waitUtils.waitForClickabilityOfLocator(COMMENT_XPATH);
      element.sendKeys(commentToAdd);
      waitUtils.waitForInvisibilityOfLocator(COMMENT_BUTTON_SPINNING_XPATH);

      interaction.scrollToLocator(COMMENT_BUTTON_XPATH);
      waitUtils.waitForClickabilityOfElement(commentButton);
      interaction.clickOnElementByXpath(COMMENT_BUTTON_XPATH);

      waitUtils.waitForInvisibilityOfLocator(COMMENT_BUTTON_XPATH);

      //This doesn't verify that the comment it sees is MY comment. I'd need to add a time stamp checker for that.
      waitUtils.waitForVisibilityOfLocator(String.format(POSTED_COMMENT_XPATH, commentToAdd));
   }

   /**
    * Method to verify that a specific comment appears on the booking page.
    * @param expectedComment The String indicating the comment that is expected to be present.
    * @return True if the comment is visibible, false if the comment is not visible.
    */
   boolean commentAppears(String expectedComment){
      return state.verifyLocatorIsVisible(String.format(POSTED_COMMENT_XPATH, expectedComment));
   }

   /**
    * Method to see if a specific start time is available.
    * @param startHour The starting hour of the request.
    * @param startMin The starting minute of the request.
    * @return True if the start time is available, false if the start time is not available.
    */
   private boolean requestStartTime(String startHour, String startMin){
      try {
         WebElement startTime = waitUtils.waitForVisibilityOfLocator(BOOKING_START_HOUR_XPATH);
         waitUtils.waitForClickabilityOfElement(startTime);
         Select startHourDropdown = new Select(startTime);
         startHourDropdown.selectByVisibleText(startHour);
         Select startMinDropdown = new Select(driver.findElement(By.xpath(BOOKING_START_MINUTE_XPATH)));
         startMinDropdown.selectByVisibleText(startMin);
         return true;
      } catch (NoSuchElementException e){
         System.out.println("Expected start time is not available, meaning the time slot has been booked");
         return false;
      }
   }

   /**
    * Method to see if a specific requested duration is available.
    * @param requestHour The duration hour of the request.
    * @param requestMin The duration minute of the request.
    * @return True if the duration is available, false if the duration is not available.
    */
   private boolean requestDurationTime(String requestHour, String requestMin){

      try {
         WebElement requestTime = waitUtils.waitForVisibilityOfLocator(BOOKING_DURATION_HOUR_XPATH);
         waitUtils.waitForClickabilityOfElement(requestTime);

         Select requestHourDropdown = new Select(requestTime);
         requestHourDropdown.selectByVisibleText(requestHour);

         Select requestMinDropdown = new Select(driver.findElement(By.xpath(BOOKING_DURATION_MINUTE_XPATH)));
         requestMinDropdown.selectByVisibleText(requestMin);

         return true;
      } catch (NoSuchElementException e){
         System.out.println("Expected duration is not available, meaning the time slot has been booked");

         return false;
      }
   }
}
