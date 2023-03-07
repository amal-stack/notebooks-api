package com.amalstack.api.notebooks.controller;

import com.amalstack.api.notebooks.dto.PageDto;
import com.amalstack.api.notebooks.dto.PageInfoDto;
import com.amalstack.api.notebooks.model.Page;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagesController.class)
@Import(ApplicationSecurityConfiguration.class)
@AutoConfigureMockMvc
@WithMockUser(username = "PagesControllerTest" + TestData.APP_USER_WITH_NOTEBOOKS_USERNAME)
class PagesControllerTest {

    static final String USERNAME_PREFIX = "PagesControllerTest";

    private static final String PATH = "/pages";

    private final TestData testData = new TestData(USERNAME_PREFIX, true);

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
        Page page = testData.getPage1OfSection1();
        String expectedContent = objectMapper.writeValueAsString(new PageInfoDto(page.getId(),
                page.getTitle(),
                page.getContent(),
                page.getSection().getId()));

        mockMvc.perform(get(PATH + "/{id}", page.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedContent));
    }

    @Test
    void get_whenIdIsNotValid_thenNotFound() throws Exception {
        long pageId = testData.nonPersistent().page().getId();

        mockMvc.perform(get(PATH + "/{id}", pageId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(pageIsNotFoundById(pageId));
    }

    @Test
    @WithMockUser(USERNAME_PREFIX + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void get_whenPageIsNotOwnedByUser_thenUnauthorized() throws Exception {
        long pageId = testData.getPage1OfSection1().getId();

        mockMvc.perform(get(PATH + "/{id}", pageId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(pageId, "page"));
    }

    @Test
    void getBySection_whenSectionExists_thenOk() throws Exception {
        long sectionId = testData.getSection1WithPages().getId();
        var pages = testData.getSection1Pages();
        mockMvc.perform(get(PATH + "/section/{id}", sectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.length()").value(pages.size()),
                        jsonPath("$[*].id")
                                .value(containsInAnyOrder(pages.stream()
                                        .map(Page::getId)
                                        .mapToInt(Long::intValue)
                                        .boxed()
                                        .toArray(Integer[]::new))),
                        jsonPath("[*].title")
                                .value(containsInAnyOrder(pages.stream()
                                        .map(Page::getTitle)
                                        .toArray())),
                        jsonPath("[*].content")
                                .value(containsInAnyOrder(pages.stream()
                                        .map(Page::getContent)
                                        .toArray())),
                        jsonPath("[*].sectionId")
                                .value(containsInAnyOrder(pages.stream()
                                        .map(p -> p.getSection().getId())
                                        .mapToInt(Long::intValue)
                                        .boxed()
                                        .toArray(Integer[]::new))));
    }

    @Test
    void getBySection_whenSectionDoesNotExist_thenNotFound() throws Exception {
        long nonExistentSectionId = testData.nonPersistent().section().getId();

        mockMvc.perform(get(PATH + "/section/{id}", nonExistentSectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(sectionIsNotFoundById(nonExistentSectionId));
    }

    @Test
    @WithMockUser(USERNAME_PREFIX + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void getBySection_whenSectionIsNotOwnedByUser_thenUnauthorized() throws Exception {
        long sectionId = testData.getSection1WithPages().getId();

        mockMvc.perform(get(PATH + "/section/{id}", sectionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(sectionId, "section"));
    }

    @Test
    void create_whenInputIsValidAndSectionExists_thenCreated() throws Exception {
        long sectionId = testData.getSection1WithPages().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(sectionId,
                        "Test Title",
                        "Test Content"
                ));

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());
    }

    @Test
    void create_whenInputIsInvalid_thenBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(
                new PageDto(testData.getSection1WithPages().getId(),
                        "", // Blank title
                        ""
                ));

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(validationFailed()
                        .withError("title", "must not be blank"));
    }

    @Test
    void create_whenSectionDoesNotExist_thenNotFound() throws Exception {
        long nonExistentSectionId = testData.nonPersistent().section().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(nonExistentSectionId,
                        "Test Title",
                        "Test Content"
                ));
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(sectionIsNotFoundById(nonExistentSectionId));
    }

    @Test
    @WithMockUser(USERNAME_PREFIX + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void create_whenSectionIsNotOwnedByUser_thenUnauthorized() throws Exception {
        long sectionId = testData.getSection1WithPages().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(sectionId,
                        "Test Title",
                        "Test Content"
                ));

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(sectionId, "section"));

    }

    @Test
    void update_whenInputIsInvalid_thenBadRequest() throws Exception {
        var sectionId = testData.getSection1WithPages().getId();
        var pageId = testData.getPage1OfSection1().getId();

        String body = objectMapper.writeValueAsString(
                new PageDto(sectionId,
                        "",     // Blank title
                        ""
                ));

        mockMvc.perform(put(PATH + "/{id}", pageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(validationFailed()
                        .withError("title", "must not be blank"));
    }

    @Test
    void update_whenParentSectionDoesNotExist_thenNotFound() throws Exception {
        long nonExistentSectionId = testData.nonPersistent().section().getId();
        long existingPageId = testData.getPage1OfSection1().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(nonExistentSectionId,
                        "Test Title",
                        "Test Content"
                ));

        mockMvc.perform(put(PATH + "/{id}", existingPageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(sectionIsNotFoundById(nonExistentSectionId));
    }

    @Test
    @WithMockUser(USERNAME_PREFIX + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void update_whenParentSectionIsNotOwnedByUser_thenUnauthorized() throws Exception {
        long sectionId = testData.getSection1WithPages().getId();
        long existingPageId = testData.getPage1OfSection1().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(sectionId,
                        "Test Title",
                        "Test Content"
                ));

        mockMvc.perform(put(PATH + "/{id}",
                        existingPageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(sectionId, "section"));
    }

    @Test
    void update_whenPageIsExistingAndNotOwnedByUser_thenUnauthorized() throws Exception {
        // Tries to add someone else's page to a section owned by current user
        long unownedPageId = testData.unowned().page().getId();
        long ownedSectionId = testData.getSection1WithPages().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(ownedSectionId,
                        "Updated Title",
                        "Updated Content"
                ));

        mockMvc.perform(put(PATH + "/{id}", unownedPageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(unownedPageId, "page"));
    }

    @Test
    void update_whenPageIsExistingAndOwnedByUser_thenOk() throws Exception {
        Page existingPage = testData.getPage1OfSection1();
        var newSectionId = testData.getSectionWithoutPages().getId();
        String body = objectMapper.writeValueAsString(
                new PageDto(newSectionId, // Move to another section
                        "Test Title",
                        "Test Content"
                ));

        mockMvc.perform(put(PATH + "/{id}", existingPage.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(existingPage.getId()),
                        jsonPath("$.title").value("Test Title"),
                        jsonPath("$.content").value("Test Content"),
                        jsonPath("$.sectionId").value(newSectionId)
                );

        assertThat(existingPage.getSection())
                .isEqualTo(testData.getSectionWithoutPages());
    }

    @Test
    void update_whenPageIsNonExisting_thenOk() throws Exception {
        var existingSectionId = testData.getSection1WithPages().getId();
        long newPageId = 42;
        String body = objectMapper.writeValueAsString(
                new PageDto(existingSectionId,
                        "New Title",
                        "New Content"
                ));

        mockMvc.perform(put(PATH + "/{id}", newPageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(newPageId),
                        jsonPath("$.title").value("New Title"),
                        jsonPath("$.content").value("New Content"),
                        jsonPath("$.sectionId").value(existingSectionId)
                );
    }

    @Test
    void delete_whenPageDoesNotExist_thenNotFound() throws Exception {
        long nonExistentPageId = testData.nonPersistent().page().getId();
        mockMvc.perform(delete(PATH + "/{id}", nonExistentPageId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(pageIsNotFoundById(nonExistentPageId));
    }

    @Test
    @WithMockUser(USERNAME_PREFIX + TestData.APP_USER_WITHOUT_NOTEBOOKS_USERNAME)
    void delete_whenPageIsNotOwnedByUser_thenUnauthorized() throws Exception {
        long unownedPageId = testData.getPage1OfSection1().getId();

        mockMvc.perform(delete(PATH + "/{id}", unownedPageId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(resourceIsNotOwned(unownedPageId, "page"));
    }

    @Test
    void delete_whenPageIsExistingAndOwnedByUser_thenOk() throws Exception {
        long existingPageId = testData.getPage1OfSection1().getId();

        mockMvc.perform(delete(PATH + "/{id}", existingPageId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}