package tech.abhyas.trainer.resume;

import java.io.IOException;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeParser parser;

    public ResumeController(ResumeParser parser) { this.parser = parser; }

    @PostMapping("/parse-fallback")
    Map<String, Object> parseFallback(@RequestParam("file") MultipartFile file) throws IOException {
        return Map.of("resume", parser.parse(file.getBytes()));
    }
}
