package com.quickwork.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickwork.AbstractTest;
import com.quickwork.TestUtils;
import com.quickwork.dtos.AdResponse;
import com.quickwork.dtos.MessageRequest;
import com.quickwork.dtos.ReviewRequest;
import com.quickwork.model.Ad;
import com.quickwork.model.County;
import com.quickwork.model.User;
import com.quickwork.repository.AdDAO;
import com.quickwork.repository.CountyDAO;
import com.quickwork.repository.MessageDAO;
import com.quickwork.repository.ProfilePicDAO;
import com.quickwork.repository.ReviewDAO;
import com.quickwork.repository.UserDAO;
import com.quickwork.service.UserService;
import com.quickwork.utilities.EntityObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends AbstractTest {

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CountyDAO countyDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AdDAO adDAO;

    @Autowired
    private ReviewDAO reviewDAO;

    @Autowired
    private MessageDAO messageDAO;

    @Autowired
    private ProfilePicDAO profilePicDAO;

    @BeforeEach
    public void prepare() {
        profilePicDAO.deleteAll();
        messageDAO.deleteAll();
        reviewDAO.deleteAll();
        adDAO.deleteAll();
        userDAO.deleteAll();
        countyDAO.deleteAll();

        County county = new County(1L, "1", "Zagrebacka");
        countyDAO.save(county);
        List<County> countyList = countyDAO.findAll();

        User user = TestUtils.createUser(TestUtils.DUMMY_USER1);
        User user2 = TestUtils.createUser(TestUtils.DUMMY_USER2);
        userDAO.save(user);
        userDAO.save(user2);
        Ad ad = TestUtils.createAd(user, countyList.get(0));
        adDAO.save(ad);
    }


    @Test
    public void getCountiesTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/public/counties").contentType(MediaType.APPLICATION_JSON);
        ResultActions actions = mockMvc.perform(requestBuilder);
        actions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAdsTest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/public/ads").contentType(MediaType.APPLICATION_JSON);
        ResultActions actions = mockMvc.perform(requestBuilder);
        actions.andExpect(MockMvcResultMatchers.status().isOk());
        List<AdResponse> response = mapper.readValue(actions.andReturn().getResponse().getContentAsString(), new TypeReference<>(){});
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isEmpty());
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("test ad", response.get(0).getContent());
    }

    @Test
    public void insertReview() throws Exception {
        ReviewRequest request = new ReviewRequest(1L, "title", "content", 4, "user1", "user2");

        String json = EntityObjectMapper.toJson(request);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/public/review").contentType(MediaType.APPLICATION_JSON).content(json);
        ResultActions actions = mockMvc.perform(requestBuilder);
        actions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        actions.andExpect(MockMvcResultMatchers.content().string("Review inserted successfully!"));
    }

    @Test
    public void insertMessage() throws Exception {
        MessageRequest request = new MessageRequest("msgContentMock", "user1", "user2", 3L);

        String json = EntityObjectMapper.toJson(request);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/public/message").contentType(MediaType.APPLICATION_JSON).content(json);
        ResultActions actions = mockMvc.perform(requestBuilder);
        actions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        actions.andExpect(MockMvcResultMatchers.content().string("Message inserted successfully!"));
    }

}
