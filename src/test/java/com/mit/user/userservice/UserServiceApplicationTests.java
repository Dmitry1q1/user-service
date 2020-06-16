package com.mit.user.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserServiceApplicationTests {

	private MockMvc mockMvc;

//	@Test
//	public void indexControllerShouldReturnHtmlPage() throws Exception {
//		mockMvc.perform(get("/"))
//				.andExpect(status().isOk())
//				.andExpect(content().string(containsString("Welcome to Spring")));
//	}

}
