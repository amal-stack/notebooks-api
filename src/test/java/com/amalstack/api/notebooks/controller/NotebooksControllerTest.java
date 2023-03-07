package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.NotebookDto;
import com.amalstack.api.notebooks.dto.NotebookInfoDto;
import com.amalstack.api.notebooks.dto.PageInfoDto;
import com.amalstack.api.notebooks.dto.SectionInfoDto;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;
import com.amalstack.api.notebooks.repository.*;
import com.amalstack.api.notebooks.security.ApplicationSecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotebooksController.class)
@Import(ApplicationSecurityConfiguration.class)
@AutoConfigureMockMvc
@WithMockUser(username = "NotebooksControllerTest" + TestData.APP_USER_WITH_NOTEBOOKS_USERNAME)
class NotebooksControllerTest {

    private static final String PATH = "/notebooks";

    private final TestData testData = new TestData("NotebooksControllerTest", true);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotebookRepository notebookRepository;

    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private PageRepository pageRepository;

    @MockBean
    private AppUserRepository appUserRepository;

    @BeforeEach
    void setUp() {
        testData.initMocks(appUserRepository,
                notebookRepository,
                sectionRepository,
                pageRepository);
    }

    @Test
    void get_whenIdIsValid_thenOk() throws Exception {
        List<Map.Entry<Section, List<Page>>> sectionAndPages = List.of(
                Map.entry(testData.getSection1WithPages(), testData.getSection1Pages()),
                Map.entry(testData.getSection2WithPages(), testData.getSection2Pages()),
                Map.entry(testData.getSectionWithoutPages(), Collections.emptyList())
        );

        var notebookInfoDto = NotebookInfoDto.fromNotebook(
                testData.getNotebookWithSections(), sectionAndPages.stream()
                        .map(e -> SectionInfoDto.fromSection(e.getKey(),
                                e.getValue().stream()
                                        .map(PageInfoDto::fromPage)
                                        .toList()))
                        .toList());

        String expectedContent = objectMapper.writeValueAsString(notebookInfoDto);

        mockMvc.perform(get(PATH + "/{id}",
                        testData.getNotebookWithSections().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent));
    }

    @Test
    void get_whenIdIsInvalid_thenNotFound() throws Exception {
        mockMvc.perform(get(PATH + "/{id}",
                        testData.nonPersistent().notebook().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.notebookIsNotFoundById(
                        testData.nonPersistent().notebook().getId()
                ));
    }

    @Test
    @WithMockUser(username = "NotebooksControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void get_whenNotebookIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/{id}",
                        testData.getNotebookWithSections().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.resourceIsNotOwned(
                        testData.getNotebookWithSections().getId(), "notebook"));
    }

    @Test
    void getByUser_whenAuthorized_thenOk() throws Exception {
        mockMvc.perform(get(PATH + "/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.length()").value(2),
                        jsonPath("$[*].id").value(Matchers.containsInAnyOrder(1, 2)),
                        jsonPath("$[*].sectionCount").value(Matchers.containsInAnyOrder(3, 0)),
                        jsonPath("$[*].pageCount").value(Matchers.containsInAnyOrder(3, 0)),
                        jsonPath("$[*].username").value(Matchers.containsInAnyOrder(
                                testData.getAppUserWithNotebooks().getUsername(),
                                testData.getAppUserWithNotebooks().getUsername())));
    }

    @Test
    void create_whenInputIsValid_thenCreated() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new NotebookDto("Test Notebook",
                                        "Test Description"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    void create_whenInputIsInvalid_thenBadRequest() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new NotebookDto("", // Blank name
                                        "Test Description"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.validationFailed()
                        .withMessage("Validation failed")
                        .withError("name", "must not be blank"));
    }

    @Test
    void update_whenInputIsValidAndExisting_thenOk() throws Exception {
        var existingNotebookId = testData.getNotebookWithSections().getId();

        mockMvc.perform(put(PATH + "/{id}",
                        existingNotebookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new NotebookDto("Updated Name",
                                        "Updated Description"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(existingNotebookId),
                        jsonPath("$.name").value("Updated Name"),
                        jsonPath("$.description").value("Updated Description"),
                        jsonPath("$.userId").value(testData.getAppUserWithNotebooks().getId()));
    }

    @Test
    void update_whenInputIsValidAndNonExisting_thenOk() throws Exception {
        long newNotebookId = 42;
        mockMvc.perform(put(PATH + "/{id}",
                        newNotebookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new NotebookDto("New Notebook",
                                        "New Description"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(newNotebookId),
                        jsonPath("$.name").value("New Notebook"),
                        jsonPath("$.description").value("New Description"));
    }

    @Test
    void update_whenInputIsInvalid_thenBadRequest() throws Exception {
        mockMvc.perform(put(PATH + "/{id}",
                        testData.getNotebookWithSections().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new NotebookDto("", // Blank name
                                        "Test Description"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.validationFailed()
                        .withError("name", "must not be blank"));
    }

    @Test
    @WithMockUser(username = "NotebooksControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void update_whenNotebookIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        long unownedNotebookId = testData.getNotebookWithSections().getId();

        mockMvc.perform(put(PATH + "/{id}",
                        unownedNotebookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new NotebookDto("Updated Name",
                                        "Updated Description"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.resourceIsNotOwned(
                        unownedNotebookId, "notebook"));
    }

    @Test
    void delete_whenNotebookExistsAndIsOwnedByCurrentUser_thenOk() throws Exception {
        mockMvc.perform(delete(PATH + "/{id}",
                        testData.getNotebookWithSections().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void delete_whenNotebookDoesNotExist_thenNotFound() throws Exception {
        var nonPersistentNotebookId = testData.nonPersistent().notebook().getId();

        mockMvc.perform(delete(PATH + "/{id}",
                        nonPersistentNotebookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.notebookIsNotFoundById(nonPersistentNotebookId));
    }

    @Test
    @WithMockUser(username = "NotebooksControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void delete_whenNotebookIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        long unownedNotebookId = testData.getNotebookWithSections().getId();

        mockMvc.perform(delete(PATH + "/{id}",
                        unownedNotebookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(AppResultMatchers.resourceIsNotOwned(
                        unownedNotebookId, "notebook"));
    }
}

