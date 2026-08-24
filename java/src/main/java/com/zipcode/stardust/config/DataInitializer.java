package com.zipcode.stardust.config;

import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.repository.SubforumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private SubforumRepository subforumRepository;
    
        @Override
        public void run(ApplicationArguments args) {

        if (subforumRepository.count() == 0) {

                Subforum build = new Subforum(
                        "Build & Make",
                        "Woodworking, fabrication, building projects, and making things from scratch.",
                        "🔨",
                        null
                );
                subforumRepository.save(build);

                Subforum home = new Subforum(
                        "Home Improvement",
                        "Repairs, remodeling, electrical, plumbing, and improving your space.",
                        "🏠",
                        null
                );
                subforumRepository.save(home);

                Subforum tech = new Subforum(
                        "Tech & Coding",
                        "Programming, AI, computers, electronics, and building with technology.",
                        "💻",
                        null
                );
                subforumRepository.save(tech);

                Subforum creative = new Subforum(
                        "Creative Projects",
                        "Photography, video, art, design, and hands-on creative projects.",
                        "🎨",
                        null
                );
                subforumRepository.save(creative);

                Subforum auto = new Subforum(
                        "Auto & Mechanics",
                        "Vehicle repairs, maintenance, modifications, restoration, and mechanical projects.",
                        "🚗",
                        null
                );
                subforumRepository.save(auto);

                Subforum outdoors = new Subforum(
                        "Outdoors & Fishing",
                        "Fishing, camping, hiking, outdoor projects, and getting outside.",
                        "🎣",
                        null
                );
                subforumRepository.save(outdoors);

                Subforum tools = new Subforum(
                        "Tools & Gear",
                        "Tools, equipment, workshop setups, gear reviews, and recommendations.",
                        "🧰",
                        null
                );
                subforumRepository.save(tools);

                Subforum repair = new Subforum(
                        "Repair & Reuse",
                        "Fix it, restore it, repurpose it, and give things another life.",
                        "♻️",
                        null
                );
                subforumRepository.save(repair);
        }
}
}
