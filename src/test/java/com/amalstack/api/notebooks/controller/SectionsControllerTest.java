package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.SectionDto;
import com.amalstack.api.notebooks.dto.SectionSummaryDto;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Section;
import com.amalstack.api.notebooks.repository.*;
import com.amalstack.api.notebooks.security.ApplicationSecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.amalstack.api.notebooks.controller.AppResultMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SectionsController.class)
@Import(ApplicationSecurityConfiguration.class)
@AutoConfigureMockMvc
@WithMockUser(username = "SectionsControllerTest" + TestData.APP_USER_WITH_NOTEBOOKS_USERNAME)
class SectionsControllerTest {

    private static final String PATH = "/sections";

    private final TestData testData = new TestData("SectionsControllerTest", true);

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
        Section section = testData.getSection1WithPages();
        String expectedContent = objectMapper.writeValueAsString(new SectionSummaryDto(
                section.getId(),
                section.getName(),
                section.getNotebook().getId()));

        mockMvc.perform(get(PATH + "/{id}", section.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent));
    }

    @Test
    void get_whenIdIsNotValid_thenThenNotFound() throws Exception {
        Section section = testData.nonPersistent().section();

        mockMvc.perform(get(PATH + "/{id}", section.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(sectionIsNotFoundById(section.getId()));
    }

    @Test
    @WithMockUser(username = "SectionsControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void get_whenSectionIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        Section section = testData.getSection1WithPages();

        mockMvc.perform(get(PATH + "/{id}", section.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(section.getId(), "section"));
    }

    @Test
    void getByNotebookId_whenNotebookExists_thenOk() throws Exception {
        Notebook notebook = testData.getNotebookWithSections();
        var sections = testData.getSections();

        mockMvc.perform(get(PATH + "/notebook/{id}", notebook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.length()")
                                .value(sections.size()),
                        jsonPath("$[*].id")
                                .value(containsInAnyOrder(sections
                                        .stream()
                                        .map(Section::getId)
                                        .mapToInt(Long::intValue)
                                        .boxed().toArray(Integer[]::new))),
                        jsonPath("$[*].name")
                                .value(contains(sections
                                        .stream()
                                        .map(Section::getName)
                                        .toArray())),
                        jsonPath("$[*].notebookId")
                                .value(containsInAnyOrder(sections
                                        .stream()
                                        .map(s -> s.getNotebook().getId())
                                        .mapToInt(Long::intValue)
                                        .boxed().toArray(Integer[]::new)))
                );
    }

    @Test
    void getByNotebookId_whenNotebookDoesNotExist_thenNotFound() throws Exception {
        Notebook notebook = testData.nonPersistent().notebook();

        mockMvc.perform(get(PATH + "/notebook/{id}", notebook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(notebookIsNotFoundById(notebook.getId()));
    }

    @Test
    @WithMockUser(username = "SectionsControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void getByNotebookId_whenNotebookIsNotOwnedByUser_thenUnauthorized() throws Exception {
        Notebook notebook = testData.getNotebookWithSections();

        mockMvc.perform(get(PATH + "/notebook/{id}", notebook.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(notebook.getId(), "notebook"));
    }

    @Test
    void create_whenInputIsValid_thenOk() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SectionDto(testData.getNotebookWithSections().getId(),
                                        "New Section"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    void create_whenInputIsInvalid_thenBadRequest() throws Exception {
        long notebookId = testData.getNotebookWithSections().getId();
        SectionDto sectionDto = new SectionDto(notebookId, "");
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(validationFailed()
                        .withError("name", "must not be blank"));

    }

    @Test
    void create_whenNotebookDoesNotExist_thenNotFound() throws Exception {
        long notebookId = testData.nonPersistent().notebook().getId();
        SectionDto sectionDto = new SectionDto(notebookId, "Test Section");
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(notebookIsNotFoundById(notebookId));
    }

    @Test
    @WithMockUser(username = "SectionsControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void create_whenNotebookIsNotOwnedByUser_thenUnauthorized() throws Exception {
        long notebookId = testData.getNotebookWithSections().getId();
        SectionDto sectionDto = new SectionDto(notebookId, "Test Section");

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sectionDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(notebookId, "notebook"));
    }

    @Test
    void update_whenInputIsInvalid_thenBadRequest() throws Exception {
        Section section = testData.getSection1WithPages();
        mockMvc.perform(put(PATH + "/{id}",
                        section.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SectionDto(section.getNotebook().getId(),
                                        ""     // Blank name
                                ))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(validationFailed()
                        .withError("name", "must not be blank"));
    }

    @Test
    void update_whenParentNotebookDoesNotExist_thenNotFound() throws Exception {
        var nonExistentNotebookId = testData.nonPersistent().notebook().getId();
        var existingSectionId = testData.getSection1WithPages().getId();

        mockMvc.perform(put(PATH + "/{id}", existingSectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SectionDto(nonExistentNotebookId,
                                "Updated Section Name"))
                        ))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(notebookIsNotFoundById(nonExistentNotebookId));
    }

    @Test
    @WithMockUser(username = "SectionsControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void update_whenParentNotebookIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        long unownedNotebookId = testData.getNotebookWithSections().getId();
        long existingSectionId = testData.getSection1WithPages().getId();

        mockMvc.perform(put(PATH + "/{id}", existingSectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SectionDto(unownedNotebookId,
                                        "Updated Description"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(unownedNotebookId, "notebook"));
    }

    @Test
    void update_whenSectionIsExistingAndIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        // Tries to add someone else's section to a notebook owned by the current user
        long unownedSectionId = testData.unowned().section().getId();
        long ownedNotebookId = testData.getNotebookWithSections().getId();

        mockMvc.perform(put(PATH + "/{id}", unownedSectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SectionDto(ownedNotebookId,
                                        "Updated Description"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(unownedSectionId, "section"));
    }

    @Test
    void update_whenInputIsValidAndSectionIsExisting_thenOk() throws Exception {
        var existingSection = testData.getSection1WithPages();
        var newNotebookId = testData.getNotebookWithoutSections().getId();

        mockMvc.perform(put(PATH + "/{id}", existingSection.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SectionDto(newNotebookId, // Move to another notebook
                                        "Updated Name")
                        )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(existingSection.getId()),
                        jsonPath("$.name").value("Updated Name"),
                        jsonPath("$.notebookId").value(newNotebookId)
                );
    }

    @Test
    void update_whenInputIsValidAndSectionIsNonExisting_thenOk() throws Exception {
        var existingNotebookId = testData.getNotebookWithSections().getId();
        var newSectionId = 42;

        mockMvc.perform(put(PATH + "/{id}", newSectionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new SectionDto(existingNotebookId,
                                        "New Section"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(newSectionId),
                        jsonPath("$.name").value("New Section"),
                        jsonPath("$.notebookId").value(existingNotebookId)
                );
    }


    @Test
    void delete_whenSectionExistsAndIsOwnedByCurrentUser_thenOk() throws Exception {
        mockMvc.perform(delete(PATH + "/{id}",
                        testData.getSection1WithPages().getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void delete_whenSectionDoesNotExist_thenNotFound() throws Exception {
        long nonExistentSectionId = testData.nonPersistent().section().getId();

        mockMvc.perform(delete(PATH + "/{id}",
                        nonExistentSectionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(sectionIsNotFoundById(nonExistentSectionId));
    }

    @Test
    @WithMockUser(username = "SectionsControllerTest" + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void delete_whenSectionIsNotOwnedByCurrentUser_thenUnauthorized() throws Exception {
        long unownedSectionId = testData.getSection1WithPages().getId();

        mockMvc.perform(delete(PATH + "/{id}",
                        unownedSectionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(unownedSectionId, "section"));
    }
}