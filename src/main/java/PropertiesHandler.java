import java.io.FileInputStream;
import java.util.Properties;

class PropertiesHandler {

   /**
    * Read the properties file.
    * @param fileName The name of the properties file to scan
    * @return A Properties object.
    */
   static Properties readPropertiesFile(String fileName) {
      FileInputStream fis = null;
      java.util.Properties prop = null;

      try {
         fis = new FileInputStream(fileName);
         prop = new java.util.Properties();
         prop.load(fis);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            fis.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return prop;
   }
}
