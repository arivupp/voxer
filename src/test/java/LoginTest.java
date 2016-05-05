import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by keerthana  goin to mergegon 2/19/2016.
 */

public class LoginTest {
    WebDriver driver;
    AppiumDriverLocalService service;

    @BeforeClass
    public void setUp() throws MalformedURLException,IOException,InterruptedException {

        // Start Virtual Device
        // VBoxManage.exe list vms
        // player --vm-name "Custom Tablet - 4.4.4 - API 19 - 2560x1600"
        String deviceName = getDeviceName();
        System.out.println("Device Found for Testing " + deviceName);
        if(deviceName == null) {
            System.out.println("Starting GenyMotion Device...");
            executeCommand("player --vm-name \"Custom Tablet - 4.4.4 - API 19 - 2560x1600\"", false);
            waitForDevice();
       }


        // Starts Appium Server. Node.exe should be added to PATH Environment variable
        service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .withLogFile(new File("target/" + "appium" + ".log")));
        service.start();


        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformVersion", "4.4");
        capabilities.setCapability("deviceName","192.168.56.101:5555");
        capabilities.setCapability("platformName","Android");
        capabilities.setCapability("app", "c:\\Users\\apandian\\Downloads\\Voxer-Enterprise.apk");
        capabilities.setCapability("appPackage", "com.rebelvox.enterprise");
        capabilities.setCapability("appActivity","com.rebelvox.voxer.Intents.Splash"); // This is Launcher activity of your app (you can get it from apk info app)

        driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(80, TimeUnit.SECONDS);
    }

    private void waitForDevice() throws IOException,InterruptedException{
        int waitTime =  3000;
        while(waitTime < 90000){
            if(getDeviceName() == null){
                System.out.println("Waiting for device...");
                Thread.sleep(45000);
                waitTime += 3000;
            }
            else {
                System.out.println("Device Found");
                return;
            }

        }
        System.out.println("No Device Found For Testing.. Exiting");
        System.exit(0);

    }
    private  String getDeviceName() throws IOException, InterruptedException{
        List<String> output = executeCommand("adb devices", true);
        if(output.size() > 1 && output.get(1).length() > 0)
            return output.get(1).split("device")[0].trim();

        return null;
    }

    private List<String> executeCommand(String command, Boolean waitForProcess) throws IOException,InterruptedException{
        java.lang.Runtime rt;
        java.lang.Process p;


        rt = java.lang.Runtime.getRuntime();
        p = rt.exec(command);

        List<String> line = new ArrayList<String>();

        if(waitForProcess) {
            p.waitFor();

            //  System.out.println("Process exited with code = " + p.exitValue());
            // Get process' output: its InputStream
            java.io.InputStream is = p.getInputStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
            // And print each line
            String s = null;

            while ((s = reader.readLine()) != null) {
                System.out.println(s);
                line.add(s);
            }
            is.close();
        }
        return line;
    }

    @Test
    public void testValidUserNameAndPassword() throws Exception {

        WebElement login=driver.findElement(By.id("com.rebelvox.enterprise:id/vs_loginButton"));
        login.click();
        driver.findElement(By.id("android:id/button1")).click();
        driver.findElement(By.id("com.rebelvox.enterprise:id/eff_email")).sendKeys("col3@ml.com");
        driver.findElement(By.id("com.rebelvox.enterprise:id/ll_password")).sendKeys("col3");
        driver.findElement(By.id("com.rebelvox.enterprise:id/ll_login_button")).click();

    }

    @AfterClass
    public void teardown(){
        //close the app
        driver.quit();
        service.stop();
    }
}
