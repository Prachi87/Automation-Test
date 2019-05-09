package com.selenium.datadriven.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.selenium.datadriven.util.ExtentManager;

import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;


public class BaseTest {

	public AndroidDriver driver=null;
	public Properties prop;
	public ExtentReports rep=ExtentManager.getInstance();
	public ExtentTest test;
	Dimension size;
	
	public void init(){
		//initial the properties
		if(prop==null){
			prop=new Properties();
			try {
				FileInputStream fs=new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//projectconfig.properties");
				prop.load(fs); //"fs" will load the properties
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	}
	
	/**************************************Launching********************************/

	public void launchapp() throws MalformedURLException{
		DesiredCapabilities capabilities=new DesiredCapabilities();
		  String apkpath="D:\\com.makemytrip_2017-07-31.apk";
		  File app=new File(apkpath);
		  
		  capabilities.setCapability("device", "Android");
		  capabilities.setCapability("deviceName","OnePlus2");
		  capabilities.setCapability("platformVersion", "6.0.1");
		  capabilities.setCapability("platformName", "Android");
		  capabilities.setCapability("app", app.getAbsolutePath());
		  capabilities.setCapability("appPackage", "com.makemytrip");
		  capabilities.setCapability("appPackage", "com.mmt.travel.app.homepage.activity.OnBoardingActivity");
		  
		  capabilities.setCapability("automationName", "uiautomator2");
		  
		  driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
		  driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
	}
	
	/**************************************UI********************************/

	public void type(String locatorKey,String data){
		
		test.log(LogStatus.INFO, "Typing in "+locatorKey+". Data - "+data);
		getElement(locatorKey).sendKeys(data);
		wait(5);
		test.log(LogStatus.INFO, "Typed Successfully in "+locatorKey);
	}

	public void click(String locatorKey){
		test.log(LogStatus.INFO, "Clicking on "+locatorKey);
		getElement(locatorKey).click();
		test.log(LogStatus.INFO, "Clicked on "+locatorKey);
	}

	public WebElement getElement(String locatorKey){
		WebElement e=null;
		try{
		if(locatorKey.endsWith("_id"))
			e=driver.findElement(By.id(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_name"))
			e=driver.findElement(By.name(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_xpath"))
			e=driver.findElement(By.xpath(prop.getProperty(locatorKey)));
		else{
			reportFailure("Locator not correct-"+locatorKey);
			Assert.fail("Locator not correct-"+locatorKey);
		}
		
		}catch(Exception ex){
			//fail test and report error
			reportFailure(ex.getMessage());
			ex.printStackTrace();
			Assert.fail("Failed the test - " +ex.getMessage());
		}
		return e;
		
	}
	
	
	
	
	/**************************************Validations********************************/

	public boolean verifyHeader(){
		return false;
		
	}

	public boolean isElementPresent(String locatorKey){
		List<WebElement> elementList=null;
		if(locatorKey.endsWith("_id"))
			elementList=driver.findElements(By.id(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_name"))
			elementList=driver.findElements(By.name(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_xpath"))
			elementList=driver.findElements(By.xpath(prop.getProperty(locatorKey)));
		else{
			reportFailure("Locator not correct-"+locatorKey);
			Assert.fail("Locator not correct-"+locatorKey);
		}
		
		if(elementList.size()==0)
		return false;
		else
			return true;
		
	}
	
	public boolean verifyText(String locatorKey,String expectedTextKey){
		String actualText=getElement(locatorKey).getText().trim();
		String expectedText=prop.getProperty(expectedTextKey);
		if(actualText.equals(expectedTextKey))
		return true;
		else
			return false;
		
	}
	
	
	public void clickAndWait(String locator_clicked,String locator_pres){
		test.log(LogStatus.INFO, "Clicking and waiting on "+locator_clicked);
		int count=5;
		for(int i=0;i<count;i++){
			getElement(locator_clicked).click();
			wait(3);
			if(isElementPresent(locator_pres))
				break;
			
		}
	}
	
	/**************************************Reporting********************************/

	public void reportPass(String msg){
		test.log(LogStatus.PASS, msg);
	}

	public void reportFailure(String msg){
		test.log(LogStatus.FAIL, msg);
		takeScreenshot();
		Assert.fail(msg);
	}

	public void takeScreenshot(){
		//fileName of screenshot
		Date d=new Date();
		String screenshotFile=d.toString().replace(":", "_").replace(" ", "_")+".png";
		//store screenshot in file
		File scrFile=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try{
			FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
		}catch(IOException e){
			e.printStackTrace();
		}
		//put screenshot file in reports
		test.log(LogStatus.INFO, "Screenshot->"+test.addScreenCapture(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
	}


	public void waitForPageLoad(){
	
		JavascriptExecutor js=(JavascriptExecutor)driver;
		String state=(String)js.executeScript("return document.readyState");
		
		while(!state.equals("Complete")){
			state=(String)js.executeScript("return document.readyState");
			wait(2);
			
		}
	}

	public void wait(int timeToWaitInSec){
		try{
			Thread.sleep(timeToWaitInSec*1000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public String getText(String locatorKey){
		test.log(LogStatus.INFO, "Getting text from "+locatorKey);
		return getElement(locatorKey).getText();

	}
	
	/**************************************App Functions********************************/
	
	public boolean doLogin(String username,String password){
		test.log(LogStatus.INFO, "Trying to login with "+username+","+password);
		click("loginlaunchbutton_id");
		wait(5);
		type("email_id",username);
		type("password_id",password);
		driver.hideKeyboard();
		click("Loginbutton_id");
		driver.manage().timeouts().implicitlyWait(7000, TimeUnit.MILLISECONDS);
		
		if(isElementPresent("flighttab_id")){
			test.log(LogStatus.INFO, "Login success");
			
			return true;
		}
		
		else{
			test.log(LogStatus.INFO, "Login failed");
			return false;
		}
	
	}
	
	public void doLogout(String locatorKey){
		test.log(LogStatus.INFO, "Clicking on "+locatorKey);
		click("mybutton_id");
		click("profiletab_xpath");
		click("logout_id");
		click("logoutalert_id");
	}
	
	public void verifyDeparture(String DepartureCity){
		
		click("flighttab_id");
		wait(5);
		test.log(LogStatus.INFO, "Verifying city "+DepartureCity);
		WebElement depCity=driver.findElement(By.xpath(prop.getProperty("departurecity_xpath")));
		System.out.println(depCity.getText());
		if(!depCity.getText().trim().equals(DepartureCity)){
			click("fromcity_id");
			type("departure_id",DepartureCity);
			test.log(LogStatus.INFO, "Click on "+ prop.getProperty("city_xpath"));
			click("city_xpath");
			takeScreenshot();
			test.log(LogStatus.INFO, "Clicked on "+ prop.getProperty("city_xpath"));
			takeScreenshot();
		}
		
	}
	
public void verifyArrival(String ArrivalCity){
		
		wait(5);
		test.log(LogStatus.INFO, "Verifying city "+ArrivalCity);
		WebElement arrCity=driver.findElement(By.xpath(prop.getProperty("arrivalcity_xpath")));
		System.out.println(arrCity.getText());
		if(!arrCity.getText().trim().equals(ArrivalCity)){
			click("tocity_id");
			type("destinationcity_id",ArrivalCity);
			test.log(LogStatus.INFO, "Click on "+ prop.getProperty("destCity_xpath"));
			click("destCity_xpath");
			takeScreenshot();
			test.log(LogStatus.INFO, "Clicked on "+ prop.getProperty("destCity_xpath"));
			takeScreenshot();
		}
		
	}

public enum DIRECTION {
	LEFT, RIGHT,UP,DOWN;
}

public void swipe(DIRECTION direction,MobileElement locatorKey) {

	// Get location of element you want to swipe
	MobileElement banner = locatorKey;
	Point bannerPoint = banner.getLocation();
	// Get size of device screen
	Dimension screenSize = driver.manage().window().getSize();
	// Get start and end coordinates for horizontal swipe
	int startX = Math.toIntExact(Math.round(screenSize.getWidth() * 0.8));
	int endX = 0;

	TouchAction action = new TouchAction(driver);
	action.press(PointOption.point(startX, bannerPoint.getY())).moveTo(PointOption.point(endX, bannerPoint.getY()))
			.release();
	driver.performTouchAction(action);
}
	
	public void selectDate(String d){
		test.log(LogStatus.INFO, "Selecting the date "+d);
		// convert the string date(input) in date object
		click("datetab_id");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		
		try {
			Date dateTobeSelected = sdf.parse(d);
			Date currentDate = new Date();
			sdf = new SimpleDateFormat("MMMM");
			String monthToBeSelected=sdf.format(dateTobeSelected);
			sdf = new SimpleDateFormat("yyyy");
			String yearToBeSelected=sdf.format(dateTobeSelected);
			sdf = new SimpleDateFormat("d");
			String dayToBeSelected=sdf.format(dateTobeSelected);
			//June 2016
			String monthYearToBeSelected=monthToBeSelected+" "+yearToBeSelected;
			
			//Get the size of screen.
			wait(3);
			
			size = driver.manage().window().getSize(); 
			System.out.println(size);
			wait(5);
			 
			//WebElement date=driver.findElement(By.xpath("//android.widget.TextView[@text='October 2017']"));
			
			//if(date.isDisplayed())
			  //{
				if(monthYearToBeSelected.equals(getText("monthyearselect_xpath"))){
					
					driver.findElement(By.xpath("//android.widget.CheckedTextView[@text='"+dayToBeSelected+"' and @index='3']")).click();
					test.log(LogStatus.INFO, "Date Selection Successful "+d);
					click("calendarOKbutton_id");
					/*  driver.findElement(By.xpath("//android.widget.CheckedTextView[@text='11' and @index='3']")).click();
					  wait(2);
					  driver.findElement(By.id("com.makemytrip:id/calOK")).click();
					  wait(2);*/
					}
				
			  //}
			  
			  else{
				  System.out.println("Date not found");
			  }
					
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
