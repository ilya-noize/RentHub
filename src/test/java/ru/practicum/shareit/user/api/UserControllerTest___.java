package ru.practicum.shareit.user.api;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserMapper;
import ru.practicum.shareit.user.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @see UserController
 */
@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTestForRevision {
    private static MockHttpServletRequestBuilder requestBuilder;
    @Autowired
    private MockMvc mockMvc;

    //    @Mock
    @MockBean
    UserService service;

    @MockBean
    UserMapper mapper;

    @Test
    void getAll() throws Exception {
        User user = new User(1, "user1@user.com", "user1");
        List<User> users = List.of(
                new User(1, "user1@user.com", "user1"),
                new User(2, "user2@user.com", "user2"),
                new User(3, "user3@user.com", "user3")
        );

        List<User> AllUsers = Arrays.asList(user);

        List<UserDto> AllUsersDto = AllUsers.stream().map(mapper::toDto).collect(Collectors.toList());

//        given(service.getAll()).willReturn(AllUsersDto);

        mockMvc.perform(request(GET, "/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect((ResultMatcher) jsonPath("$[0].name", is(user.getName())));

//        mockMvc.perform(request(GET, "/users"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(containsString("[]")));
    }

    @Test
    void get() throws Exception {
        mockMvc.perform(request(GET, "/users/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User with id:(1) not exist")));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(request(DELETE, "/user/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User with id:(1) not exist")));
    }

    @Test
    void create() {
//        User user = new User(1, "user@user.com", "user");
//        UserDto userDto = new UserDto(1, "user@user.com", "user");

//        MultyValueMap<String, String> params = MultiValueMap.
//                "email", "user@user.com",
//                "name", "user");
//
//          ServletContext context
//        requestBuilder = post("/users").buildRequest()
//
//        mockMvc.perform(requestBuilder)
//                .andDo(print())
//                .andExpect(status().isOk());
    }
}