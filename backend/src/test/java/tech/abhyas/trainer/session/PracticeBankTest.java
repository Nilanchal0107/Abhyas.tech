package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Placeholder filling, skill matching and the personal-text check that keeps one user's details out of the shared bank (ticket 17). */
class PracticeBankTest {
    static final List<String> HIDDEN = List.of("Order Service", "Acme Corp", "Asha Rao", "Built a payment gateway integration with retries for failed orders");

    @Test
    void placeholdersAreFilledFromTheResumeAndAnEmptyOneRejectsTheTemplate() {
        assertThat(PracticeBank.fillPlaceholders("In {project} you used {skill}. Why?", "Order Service", "Redis", null, null, null)).isEqualTo("In Order Service you used Redis. Why?");
        assertThat(PracticeBank.fillPlaceholders("At {company} you wrote: '{bullet}'. How did you measure it?", null, null, "Acme", "Cut latency 40%", null))
                .isEqualTo("At Acme you wrote: 'Cut latency 40%'. How did you measure it?");
        assertThat(PracticeBank.fillPlaceholders("Tell me about {theme} on {project}.", null, null, null, null, "deadline pressure")).isNull(); // no project to name
        assertThat(PracticeBank.fillPlaceholders("No placeholders here.", null, null, null, null, null)).isEqualTo("No placeholders here.");
    }

    @Test
    void skillsAreMatchedAsWholeWordsIgnoringCase() {
        assertThat(PracticeBank.mentions("Built REST APIs with Spring Boot and MySQL", "spring boot")).isEqualTo(1);
        assertThat(PracticeBank.mentions("Wrote JavaScript tests", "Java")).isZero();
        assertThat(PracticeBank.mentions("Used C++ and C#", "C++")).isEqualTo(1);
        assertThat(PracticeBank.mentions("Redis, redis and REDIS", "Redis")).isEqualTo(3);
    }

    @Test
    void aQuestionWithAnyPersonalTextIsNotSaved() {
        assertThat(PracticeBank.personal(HIDDEN, List.of("In Order Service you used Redis. Why?"))).isTrue(); // project name
        assertThat(PracticeBank.personal(HIDDEN, List.of("At acme corp, which cache did you pick?"))).isTrue(); // company, any case
        assertThat(PracticeBank.personal(HIDDEN, List.of("Is Asha Rao's approach to Redis right?"))).isTrue(); // the user's name
        assertThat(PracticeBank.personal(HIDDEN, List.of("You built a payment gateway integration with retries, why?"))).isTrue(); // a run of words from a bullet
        assertThat(PracticeBank.personal(HIDDEN, List.of("Which", "[{\"key\":\"A\",\"text\":\"Order Service\"}]"))).isTrue(); // inside the options too
        assertThat(PracticeBank.personal(HIDDEN, List.of("Which Redis command sets a key with a time to live?"))).isFalse();
        assertThat(PracticeBank.personal(HIDDEN, List.of("What does a payment gateway do?"))).isFalse(); // a few shared words are fine
    }
}
