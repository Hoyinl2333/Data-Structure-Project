package com.codex.taxitrajectory;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaxiTrajectoryApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;

	@BeforeEach
	public void setUp() {
		// 自动管理 ChromeDriver
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		// 无头模式，不显示浏览器窗口
		options.addArguments("--headless");
		driver = new ChromeDriver(options);
		// 设置隐式等待时间
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	@AfterEach
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void testTaxiTrajectoryPage() {
		// 构建测试页面的 URL
		String baseUrl = "http://localhost:" + port + "/taxi-trajectory.html";
		driver.get(baseUrl);

		// 查找输入框并输入出租车 ID
		WebElement taxiIdInput = driver.findElement(By.id("taxiId"));
		taxiIdInput.sendKeys("1234");

		// 查找查询按钮并点击
		WebElement searchButton = driver.findElement(By.cssSelector("button"));
		searchButton.click();

		// 等待一段时间，确保页面加载完成
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 检查结果区域是否有内容
		WebElement resultDiv = driver.findElement(By.id("result"));
		assertTrue(resultDiv.getText().length() > 0);
	}
}

