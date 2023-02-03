package com.quickwork.controller;

import com.quickwork.AbstractTest;
import com.quickwork.TestUtils;
import com.quickwork.model.County;
import com.quickwork.model.RegistrationRequest;
import com.quickwork.repository.AdDAO;
import com.quickwork.repository.CountyDAO;
import com.quickwork.repository.MessageDAO;
import com.quickwork.repository.ProfilePicDAO;
import com.quickwork.repository.ReviewDAO;
import com.quickwork.repository.UserDAO;
import com.quickwork.utilities.EntityObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest extends AbstractTest {

    @Autowired
    protected MockMvc mockMvc;

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
    }

    @Test
    void registerUser() throws Exception {
        RegistrationRequest request = new RegistrationRequest(TestUtils.DUMMY_USER1, "1234", "dummy@gmail.com", "1", "0986767676");
        String json = EntityObjectMapper.toJson(request);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/public/register").contentType(MediaType.APPLICATION_JSON).content(json);
        ResultActions actions = mockMvc.perform(requestBuilder);
        actions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @AfterEach
    public void cleanUp() {
        profilePicDAO.deleteAll();
        messageDAO.deleteAll();
        reviewDAO.deleteAll();
        adDAO.deleteAll();
        userDAO.deleteAll();
        countyDAO.deleteAll();
    }
}
