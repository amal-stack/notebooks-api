package com.amalstack.api.notebooks;

import com.amalstack.api.notebooks.model.AppUser;
import com.amalstack.api.notebooks.model.Notebook;
import com.amalstack.api.notebooks.model.Page;
import com.amalstack.api.notebooks.model.Section;
import com.amalstack.api.notebooks.repository.AppUserRepository;
import com.amalstack.api.notebooks.repository.NotebookRepository;
import com.amalstack.api.notebooks.repository.PageRepository;
import com.amalstack.api.notebooks.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class NotebooksApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotebooksApiApplication.class, args);
    }

}

@Component
final class DataSeeder implements CommandLineRunner {

    @Autowired
    private NotebookRepository notebookRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<AppUser> users = new ArrayList<>(5);
        List<Notebook> notebooks = new ArrayList<>(15);
        List<Section> sections = new ArrayList<>();
        List<Page> pages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var user = new AppUser("user_" + (i + 1), "user_" + (i + 1), passwordEncoder.encode("pwd"));
            users.add(user);
            for (int j = 0; j < 3; j++) {
                var notebook = new Notebook("Notebook U" + (i + 1) + ".N" + (j + 1), "Description", LocalDateTime.now(), user);
                notebooks.add(notebook);
                for (int k = 0; k < 5; k++) {
                    var section = new Section("Section U" + (i + 1) + ".N" + (j + 1) + ".S" + (k + 1), notebook);
                    sections.add(section);
                    for (int m = 0; m < 5; m++) {
                        var page = new Page("Page " + (i + 1) + ".N" + (j + 1) + ".S" + (k + 1) + ".P" + (m + 1), "*Content* of **page**" + (i + 1) + ".N" + (j + 1) + ".S" + (k + 1) + ".P" + (m + 1), section);
                        pages.add(page);
                    }
                }
            }
            appUserRepository.saveAll(users);
            notebookRepository.saveAll(notebooks);
            sectionRepository.saveAll(sections);
            pageRepository.saveAll(pages);
        }


    }
}

