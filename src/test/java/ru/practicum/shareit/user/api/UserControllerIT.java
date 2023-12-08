package ru.practicum.shareit.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.api.dto.UserDto;
import ru.practicum.shareit.user.api.dto.UserSimpleDto;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.user.api.UserController.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerIT {
    private final UserSimpleDto userRequestNew = new UserSimpleDto("user@user.com", "user");
    private final UserDto userRequestPatch = new UserDto(1, "userUpdate@user.com", "userUpdate");
    private final UserDto userResponse = new UserDto(1, "user@user.com", "user");
    private final List<UserDto> userResponseList = List.of(
            new UserDto(2, "root@user.com", "root"),
            new UserDto(3, "user@user.com", "user"));
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserServiceImpl userService;

    @SneakyThrows
    @Test
    void create() {

        when(userService.create(userRequestNew))
                .thenReturn(userResponse);

        mvc.perform(post(CREATE_USER)
                        .content(mapper.writeValueAsString(userRequestNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()))
                .andExpect(status().isOk());

        verify(userService, times(1)).create(any(UserSimpleDto.class));
    }

    @Test
    void update() throws Exception {
        userResponse.setEmail("userUpdate@user.com");
        userResponse.setName("userUpdate");
        int userId = userRequestPatch.getId();

        when(userService.update(userRequestPatch))
                .thenReturn(userResponse);

        mvc.perform(patch(UPDATE_USER, userId)
                        .content(mapper.writeValueAsString(userRequestPatch))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()))
                .andExpect(status().isOk());

        verify(userService, times(1)).update(userRequestPatch);
    }

    @Test
    void get() throws Exception {
        int userId = userRequestPatch.getId();
        when(userService.get(1))
                .thenReturn(userResponse);
        mvc.perform(MockMvcRequestBuilders.get(GET_USER, userId))
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        when(userService.getAll())
                .thenReturn(userResponseList);
        mvc.perform(MockMvcRequestBuilders.get(GET_ALL_USERS))
                .andExpect(jsonPath("$[0].id").value(userResponseList.get(0).getId()))
                .andExpect(jsonPath("$[0].email").value(userResponseList.get(0).getEmail()))
                .andExpect(jsonPath("$[0].name").value(userResponseList.get(0).getName()))
                .andExpect(jsonPath("$[1].id").value(userResponseList.get(1).getId()))
                .andExpect(jsonPath("$[1].email").value(userResponseList.get(1).getEmail()))
                .andExpect(jsonPath("$[1].name").value(userResponseList.get(1).getName()))
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        int userId = userRequestPatch.getId();
        doNothing().when(userService).delete(anyInt());

        mvc.perform(MockMvcRequestBuilders.delete(DELETE_USER, userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(userId);
    }
}