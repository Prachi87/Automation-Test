package com.selenium.datadriven.test;

import java.net.MalformedURLException;
import java.util.Hashtable;

import org.openqa.selenium.By;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


import com.relevantcodes.extentreports.LogStatus;
import com.selenium.datadriven.base.BaseTest;
import com.selenium.datadriven.util.DataUtil;
import com.selenium.datadriven.util.Xls_Reader;


public class LoginTest extends BaseTest {
	String testCaseName="LoginTest";
	SoftAssert softAssert;
	Xls_Reader xls;
	String runmodes[]=null;
	static boolean fail=false;
	static boolean skip=false;
	static boolean isTestPass=true;
	static int count=-1;

	@Test(dataProvider="getData")
	public void doLogin(Hashtable<String,String> data) throws MalformedURLException{
		
		test= rep.startTest("LoginTest");
		test.log(LogStatus.INFO, data.toString());
		
		if(!DataUtil.isRunnable(testCaseName, xls) || data.get("Runmode").equalsIgnoreCase("N")){
			test.log(LogStatus.SKIP, "Skipping the test case as runmode is No");
			throw new SkipException("Skipping the test case as runmode is No");
		}
		
		launchapp();
		
		boolean actualResult=doLogin(data.get("Username"),data.get("Password"));
		boolean expectedResult=false;
			            
		if(data.get("ExpectedResult").equals("Successful Login")){
			test.log(LogStatus.INFO, "Success");
			expectedResult=true;
			takeScreenshot();
			wait(5);
		}
		
		else if(data.get("ExpectedResult").equals("Please enter a valid email id-Error")){
			
			if(verifyText("emailerror_name","Please enter a valid Email Id")){
				System.out.println("Please enter a valid Email Id");
			test.log(LogStatus.INFO, "Error recieved");
			 takeScreenshot();
			 wait(5);
			}
			
			else if(data.get("ExpectedResult").equals("Please Enter Password-Error")){
				if(verifyText("passworderror_name","Please enter Password")){
				System.out.println("Please enter Password");
				test.log(LogStatus.INFO, "Error recieved");
				takeScreenshot();
				 wait(5);
			}
			}
			else{
				data.get("ExpectedResult").equals("Error");
				System.out.println("Error recieved");
				test.log(LogStatus.INFO, "Error recieved");
				takeScreenshot();
				 wait(5);
			}
		}
		
		else{
			expectedResult=false;
		}
		
		if(expectedResult!=actualResult){
			System.out.println("Test Failed");
			reportFailure("Test Failed");
			reportPass("Login Test Passed");
		}
	}
	
	@BeforeMethod
	public void init(){
		softAssert=new SoftAssert();
		
	}
	
	@AfterMethod
	public void quit(){
		try{
			softAssert.assertAll();
		}catch(Error e){
			
			test.log(LogStatus.FAIL, e.getMessage());
		}
		
	}
	
	
	@DataProvider
	public Object[][] getData(){
		super.init();
		xls=new Xls_Reader(prop.getProperty("xlspath"));
		return DataUtil.getTestData(xls, testCaseName);
		//return TestUtil.getData(suite_shop_xls, this.getClass().getSimpleName()) ;
	}
}
