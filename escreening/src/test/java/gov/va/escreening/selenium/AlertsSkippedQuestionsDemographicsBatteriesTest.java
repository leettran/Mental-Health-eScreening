package gov.va.escreening.selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class AlertsSkippedQuestionsDemographicsBatteriesTest extends SeleniumTest{

	@Test
	public void testAlertsSkippedQuestionsDemographicsBatteries() throws Exception {
		driver.get(baseUrl + "/home");
		driver.findElement(By.linkText("Staff Login")).click();
		driver.findElement(By.id("userNameParam")).clear();
		driver.findElement(By.id("userNameParam")).sendKeys("techadmin");
		driver.findElement(By.id("passwordParam")).clear();
		driver.findElement(By.id("passwordParam")).sendKeys("password");
		driver.findElement(By.id("dashboardLogin")).click();
		driver.findElement(By.id("createBatteryTab")).click();
		driver.findElement(By.id("ssnLastFour")).clear();
		driver.findElement(By.id("ssnLastFour")).sendKeys("1234");
		driver.findElement(By.id("searchButton")).click();
		driver.findElement(By.xpath("(//a[contains(text(),'Select')])[34]")).click();
		driver.findElement(By.id("createAssessmentButton")).click();
		new Select(driver.findElement(By.id("selectedProgramId"))).selectByVisibleText("OOO");
		new Select(driver.findElement(By.id("selectedClinicId"))).selectByVisibleText("MAMMOGRAM");
		new Select(driver.findElement(By.id("selectedNoteTitleId"))).selectByVisibleText("Adverse React/Allergy");
		new Select(driver.findElement(By.id("selectedClinicianId"))).selectByVisibleText("1pharmacist, One");
		driver.findElement(By.id("selectedBatteryId5")).click();
		driver.findElement(By.linkText("Clear all Checked Modules")).click();
		driver.findElement(By.id("selectedSurveyIdList9")).click();
		driver.findElement(By.id("selectedSurveyIdList37")).click();
		driver.findElement(By.id("selectedSurveyIdList38")).click();
		driver.findElement(By.id("selectedSurveyIdList50")).click();
		driver.findElement(By.id("saveButton")).click();
		navigateToVeteranLogin();
		driver.findElement(By.id("lastName")).clear();
		driver.findElement(By.id("lastName")).sendKeys("veteran48");
		driver.findElement(By.id("lastFourSsn")).clear();
		driver.findElement(By.id("lastFourSsn")).sendKeys("1234");
		driver.findElement(By.id("veteranLogin")).click();
		driver.findElement(By.id("startAssessmentButton")).click();
		driver.findElement(By.id("inp_161")).click();
		driver.findElement(By.id("inp_170")).clear();
		driver.findElement(By.id("inp_170")).sendKeys("11/15/1967");
		driver.findElement(By.id("inp_210")).clear();
		driver.findElement(By.id("inp_210")).sendKeys("142");
		new Select(driver.findElement(By.id("queILS_71"))).selectByVisibleText("5");
		new Select(driver.findElement(By.id("queILS_72"))).selectByVisibleText("6");
		driver.findElement(By.id("inp_221")).click();
		driver.findElement(By.cssSelector("div.checkSwitchHandle")).click();
		new Select(driver.findElement(By.id("queILS_727"))).selectByVisibleText("single");
		driver.findElement(By.id("inp_6270")).click();
		driver.findElement(By.id("nextBtn")).click();
		driver.findElement(By.id("tableQuestionAdd_90")).click();
		driver.findElement(By.id("inp0_910")).click();
		new Select(driver.findElement(By.id("queILS0_92"))).selectByVisibleText("Navy");
		driver.findElement(By.id("inp0_930")).click();
		driver.findElement(By.id("inp0_930")).clear();
		driver.findElement(By.id("inp0_930")).sendKeys("1987");
		driver.findElement(By.id("inp0_940")).clear();
		driver.findElement(By.id("inp0_940")).sendKeys("2007");
		new Select(driver.findElement(By.id("queILS0_96"))).selectByVisibleText("Honorable");
		new Select(driver.findElement(By.id("queILS0_97"))).selectByVisibleText("o5");
		driver.findElement(By.id("nextBtn")).click();
		driver.findElement(By.xpath("//button[@type='button']")).click();
		driver.findElement(By.xpath("//ul[@id='inpIL110']/li[7]/div/div/div/div[3]")).click();
		driver.findElement(By.xpath("//ul[@id='inpIL110']/li[5]/div/div/div/div[2]")).click();
		driver.findElement(By.id("nextBtn")).click();
		driver.findElement(By.id("inp_5223")).click();
		driver.findElement(By.id("inp_5234")).click();
		driver.findElement(By.id("inp_5243")).click();
		driver.findElement(By.id("inp_5264")).click();
		driver.findElement(By.id("inp_5273")).click();
		driver.findElement(By.id("inp_5283")).click();
		driver.findElement(By.id("inp_5292")).click();
		driver.findElement(By.id("inp_5303")).click();
		driver.findElement(By.id("inp_5313")).click();
		driver.findElement(By.id("inp_5332")).click();
		driver.findElement(By.id("inp_5343")).click();
		driver.findElement(By.id("inp_5354")).click();
		driver.findElement(By.id("inp_5354")).click();
		driver.findElement(By.id("inp_5364")).click();
		driver.findElement(By.id("inp_5383")).click();
		driver.findElement(By.id("nextBtn")).click();
		driver.findElement(By.xpath("//button[@type='button']")).click();
		driver.findElement(By.id("inp_5421")).click();
		driver.findElement(By.id("inp_5431")).click();
		driver.findElement(By.id("inp_5440")).click();
		driver.findElement(By.id("inp_5451")).click();
		driver.findElement(By.id("center")).click();
		driver.findElement(By.id("nextBtn")).click();
		driver.findElement(By.id("completedButton")).click();
		driver.findElement(By.cssSelector("input.answerButton.doneButton")).click();
		driver.findElement(By.cssSelector("img[alt=\"Department of Veterans Affairs | eScreening Program\"]")).click();
	}
}
