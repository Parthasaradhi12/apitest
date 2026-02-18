package com.example.apitest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ApitestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApitestApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {

        return args -> {

            RestTemplate restTemplate = new RestTemplate();
            String url1 =
              "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> body = new HashMap<>();
            body.put("name", "John Doe");
            body.put("regNo", "REG12347");
            body.put("email", "john@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url1, request, Map.class);

            System.out.println("First API Response:");
            System.out.println(response.getBody());
            Map<String, Object> res = response.getBody();

            String webhookUrl = res.get("webhook").toString();
            String token = res.get("accessToken").toString();

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + token);
       
            String finalQuery =
            		"SELECT d.DEPARTMENT_NAME, " +
            		"MAX(emp_total.total_salary) AS SALARY, " +
            		"CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, " +
            		"TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE " +
            		"FROM DEPARTMENT d " +
            		"JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
            		"JOIN ( " +
            		" SELECT p.EMP_ID, SUM(p.AMOUNT) AS total_salary " +
            		" FROM PAYMENTS p " +
            		" WHERE DAY(p.PAYMENT_TIME) <> 1 " +
            		" GROUP BY p.EMP_ID " +
            		") emp_total ON e.EMP_ID = emp_total.EMP_ID " +
            		"GROUP BY d.DEPARTMENT_ID " +
            		"HAVING emp_total.total_salary = MAX(emp_total.total_salary);";

            HttpHeaders headers2 = new HttpHeaders();
            headers2.setContentType(MediaType.APPLICATION_JSON);
            headers2.set("Authorization", token);

            Map<String, String> finalBody = new HashMap<>();
            finalBody.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> finalRequest =
                    new HttpEntity<>(finalBody, headers2);

            ResponseEntity<String> finalResponse =
                    restTemplate.postForEntity(
                            webhookUrl,
                            finalRequest,
                            String.class
                    );

            System.out.println("Final Response:");
            System.out.println(finalResponse.getBody());
        };
    }
}
